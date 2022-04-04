package hanpoom.internal_cron.crons.dashboard.fedex.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import hanpoom.internal_cron.utility.calendar.CalendarFormatter;
import hanpoom.internal_cron.utility.calendar.CalendarManager;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderShipment {
    // private String today;
    private String shipmentClass;
    private int orderNo;
    private String shipmentNo;
    private String trackingNo;

    private String orderDate;
    private LocalDateTime shippedDate;
    private LocalDateTime eventDate;
    // private float shippingDuration;

    private String serviceType;
    private String issueType;
    private String remark;
    private String detail;

    private String event;
    private String eventCode;

    public String getToday() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(CalendarFormatter.DATE));
    }

    public float getShippingDuration() {
        // shippedDate -> pickedup datetime
        // deliveredDate -> delivered datetime
        if (shippedDate == null || eventDate == null) {
            return 0;
        } else {
            return CalendarManager.getDayDifference(shippedDate, eventDate);
        }

    }
}
