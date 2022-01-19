package hanpoom.internal_cron.crons.dashboard.dhl.vo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class DHLTrackingVO {
    private int order_no;
    private String tracking_no;
    private String event_code;
    private String event;
    private String event_dtime;
    // private String shipped_dtime;

    private String eventRemarkDetails;
    private String eventRemarkNextSteps;

    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public DHLTrackingVO() {
    }

    // for Delayed Orders
    public DHLTrackingVO(int orderNo, String trackingNo) {
        this.order_no = orderNo;
        this.tracking_no = trackingNo;
        // DLY - Customized Event Code -> Delayed Shipping
        this.event_code = "DLY";
        this.event = "발송물의 배송이 지연되고 있습니다.";
        this.event_dtime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
    }

    public DHLTrackingVO(int orderNo, String trackingNo,
            String eventCode, String event, String eventDate, String eventTime) {
        this.order_no = orderNo;
        this.tracking_no = trackingNo;
        this.event_code = eventCode;
        this.event = event;
        this.event_dtime = String.format("%s %s", eventDate, eventTime);
        // this.event_dtime = LocalDateTime.parse(eventDate + " " + eventTime,
        // DateTimeFormatter.ofPattern(DATETIME_FORMAT));

    }

    public DHLTrackingVO(int orderNo, String trackingNo,
            String eventCode, String event, String eventDate, String eventTime,
            String eventRemarkDetails, String eventRemarkNextSteps) {
        this.order_no = orderNo;
        this.tracking_no = trackingNo;
        this.event_code = eventCode;
        this.event = event;
        this.event_dtime = String.format("%s %s", eventDate, eventTime);
        this.eventRemarkDetails = eventRemarkDetails;
        this.eventRemarkNextSteps = eventRemarkNextSteps;
        // this.event_dtime = LocalDateTime.parse(eventDate + " " + eventTime,
        // DateTimeFormatter.ofPattern(DATETIME_FORMAT));

    }
    // public DHLTrackingVO(){}

}
