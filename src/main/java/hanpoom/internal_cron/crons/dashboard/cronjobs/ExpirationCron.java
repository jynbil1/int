package hanpoom.internal_cron.crons.dashboard.cronjobs;

import hanpoom.internal_cron.crons.dashboard.slack.service.DashboardService;
import hanpoom.internal_cron.crons.dashboard.slack.service.ExpirationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpirationCron {

    private ExpirationService expirationService;

    public ExpirationCron(ExpirationService expirationService) {
        this.expirationService = expirationService;
    }

    // 매일 오전 10시 유통기한 손실금액 알림
    @Scheduled(cron = "0 0 10 * * *", zone = "Asia/Seoul") //fixedDelay=10000(10초 단위)
    public void expirationManagementLossCron() {
        expirationService.reportExpirationManagementLoss();
    }

    // 매일 오전 9시 유통기한 임박 알림(8시 50분으로 설정)
    @Scheduled(cron = "0 50 8 * * *", zone = "Asia/Seoul")
    public void expirationManagementImminentCron(){
        expirationService.reportExpirationManagementImminent();
    }

}
