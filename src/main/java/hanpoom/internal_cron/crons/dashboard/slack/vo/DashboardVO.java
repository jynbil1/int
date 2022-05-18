package hanpoom.internal_cron.crons.dashboard.slack.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DashboardVO {
    
    private double yesterdayRevenue;
    private double currentYearRevenue;
    private int newCustomers;
    private int totalCustomers;  
}
