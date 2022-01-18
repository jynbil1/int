package hanpoom.internal_cron.crons.dashboard.dhl.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DHLTrackingResult {
    private int totalDeliveries;
    private int totalCustomsIssues;
    private int totalOtherIssues;
    private int totalDelays;
}
