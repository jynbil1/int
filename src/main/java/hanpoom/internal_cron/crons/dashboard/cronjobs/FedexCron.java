package hanpoom.internal_cron.crons.dashboard.cronjobs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hanpoom.internal_cron.api.shipment.fedex.manager.FedexTrackManager;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse;
import hanpoom.internal_cron.crons.dashboard.fedex.service.FedexService;
import hanpoom.internal_cron.crons.dashboard.fedex.vo.OrderShipment;
import hanpoom.internal_cron.utility.group.Grouping;

@Component
public class FedexCron {
    
    private static final String FEDEX_SLACK_ARALM_URL = "https://hooks.slack.com/services/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";

    @Autowired
    private FedexService fedexService;

    @Autowired
    private FedexTrackManager fedexTrackManager;

    
    @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Seoul")
    public void cronFedexShipmentTrack(){

        // 값 유형별로 처리할 것.
        List<FedexTrackResponse> deliveredOrders = new ArrayList<>();
        List<FedexTrackResponse> delayedOrders = new ArrayList<>();
        List<FedexTrackResponse> issueOrders = new ArrayList<>();
        List<FedexTrackResponse> untrackableOrders = new ArrayList<>();
        List<FedexTrackResponse> returnedOrders = new ArrayList<>();

        // 1. 발송된 데이터 추출.
        List<OrderShipment> orderShipments = fedexService.getShippedFedexOrders();

        // 1.5. 한번에 요청할 수 있는 수가 있으니 30개씩만 요청할 것.
        List<List<OrderShipment>> orderShipmentSets = new Grouping<OrderShipment>().groupByNumberSet(orderShipments, 30);

        // 2. 배송 완료 파악 (tss -> Trackable Shipment Set)
        HashSet<String> trackingNos = new HashSet<>();
        for (List<OrderShipment> tss: orderShipmentSets) {
            tss.stream().filter(obj -> obj.getTrackingNo());
            List<FedexTrackResponse> responses = fedexTrackManager.trackMultipleShipments(trackingNos, false);
            trackingNos = new HashSet<>();
            
            // 2.1 문제 여부 파악
            for (FedexTrackResponse response: responses) {
                if (fedexTrackManager.isDelivered(response.getTrackResults().get(0))){
                    deliveredOrders.add(response);
                }
            }
            // 3. 배송 완료 건 DB 저장
            // 4. 배송 미완료 패스
        }
        
        // 5. 문제건 시트 기재
        // 6. 슬랙 알림.

    }
} 
