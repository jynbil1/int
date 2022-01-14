package hanpoom.internal_cron.crons.dashboard.dhl.cron;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hanpoom.internal_cron.crons.dashboard.dhl.service.DHLService;

@Component
public class DHLCron {

    private DHLService dHLService;

    public DHLCron(DHLService dHLService) {
        this.dHLService = dHLService;
    }


    // Runs when the Delivery Operations are ended up on the 6 PM in the evening in US CA. (GMT-8)
    // 미국 CA (GMT-8) 오후 6 시 이후, 대부분의 배달 작업이 끝나는 시간대를 고려하여 스케줄러를 수행
    @Scheduled(cron = "0 0 11 * * TUE-SAT", zone = "Asia/Seoul")
    public void cronJobSlackerDashboard() {
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 10");

        // Date now = new Date();
        // String strDate = sdf.format(now);
        // System.out.println("Java cron job expression:: " + strDate);

        // String notificationMessage = "***" + strDate + "시 리포팅 ***"
        //         + "\n전일 매출: " + dashboardService.getYesterdayRevenue()
        //         + "\n신규 가입자: " + dashboardService.getNewCustomers()
        //         + "\n금년 총 매출: " + dashboardService.getCurrentYearRevenue()
        //         + "\n누적 회원: " + dashboardService.getTotalCustomers();
        // System.out.println(notificationMessage);
        // boolean isSent = new SlackService().sendNotification(notificationMessage);
        // if (!isSent) {
        //     isSent = new SlackService().sendNotification(notificationMessage);
        //     if (!isSent) {
        //         System.out.println("결국 실패했습니다.");
        //     }
        // } else {
        //     System.out.println("슬랙 알림 오케이.");
        // }
    }

}
