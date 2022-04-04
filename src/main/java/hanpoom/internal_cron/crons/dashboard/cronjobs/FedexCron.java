package hanpoom.internal_cron.crons.dashboard.cronjobs;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hanpoom.internal_cron.api.shipment.fedex.manager.FedexTrackManager;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse.TrackResult;
import hanpoom.internal_cron.api.slack.SlackAPI;
import hanpoom.internal_cron.crons.dashboard.fedex.service.FedexService;
import hanpoom.internal_cron.crons.dashboard.fedex.vo.OrderShipment;
import hanpoom.internal_cron.utility.group.Grouping;

@Component
public class FedexCron {

    // private static final String FEDEX_SLACK_ALARM_URL =
    // "https://hooks.slack.com/services/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";
    private static final String FEDEX_SLACK_ALARM_URL = "https://hooks.slack.com/services/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";
    @Autowired
    private FedexService fedexService;

    @Autowired
    private FedexTrackManager fedexTrackManager;

    @Autowired
    private SlackAPI slack;

    @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Seoul")
    public void cronFedexShipmentTrack() {

        // 값 유형별로 처리할 것.
        List<FedexTrackResponse> deliveredOrders = new ArrayList<>();
        List<FedexTrackResponse> delayedOrders = new ArrayList<>();
        List<FedexTrackResponse> issueOrders = new ArrayList<>();
        List<FedexTrackResponse> untrackableOrders = new ArrayList<>();
        List<FedexTrackResponse> returnedOrders = new ArrayList<>();
        List<FedexTrackResponse> otherIssueOrders = new ArrayList<>();
        int inTransitOrders = 0;

        // 1. 발송된 데이터 추출.
        List<OrderShipment> orderShipments = fedexService.getShippedFedexOrders();

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
                for (FedexTrackResponse response : responses) {
                    TrackResult result = response.getTrackResults().get(0);
                    if (fedexTrackManager.isDelivered(result)) {
                        deliveredOrders.add(response);
                    } else if (fedexTrackManager.isDelayed(result)) {
                        delayedOrders.add(response);
                    } else if (fedexTrackManager.isProblematic(result)) {
                        issueOrders.add(response);
                    } else if (fedexTrackManager.isReturned(result)) {
                        returnedOrders.add(response);
                    } else if (fedexTrackManager.isNotFound(result)) {
                        untrackableOrders.add(response);
                    } else if (fedexTrackManager.isInTransit(result)) {
                        ++inTransitOrders;
                    } else {
                        // 알수 없음.
                        System.out.println("====================================");
                        System.out.println(response.getTrackingNumber());
                        System.out.println("알수 없는 건을 조회했습니다.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. 문제건 시트 기재

        // 4. 배송 완료/미완료 건 DB 저장

        // 5. 슬랙 알림.
        Map<String, String> workResult = Map.of(
                "delivered",
                deliveredOrders.isEmpty() ? "0" : NumberFormat.getInstance().format(deliveredOrders.size()),
                "delayed", delayedOrders.isEmpty() ? "0" : NumberFormat.getInstance().format(delayedOrders.size()),
                "untrackable",
                untrackableOrders.isEmpty() ? "0" : NumberFormat.getInstance().format(untrackableOrders.size()),
                "others", otherIssueOrders.isEmpty() ? "0" : NumberFormat.getInstance().format(otherIssueOrders.size()),
                "returned", returnedOrders.isEmpty() ? "0" : NumberFormat.getInstance().format(returnedOrders.size()),
                "inTransit", NumberFormat.getInstance().format(inTransitOrders));

        try {
            slack.sendMessage(fedexService.getTrackReportMsg(workResult), FEDEX_SLACK_ALARM_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
