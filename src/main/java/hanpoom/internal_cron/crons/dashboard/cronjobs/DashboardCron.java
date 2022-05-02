package hanpoom.internal_cron.crons.dashboard.cronjobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hanpoom.internal_cron.crons.dashboard.slack.service.DashboardService;

@Component
public class DashboardCron {

    private DashboardService dashboardService;

    public DashboardCron(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // 매일 10시
    @Scheduled(cron = "0 0 10 * * *", zone = "Asia/Seoul")
    public void revenueDashboardCron() {
        dashboardService.reportRevenueDashboard();
    }

    // 매일 10시
    @Scheduled(cron = "0 0 10 * * *", zone = "Asia/Seoul")
    public void newUserDashboardCron() {
        dashboardService.reportNewUsersDashboard();
    }

    // 매달 1일
    @Scheduled(cron = "0 0 0 1 * *", zone = "Asia/Seoul")
    public void lastMonthNewUserAchievements(){
        dashboardService.reportLastMonthNewUserAchievement();
    }

}
