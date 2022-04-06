package hanpoom.internal_cron.crons.dashboard.cronjobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hanpoom.internal_cron.crons.dashboard.fedex.service.FedexService;

@Component
public class FedexCron {

    
    private static final String TZ_KOREA = "Asia/Seoul";

    @Autowired
    private FedexService fedexService;

    @Scheduled(cron = "0 0 12 * * *", zone = TZ_KOREA)
    public void fedexShipmentMonitorCron() {
        // @Scheduled(cron = "", zone = TZ_KOREA)
        // public void monitorFedexErrorShipment(){
        // 1. 통합 시트_CX - Fedex 에 있는 미처리 값을 불러온다.
        // 2. 해당 값들이 현재 DB 에 저장된 값들과 동일한 값들인지 확인한다.
        // 3. 동일하지 않을 경우, DB 값을 해당 엑셀 시트에 갱신한다.
        // 4. 갱신된 값들을 기준으로 Fedex 운송장 조회를 한다.
        // 5. 완료된 값들은
        // 5.1. 스프레드시트에서 완료 처리를 하고,
        // 5.2. us_ca_a_wh_delivered 에 값을 기입한다.

        // }
        fedexService.monitorNReportFedexShipment();

    }

}
