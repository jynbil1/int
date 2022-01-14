package hanpoom.internal_cron.crons.dashboard.dhl.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DHLVO {
    private String order_no;
    private String tracking_no;
}
