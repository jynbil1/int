package hanpoom.internal_cron.crons.dashboard.dhl.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DHLTrackingResult {
    private String totalDeliveries;
    private String totalCustomsIssues;
    private String totalOtherIssues;
    private String totalDelays;

    public DHLTrackingResult(String totalDeliveries,
            String totalCustomsIssues,
            String totalOtherIssues,
            String totalDelays) {

        this.totalDeliveries = totalDeliveries;
        this.totalCustomsIssues = totalCustomsIssues;
        this.totalOtherIssues = totalOtherIssues;
        this.totalDelays = totalDelays;

    }
}
