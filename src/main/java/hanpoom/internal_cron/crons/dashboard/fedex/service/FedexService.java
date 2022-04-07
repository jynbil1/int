package hanpoom.internal_cron.crons.dashboard.fedex.service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.api.shipment.fedex.enumerate.FedexShipmentStatus;
import hanpoom.internal_cron.api.shipment.fedex.manager.FedexTrackManager;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse.LatestStatusDetail;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse.TrackResult;
import hanpoom.internal_cron.api.slack.SlackAPI;
import hanpoom.internal_cron.crons.dashboard.fedex.mapper.FedexMapper;
import hanpoom.internal_cron.crons.dashboard.fedex.vo.FedexExcelRow;
import hanpoom.internal_cron.crons.dashboard.fedex.vo.OrderShipment;
import hanpoom.internal_cron.utility.calendar.CalendarFormatter;
import hanpoom.internal_cron.utility.group.Grouping;

@Service
public class FedexService {
    // Live Slack URL
    private static final String FEDEX_SLACK_ALARM_URL = "https://hooks.slack.com/services/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";

    // Test Slack URL
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
                .append(" 건\n 출고 이전: ")
                .append(workResult.get("beforePickup"))

                // .append(" 건\n\n 반송 완료: ")
                // .append(workResult.get("returned"))

                .append(" 건\n---------------------------------------------------\n")
                .append("배송중: ")
                .append(workResult.get("inTransit"))
                .append(" 건\n")
                .append("<https://docs.google.com/spreadsheets/d/1G3Y2CWeYveB2KNVRduKTSgFZuOIh7Cb8JQZOO0gBDqw/edit#gid=1386751274|문제 보러가기>");

        return sb.toString();
    }

    public void monitorNReportFedexShipments() {
        // 값 유형별로 처리할 것.
        int deliveredOrders = 0;
        int delayedOrders = 0;
        int issueOrders = 0;
        int untrackableOrders = 0;
        int returnedOrders = 0;
        int otherIssueOrders = 0;
        int beforePickupOrders = 0;
        int inTransitOrders = 0;

        boolean areShipmentsMonitored = false;

        // 1. 발송된 데이터 추출. -->
        List<OrderShipment> orderShipments = getShippedFedexOrders();
        List<OrderShipment> errorShipments = new ArrayList<>();
        List<OrderShipment> deliveredShipments = new ArrayList<>();

        // 추출된 데이터가 없으면 해당 메소드를 수행하지 않음.
        if (orderShipments.isEmpty() || orderShipments.size() < 1) {
            return;
        }

        // 1.5. 한번에 요청할 수 있는 수가 있으니 30개씩만 요청할 것.
        List<List<OrderShipment>> orderShipmentSets = new Grouping<OrderShipment>().groupByNumberSet(orderShipments,
                30);

        // 2. 배송 완료 파악 (tss -> Trackable Shipment Set)
        HashSet<String> trackingNos = new HashSet<>();

        try {

            for (List<OrderShipment> tss : orderShipmentSets) {
                // tss 객체에서 운송장 번호만 가져와서 List<String> 으로 구현함.
                trackingNos = new HashSet<>(tss.stream().map(obj -> obj.getTrackingNo()).collect(Collectors.toList()));

                List<FedexTrackResponse> responses = fedexTrackManager.trackMultipleShipments(trackingNos, true);
                trackingNos = new HashSet<>();

                // 2.1 문제 여부 파악

                for (FedexTrackResponse response : responses) {
                    String issueType = "";
                    String event = "";
                    String eventCode = "";

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
                        areShipmentsMonitored = true;
                        deliveredShipments.add(selectedOrder);
                    } else {
                        if (fedexTrackManager.isDelayed(result)) {
                            LatestStatusDetail latestEvent = fedexTrackManager.getRecentEvent(result);
                            issueType = "지연";
                            eventCode = latestEvent.getCode();
                            event = latestEvent.getDescription();

                            ++delayedOrders;
                            areShipmentsMonitored = true;
                        } else if (fedexTrackManager.isBeforePickUp(result)) {
                            ++beforePickupOrders;

                            // } else if (fedexTrackManager.isProblematic(result)) {
                            // issueType = "문제";
                            // eventCode = "PRB";
                            // // event =
                            // //
                            // result.getDateAndTimes().stream().sorted(Comparator.comparing(DateAndTime::getDateTime).reversed()).findFirst().get().get;

                            // areShipmentsMonitored = true;
                            // ++issueOrders;
                            // eventDate = fedexTrackManager.getEventDateTime(result, FedexShipmentStatus.);

                            // } else if (fedexTrackManager.isReturned(result)) {
                            // issueType = "반송";
                            // eventCode = "DLY";
                            // event = "Shipment is delayed than expected.";

                            // areShipmentsMonitored = true;
                            // ++returnedOrders;
                        } else if (fedexTrackManager.isNotFound(result)) {
                            issueType = "찾을 수 없음";
                            eventCode = "NF";
                            event = "Tracking Number is not Found.";

                            areShipmentsMonitored = true;
                            ++untrackableOrders;
                        } else {
                            ++inTransitOrders;
                            continue;
                        }

                        selectedOrder.setShippedDate(
                                fedexTrackManager.getEventDateTime(result, FedexShipmentStatus.SHIPPED));
                        selectedOrder.setEvent(event);
                        selectedOrder.setEventCode(eventCode);
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
            fedexSpreadSheet.insertIntoFedexSheet(errorShipments);
            insertErrorShipments(errorShipments);
        }

        // 5. 슬랙 알림.
        Map<String, String> workResult = Map.of(
                "delivered", NumberFormat.getInstance().format(deliveredOrders),
                "delayed", NumberFormat.getInstance().format(delayedOrders),
                "untrackable", NumberFormat.getInstance().format(untrackableOrders),
                "beforePickup", NumberFormat.getInstance().format(beforePickupOrders),
                // "others", NumberFormat.getInstance().format(otherIssueOrders),
                // "returned", NumberFormat.getInstance().format(returnedOrders),
                "inTransit", NumberFormat.getInstance().format(inTransitOrders));

        try {
            // 운송물들이 '운송중' 외의 상태일 경우에는 결과를 안내한다.
            if (areShipmentsMonitored) {
                slack.sendMessage(getTrackReportMsg(workResult), FEDEX_SLACK_ALARM_URL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reMonitorFedexIssueShipments() {
        // 1. 통합 시트_CX - Fedex 에 있는 미처리 값을 불러온다.
        List<FedexExcelRow> rows = fedexSpreadSheet.readUnresolvedShipmentExcel();

        // 2. 해당 값들이 현재 DB 에 저장된 값들과 동일한 값들인지 확인한다.
        List<OrderShipment> orderShipments = getShipments(
                rows.stream()
                        .map(key -> key.getOrderNo().getValue())
                        .collect(Collectors.toList()));

        // 운송장 번호가 변경된 값으로, 다시 업데이트를 해야하는 건들을 필터링.
        List<FedexExcelRow> updatableRows = new ArrayList<>();
        for (FedexExcelRow row : rows) {
            // 운송장 번호는 다른데, 주문번호가 같은게 있으면 true
            try {
                OrderShipment shipment = orderShipments.stream()
                        .filter(obj -> !obj.getTrackingNo().equals(row.getTrackingNo().getValue()) &&
                                obj.getOrderNo() == row.getOrderNo().getValue())
                        .findFirst().get();

                row.getTrackingNo().setValue(shipment.getTrackingNo());
                updatableRows.add(row);
            } catch (NoSuchElementException nee) {
                continue;
            }
        }

        // 3. 동일하지 않을 경우, DB 값을 해당 엑셀 시트에 갱신한다.
        if (!updatableRows.isEmpty()) {
            fedexSpreadSheet.insertNewTrackingNumbers(updatableRows);
        }

        // 4. 갱신된 값들을 기준으로 Fedex 운송장 조회를 한다.
        List<OrderShipment> deliveredShipments = new ArrayList<>();
        if (!orderShipments.isEmpty() && orderShipments.size() > 0) {
            List<List<OrderShipment>> shipmentsSet = new Grouping<OrderShipment>().groupByNumberSet(orderShipments, 30);

            for (List<OrderShipment> shipments : shipmentsSet) {
                // shipments 가 가진 값의 운송장 번호를 리스트로 만들어서 매개 변수로 넣는다.
                List<FedexTrackResponse> responses = fedexTrackManager.trackMultipleShipments(new HashSet<String>(
                        shipments
                                .stream()
                                .map(obj -> obj.getTrackingNo())
                                .collect(Collectors.toList())),
                        false);

                for (FedexTrackResponse response : responses) {
                    if (fedexTrackManager.isDelivered(response.getTrackResults().get(0))) {
                        // 5. 완료된 값들은
                        // 5.1. 스프레드시트에서 완료 처리를 하고,
                        try {
                            FedexExcelRow selectedRow = rows
                                    .stream()
                                    .filter(object -> object.getTrackingNo().getValue()
                                            .equals(response.getTrackingNumber()))
                                    .findFirst()
                                    .get();

                            selectedRow.getIsCompleted().setValue(true);
                            selectedRow.getDeliveredAt().setValue(
                                    (fedexTrackManager.getEventDateTime(
                                            response.getTrackResults().get(0), FedexShipmentStatus.DELIVERED).format(
                                                    DateTimeFormatter.ofPattern(CalendarFormatter.DATETIME))));
                            fedexSpreadSheet.checkNRecordShipment(selectedRow);

                            for (OrderShipment shipment: shipments){
                                if (response.getTrackingNumber().equals(shipment.getTrackingNo())){

                                    TrackResult shipResponse = response.getTrackResults().get(0);
                                    shipment.setEvent("배송완료");
                                    shipment.setEventCode("OK");

                                    shipment.setShippedDate(
                                        fedexTrackManager.getEventDateTime(shipResponse, FedexShipmentStatus.SHIPPED)
                                    );
                                    shipment.setEventDate(
                                        fedexTrackManager.getEventDateTime(shipResponse, FedexShipmentStatus.DELIVERED)
                                    );
                                    deliveredShipments.add(shipment);
                                }
                            }
                           
                        } catch (NoSuchElementException nee) {
                            continue;
                        }

                    }
                }
            }
        }

        // 5.2. us_ca_a_wh_delivered 에 값을 기입한다.
        if (!deliveredShipments.isEmpty() && deliveredShipments.size() > 0) {
            insertDeliveredShipments(deliveredShipments);
        }
    }
}
