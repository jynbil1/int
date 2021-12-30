package hanpoom.internal_cron.crons.dashboard.cron;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hanpoom.internal_cron.crons.dashboard.service.DashboardService;
import hanpoom.internal_cron.utilities.slack.service.SlackService;

@Component
public class DashboardCron {

    private DashboardService dashboardService;

    public DashboardCron(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Scheduled(cron = "0 45 12 * * *", zone = "Asia/Seoul")
    public void cronJobSch() {
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 10");

        Date now = new Date();
        String strDate = sdf.format(now);
        // System.out.println("Java cron job expression:: " + strDate);

        String notificationMessage = "***" + strDate + "시 리포팅 ***" 
                + "\n전일 매출: " + dashboardService.getYesterdayRevenue()
                + "\n신규 가입자: " + dashboardService.getNewCustomers()
                + "\n금년 총 매출: " + dashboardService.getCurrentYearRevenue()
                + "\n누적 회원: " + dashboardService.getTotalCustomers();
        
        boolean isSent = new SlackService().sendNotification(notificationMessage);
        if (!isSent) {
            isSent = new SlackService().sendNotification(notificationMessage);
            if (!isSent) {
                System.out.println("결국 실패했습니다.");
            }
        } else {
            System.out.println("슬랙 알림 오케이.");
        }
    }

    // @Scheduled(fixedDelay = 1000)
    // public void scheduleFixedRateTask() {
    // System.out.println(
    // "Fixed rate task - " + System.currentTimeMillis() / 1000);
    // }
}
