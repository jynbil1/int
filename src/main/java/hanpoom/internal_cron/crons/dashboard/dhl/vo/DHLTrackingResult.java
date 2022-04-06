package hanpoom.internal_cron.crons.dashboard.dhl.vo;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DHLTrackingResult {
    private List<DHLTrackingVO> deliveredOrders;
    private List<DHLTrackingVO> customIssueOrders;
    private List<DHLTrackingVO> otherIssueOrders;

    private List<DHLTrackingVO> delayedOrders;
    private List<DHLTrackingVO> untrackableOrders;
    private List<DHLTrackingVO> returnedOrders;

    private String totalInTransit;

    public static String getMoneyFormat(int number) {
        return NumberFormat.getNumberInstance(Locale.US).format(number);
    }
}
