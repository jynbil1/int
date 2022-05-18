package hanpoom.internal_cron.crons.dashboard.slack.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hanpoom.internal_cron.crons.dashboard.slack.service.DashboardService;
import hanpoom.internal_cron.utility.slack.service.SlackService;

@RestController
public class SlackTestController {
    private DashboardService dashboardService;

    public SlackTestController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // @GetMapping("/slacker")
    // public void slackerTest() {

    // // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 10");

    // Date now = new Date();
    // String strDate = sdf.format(now);
    // System.out.println("Java cron job expression:: " + strDate);

    // String notificationMessage = "***" + strDate + "시 리포팅 ***"
    // + "\n전일 매출: " + dashboardService.getYesterdayRevenue()
    // + "\n신규 가입자: " + dashboardService.getNewCustomers()
    // + "\n전일 주문건: " + dashboardService.getNewOrders()
    // + "\n금년 총 매출: " + dashboardService.getCurrentYearRevenue()
    // + "\n누적 회원: " + dashboardService.getTotalCustomers();
    // System.out.println(notificationMessage);
    // boolean isSent = new SlackService().sendNotification(notificationMessage);
    // if (!isSent) {
    // isSent = new SlackService().sendNotification(notificationMessage);
    // if (!isSent) {
    // System.out.println("결국 실패했습니다.");
    // }
    // } else {
    // System.out.println("슬랙 알림 오케이.");
    // }
    // }
    // @GetMapping("/slack/one")
    public void one() {
        dashboardService.reportRevenueDashboard();
    }

    // @GetMapping("slack/two")
    public void two() {
        dashboardService.reportNewUsersDashboard();
    }

    // @GetMapping("slack/three")
    public void three() {
        dashboardService.reportLastMonthNewUserAchievement();
    }

}
