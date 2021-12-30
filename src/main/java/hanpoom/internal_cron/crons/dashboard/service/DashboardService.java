package hanpoom.internal_cron.crons.dashboard.service;

import org.springframework.stereotype.Service;

import hanpoom.internal_cron.crons.dashboard.mapper.DashboardMapper;

@Service
public class DashboardService {
    private DashboardMapper dashboardMapper;

    public DashboardService(DashboardMapper dashboardMapper) {
        this.dashboardMapper = dashboardMapper;
    }

    public String getYesterdayRevenue(){
        String yesterdayRevenue;
        try {
            yesterdayRevenue = "$" + dashboardMapper.getYesterdayRevenue();
        } catch (Exception e) {
            yesterdayRevenue = "집계에 실패했습니다.";
            e.printStackTrace();
        }
        return yesterdayRevenue;
    }

    public String getCurrentYearRevenue(){
        String currentYearRevenue;
        try {
            currentYearRevenue = "$" + dashboardMapper.getCurrentYearRevenue();
        } catch (Exception e) {
            currentYearRevenue = "집계에 실패했습니다.";
            e.printStackTrace();
        }
        return currentYearRevenue;
    }

    public String getNewCustomers(){
        String newCustomers;
        try {
            newCustomers = dashboardMapper.getNewCustomers() + " 명";
        } catch (Exception e) {
            newCustomers = "집계에 실패했습니다.";
            e.printStackTrace();
        }
        return newCustomers;
    }

    public String getTotalCustomers(){
        String totalCustomers;
        try {
            totalCustomers = dashboardMapper.getTotalCustomers() + " 명";
        } catch (Exception e) {
            totalCustomers = "집계에 실패했습니다.";
            e.printStackTrace();
        }
        return totalCustomers;
    }
}
