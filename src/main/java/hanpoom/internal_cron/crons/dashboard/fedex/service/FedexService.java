package hanpoom.internal_cron.crons.dashboard.fedex.service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.api.shipment.fedex.enumerate.FedexShipDuration;
import hanpoom.internal_cron.api.shipment.fedex.enumerate.FedexShipmentStatus;
import hanpoom.internal_cron.api.shipment.fedex.manager.FedexTrackManager;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse.TrackResult;
import hanpoom.internal_cron.api.slack.SlackAPI;
import hanpoom.internal_cron.crons.dashboard.fedex.mapper.FedexMapper;
import hanpoom.internal_cron.crons.dashboard.fedex.vo.OrderShipment;
import hanpoom.internal_cron.utility.calendar.CalendarFormatter;
import hanpoom.internal_cron.utility.group.Grouping;
import hanpoom.internal_cron.utility.spreadsheet.vo.UpdateSheetVO;

@Service
public class FedexService {
    // Live Slack URL
    private static final String FEDEX_SLACK_ALARM_URL = "https://hooks.slack.com/services/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";
    // private static final String FEDEX_SLACK_ALARM_URL =
    // "https://hooks.slack.com/services/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";

    @Autowired
    private FedexSpreadSheet fedexSpreadSheet;
    @Autowired
    private FedexTrackManager fedexTrackManager;
    @Autowired
    private SlackAPI slack;

    @Autowired
    private FedexMapper fedexMapper;

    private List<OrderShipment> getShippedFedexOrders() {
        try {
            return fedexMapper.getOrderShipments();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private List<OrderShipment> getShipments(List<Integer> orderNos) {
        try {
            return fedexMapper.getShipments(orderNos);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void insertDeliveredShipments(List<OrderShipment> orders) {
        try {
            fedexMapper.insertDeliveredShipments(orders);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void insertErrorShipments(List<OrderShipment> orders) {
        try {
            fedexMapper.insertErrorShipments(orders);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String getTrackReportMsg(Map<String, String> workResult) {
        StringBuilder sb = new StringBuilder();
        sb
                .append("***")
                .append(CalendarFormatter.toKoreanDate(LocalDate.now()))
                .append(" ")
                .append(String.valueOf(LocalDateTime.now().getHour()))
                .append("시 Fedex 배송 현황***\n")
                .append("---------------------------------------------------\n")
                .append("배송 완료: ")
                .append(workResult.get("delivered"))
                .append(" 건\n\n 배송 지연: ")
                .append(workResult.get("delayed"))
                .append(" 건\n 조회 불가: ")
                .append(workResult.get("untrackable"))
                .append(" 건\n 이외 문제: ")
                .append(workResult.get("others"))

                .append(" 건\n\n 반송 완료: ")
                .append(workResult.get("returned"))

                .append(" 건\n---------------------------------------------------\n")
                .append("배송중: ")
                .append(workResult.get("inTransit"))
                .append(" 건\n")
                .append("<https://docs.google.com/spreadsheets/d/1G3Y2CWeYveB2KNVRduKTSgFZuOIh7Cb8JQZOO0gBDqw/edit#gid=1386751274|문제 보러가기>");

        return sb.toString();
    }

    public void monitorNReportFedexShipment() {
        // 값 유형별로 처리할 것.
        int deliveredOrders = 0;
        int delayedOrders = 0;
        int issueOrders = 0;
        int untrackableOrders = 0;
        int returnedOrders = 0;
        int otherIssueOrders = 0;
        int inTransitOrders = 0;

        // 1. 발송된 데이터 추출. -->
        List<OrderShipment> orderShipments = getShippedFedexOrders();

        if (orderShipments.isEmpty()) {
            return;
        }

        List<OrderShipment> errorShipments = new ArrayList<>();
        List<OrderShipment> deliveredShipments = new ArrayList<>();

        // 1.5. 한번에 요청할 수 있는 수가 있으니 30개씩만 요청할 것.
        List<List<OrderShipment>> orderShipmentSets = new Grouping<OrderShipment>().groupByNumberSet(orderShipments,
                30);

        // 2. 배송 완료 파악 (tss -> Trackable Shipment Set)
        HashSet<String> trackingNos = new HashSet<>();

        try {

            for (List<OrderShipment> tss : orderShipmentSets) {
                // tss 객체에서 운송장 번호만 가져와서 List<String> 으로 구현함.
                trackingNos = new HashSet<>(tss.stream().map(obj -> obj.getTrackingNo()).collect(Collectors.toList()));

                List<FedexTrackResponse> responses = fedexTrackManager.trackMultipleShipments(trackingNos, false);
                trackingNos = new HashSet<>();

                // 2.1 문제 여부 파악
                String issueType = "";
                for (FedexTrackResponse response : responses) {
                    LocalDateTime eventDate = LocalDateTime.now();
                    TrackResult result = response.getTrackResults().get(0);

                    OrderShipment selectedOrder = orderShipments
                            .stream()
                            .filter(key -> response.getTrackingNumber().equals(key.getTrackingNo()))
                            .findFirst()
                            .get();

                    if (fedexTrackManager.isDelivered(result)) {
                        ++deliveredOrders;
                        selectedOrder.setShippedDate(
                                fedexTrackManager.getEventDateTime(result, FedexShipmentStatus.SHIPPED));
                        selectedOrder.setEventDate(
                                fedexTrackManager.getEventDateTime(result, FedexShipmentStatus.DELIVERED));
                        selectedOrder.setEvent("배송완료");
                        selectedOrder.setEventCode("OK");
                        deliveredShipments.add(selectedOrder);
                    } else {
                        if (fedexTrackManager.isDelayed(result,
                                FedexShipDuration.findByServiceType(selectedOrder.getServiceType()))) {
                            issueType = "지연";
                            ++delayedOrders;
                        } else if (fedexTrackManager.isProblematic(result)) {
                            issueType = "문제";
                            ++issueOrders;
                            // eventDate = fedexTrackManager.getEventDateTime(result, FedexShipmentStatus.);

                        } else if (fedexTrackManager.isReturned(result)) {
                            issueType = "반송";
                            ++returnedOrders;
                        } else if (fedexTrackManager.isNotFound(result)) {
                            issueType = "찾을 수 없음";
                            ++untrackableOrders;
                        } else {
                            ++inTransitOrders;
                            continue;
                        }

                        selectedOrder.setShippedDate(
                                fedexTrackManager.getEventDateTime(result, FedexShipmentStatus.SHIPPED));
                        selectedOrder.setIssueType(issueType);
                        selectedOrder.setEventDate(eventDate);
                        errorShipments.add(selectedOrder);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. 배송 완료/미완료 건 DB 저장
        if (!deliveredShipments.isEmpty()) {
            insertDeliveredShipments(deliveredShipments);
        }

        // 4. 문제건 처리
        if (!errorShipments.isEmpty()) {
            UpdateSheetVO updateResult = fedexSpreadSheet.insertIntoFedexSheet(errorShipments);
            insertErrorShipments(errorShipments);
        }

        try {
            if (!orderShipments.isEmpty() && orderShipments.size() > 0) {
                // 5. 슬랙 알림.
                Map<String, String> workResult = Map.of(
                        "delivered", NumberFormat.getInstance().format(deliveredOrders),
                        "delayed", NumberFormat.getInstance().format(delayedOrders),
                        "untrackable", NumberFormat.getInstance().format(untrackableOrders),
                        "others", NumberFormat.getInstance().format(otherIssueOrders),
                        "returned", NumberFormat.getInstance().format(returnedOrders),
                        "inTransit", NumberFormat.getInstance().format(inTransitOrders));
                slack.sendMessage(getTrackReportMsg(workResult), FEDEX_SLACK_ALARM_URL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
