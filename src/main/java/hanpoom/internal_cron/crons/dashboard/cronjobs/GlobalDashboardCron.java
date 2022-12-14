package hanpoom.internal_cron.crons.dashboard.cronjobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hanpoom.internal_cron.crons.dashboard.spreadsheet.service.GlobalDashboardService;

@Component
public class GlobalDashboardCron {

    @Autowired
    private GlobalDashboardService dashboard;

    // "0 0 * * * *" = the top of every hour of every day.
    // "* * * * * *" = 매초 실행 합니다.
    // "*/10 * * * * *" = 매 10초마다 실행한다.
    // 0 */1 * * * = 매시간 실행 합니다.
    // "0 0 8-10 * * *" = 매일 8, 9, 10시에 실행한다
    // "0 0 6,19 * * *" = 매일 오전 6시, 오후 7시에 실행한다.
    // "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30, 10:00 and 10:30 every day.
    // "0 0 9-17 * * MON-FRI" = 오전 9시부터 오후 5시까지 주중(월~금)에 실행한다.
    // "0 0 0 25 12 ?" = every Christmas Day at midnight

    // 매일 월요일일때만 -> 매주 월요일 자정이 되면 수행.
    @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Seoul")
    public void cronJobGloabalWeeklyDashboard() {
        dashboard.reportWeeklyGobalDashboard();

    }
}
