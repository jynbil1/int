package hanpoom.internal_cron.crons.dashboard.spreadsheet.service;

import org.springframework.stereotype.Service;

import hanpoom.internal_cron.crons.dashboard.common.mapper.CommonMapper;
import hanpoom.internal_cron.crons.dashboard.common.vo.DateRangeVO;
import hanpoom.internal_cron.crons.dashboard.slack.mapper.DashboardMapper;
import hanpoom.internal_cron.utility.calendar.service.CalendarService;

@Service
public class GlobalDashboardService {
    private CommonMapper commonMapper;
    private CalendarService calendar;

    public GlobalDashboardService(CommonMapper commonMapper, CalendarService calendar) {
        this.commonMapper = commonMapper;
        this.calendar = calendar;
    }

    public String getLastWeekRevenue() {
        String lastWeekRevenue;
        try {
            lastWeekRevenue = commonMapper
                    .getRevenue(new DateRangeVO(calendar.getPreviousWeekMonday(true),
                            calendar.getPreviousWeekSunday(true)));
        } catch (Exception e) {
            lastWeekRevenue = "N/A";
            e.printStackTrace();
        }
        return lastWeekRevenue;
    }

    public String getLastWeekNewUsers() {
        String newUsers;
        try {
            // get the corresponding startdate and enddate
            newUsers = commonMapper.getNewUsers(
                    new DateRangeVO(calendar.getPreviousWeekMonday(true), calendar.getPreviousWeekSunday(true)));
        } catch (Exception e) {
            newUsers = "N/A";
            e.printStackTrace();
        }
        return newUsers;
    }

    public String getLastWeekNewPurchase() {
        String newPurchase;
        try {
            // get the corresponding startdate and enddate
            newPurchase = commonMapper.getRevenue(new DateRangeVO(calendar.getPreviousWeekMonday(true),
                    calendar.getPreviousWeekSunday(true)));
        } catch (Exception e) {
            newPurchase = "N/A";
            e.printStackTrace();
        }
        return newPurchase;
    }

    public String getLastWeekRePurchase() {
        String rePurchase;
        try {
            // get the corresponding startdate and enddate
            rePurchase = commonMapper.getRevenue(new DateRangeVO(calendar.getPreviousWeekMonday(true),
                    calendar.getPreviousWeekSunday(true)));
        } catch (Exception e) {
            rePurchase = "N/A";
            e.printStackTrace();
        }
        return rePurchase;
    }

    public String getLastWeekTotalOrders() {
        String totalOrders;
        try {
            totalOrders = commonMapper.getTotalOrders(new DateRangeVO(calendar.getPreviousWeekMonday(true),
                    calendar.getPreviousWeekSunday(true)));
        } catch (Exception e) {
            totalOrders = "N/A";
            e.printStackTrace();
        }
        return totalOrders;
    }

    public String getLastWeekMargins() {
        String totalMargins;
        try {
            totalMargins = commonMapper.getTotalMargins(new DateRangeVO(calendar.getPreviousWeekMonday(true),
                    calendar.getPreviousWeekSunday(true)));
        } catch (Exception e) {
            totalMargins = "N/A";
            e.printStackTrace();
        }
        return totalMargins;
    }
}
