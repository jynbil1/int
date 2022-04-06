package hanpoom.internal_cron.crons.dashboard.slack.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.stereotype.Service;

import hanpoom.internal_cron.crons.dashboard.common.mapper.CommonMapper;
import hanpoom.internal_cron.crons.dashboard.common.vo.DateRangeVO;
import hanpoom.internal_cron.crons.dashboard.slack.mapper.DashboardMapper;
import hanpoom.internal_cron.utility.calendar.CalendarManager;
import hanpoom.internal_cron.utility.slack.service.SlackService;

@Service
public class DashboardService {
    private DashboardMapper dashboardMapper;
    private CommonMapper commonMapper;
    private CalendarManager calendar;

    public DashboardService(DashboardMapper dashboardMapper, CommonMapper commonMapper, CalendarManager calendar) {
        this.dashboardMapper = dashboardMapper;
        this.commonMapper = commonMapper;
        this.calendar = calendar;
    }

    private String getYesterdayRevenue() {
        String yesterdayRevenue;
        try {
            yesterdayRevenue = "$"
                    + commonMapper.getRevenue(
                            new DateRangeVO(calendar.getStartofYesterday(true), calendar.getEndofYesterday(true)));
        } catch (Exception e) {
            yesterdayRevenue = "집계에 실패했습니다.";
            e.printStackTrace();
        }
        return yesterdayRevenue;
    }

    private String getCurrentYearRevenue() {
        String currentYearRevenue;
        try {
            // get the corresponding startdate and enddate
            int thisYear = LocalDateTime.now().getYear();
            currentYearRevenue = "$"
                    + commonMapper.getRevenue(new DateRangeVO(calendar.getStartOfYear(thisYear, true),
                            calendar.getEndOfYearOpt(thisYear, true)));
        } catch (Exception e) {
            currentYearRevenue = "집계에 실패했습니다.";
            e.printStackTrace();
        }
        return currentYearRevenue;
    }

    private String getNewCustomers() {
        String newCustomers;
        try {
            newCustomers = dashboardMapper.getDailyNewCustomer() + " 명";
        } catch (Exception e) {
            newCustomers = "집계에 실패했습니다.";
            e.printStackTrace();
        }
        return newCustomers;
    }

    private String getTotalCustomers() {
        String totalCustomers;
        try {
            totalCustomers = dashboardMapper.getTotalCustomers() + " 명";
        } catch (Exception e) {
            totalCustomers = "집계에 실패했습니다.";
            e.printStackTrace();
        }
        return totalCustomers;
    }

    private String getNewOrders() {
        String totalOrders = null;
        try {
            totalOrders = commonMapper.getTotalOrders(
                    new DateRangeVO(calendar.getStartofYesterday(true), calendar.getEndofYesterday(true))) + " 건";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalOrders;
    }

    public void reportRevenueDashboard() {
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 10");

        Date now = new Date();
        String strDate = sdf.format(now);
        System.out.println("Java cron job expression:: " + strDate);

        String notificationMessage = "***" + strDate + "시 리포팅 ***"
                + "\n전일 매출: " + getYesterdayRevenue()
                + "\n신규 가입자: " + getNewCustomers()
                + "\n전일 주문건: " + getNewOrders()
                + "\n금년 총 매출: " + getCurrentYearRevenue()
                + "\n누적 회원: " + getTotalCustomers();
        System.out.println(notificationMessage);
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
}
