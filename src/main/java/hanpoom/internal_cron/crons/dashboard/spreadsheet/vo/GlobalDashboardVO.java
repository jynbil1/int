package hanpoom.internal_cron.crons.dashboard.spreadsheet.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GlobalDashboardVO {
    private String new_users;
    private String total_orders;
    private String margins;
}
