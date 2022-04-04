package hanpoom.internal_cron.crons.dashboard.fedex.controller;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hanpoom.internal_cron.api.shipment.fedex.enumerate.FedexShipDuration;
import hanpoom.internal_cron.api.shipment.fedex.enumerate.FedexShipmentStatus;
import hanpoom.internal_cron.api.shipment.fedex.manager.FedexTrackManager;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse.TrackResult;
import hanpoom.internal_cron.api.slack.SlackAPI;
import hanpoom.internal_cron.crons.dashboard.fedex.service.FedexService;
import hanpoom.internal_cron.crons.dashboard.fedex.vo.OrderShipment;
import hanpoom.internal_cron.utility.group.Grouping;
import hanpoom.internal_cron.utility.spreadsheet.vo.UpdateSheetVO;

@RestController
public class FedexTestController {

    // Live Slack URL
    private static final String FEDEX_SLACK_ALARM_URL = "https://hooks.slack.com/services/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";
    // private static final String FEDEX_SLACK_ALARM_URL =
    // "https://hooks.slack.com/services/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";

    @Autowired
    private FedexService fedexService;
    @Autowired
    private FedexTrackManager fedexTrackManager;
    @Autowired
    private SlackAPI slack;

    @GetMapping(value = "/api/fedex/test")
    public void cronFedexShipmentTrack() {

        // 값 유형별로 처리할 것.
        int deliveredOrders = 0;
        int delayedOrders = 0;
        int issueOrders = 0;
        int untrackableOrders = 0;
        int returnedOrders = 0;
        int otherIssueOrders = 0;
        int inTransitOrders = 0;

        // 1. 발송된 데이터 추출. -->
        List<OrderShipment> orderShipments = fedexService.getShippedFedexOrders();
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
            fedexService.insertDeliveredShipments(deliveredShipments);
        }

        // 4. 문제건 처리
        if (!errorShipments.isEmpty()) {
            UpdateSheetVO updateResult = fedexService.insertIntoFedexSheet(errorShipments);
            fedexService.insertErrorShipments(errorShipments);

        }

        // 5. 슬랙 알림.
        Map<String, String> workResult = Map.of(
                "delivered", NumberFormat.getInstance().format(deliveredOrders),
                "delayed", NumberFormat.getInstance().format(delayedOrders),
                "untrackable", NumberFormat.getInstance().format(untrackableOrders),
                "others", NumberFormat.getInstance().format(otherIssueOrders),
                "returned", NumberFormat.getInstance().format(returnedOrders),
                "inTransit", NumberFormat.getInstance().format(inTransitOrders));

        try {
            if (!orderShipments.isEmpty()) {
                slack.sendMessage(fedexService.getTrackReportMsg(workResult), FEDEX_SLACK_ALARM_URL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
