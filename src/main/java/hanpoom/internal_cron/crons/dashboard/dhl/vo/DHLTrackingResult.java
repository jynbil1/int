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
    private String totalUntrackables;
    private String total;

    public DHLTrackingResult(String totalDeliveries,
            String totalCustomsIssues,
            String totalOtherIssues,
            String totalDelays,
            String totalUntrackables,
            String total) {

        this.totalDeliveries = totalDeliveries;
        this.totalCustomsIssues = totalCustomsIssues;
        this.totalOtherIssues = totalOtherIssues;
        this.totalDelays = totalDelays;
        this.totalUntrackables = totalUntrackables;
        this.total = total;
    }
}
