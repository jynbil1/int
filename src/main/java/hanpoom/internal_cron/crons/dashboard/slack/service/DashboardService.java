package hanpoom.internal_cron.crons.dashboard.slack.service;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.crons.dashboard.common.mapper.CommonMapper;
import hanpoom.internal_cron.crons.dashboard.common.vo.DateRangeVO;
import hanpoom.internal_cron.crons.dashboard.slack.mapper.DashboardMapper;
import hanpoom.internal_cron.utility.calendar.service.CalendarService;

@Service
public class DashboardService {
    private DashboardMapper dashboardMapper;
    private CommonMapper commonMapper;
    private CalendarService calendar;

    public DashboardService(DashboardMapper dashboardMapper, CommonMapper commonMapper, CalendarService calendar) {
        this.dashboardMapper = dashboardMapper;
        this.commonMapper = commonMapper;
        this.calendar = calendar;
    }

    public String getYesterdayRevenue() {
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

    public String getCurrentYearRevenue() {
        String currentYearRevenue;
        try {
            // get the corresponding startdate and enddate
            int thisYear = DateTime.now().getYear();
            currentYearRevenue = "$"
                    + commonMapper.getRevenue(new DateRangeVO(calendar.getStartOfYear(thisYear, true),
                            calendar.getEndOfYearOpt(thisYear, true)));
        } catch (Exception e) {
            currentYearRevenue = "집계에 실패했습니다.";
            e.printStackTrace();
        }
        return currentYearRevenue;
    }

    public String getNewCustomers() {
        String newCustomers;
        try {
            newCustomers = dashboardMapper.getDailyNewCustomer() + " 명";
        } catch (Exception e) {
            newCustomers = "집계에 실패했습니다.";
            e.printStackTrace();
        }
        return newCustomers;
    }

    public String getTotalCustomers() {
        String totalCustomers;
        try {
            totalCustomers = dashboardMapper.getTotalCustomers() + " 명";
        } catch (Exception e) {
            totalCustomers = "집계에 실패했습니다.";
            e.printStackTrace();
        }
        return totalCustomers;
    }

    public String getNewOrders() {
        String totalOrders = null;
        try {
            totalOrders = commonMapper.getTotalOrders(
                    new DateRangeVO(calendar.getStartofYesterday(true), calendar.getEndofYesterday(true))) + " 건";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalOrders;
    }
}
