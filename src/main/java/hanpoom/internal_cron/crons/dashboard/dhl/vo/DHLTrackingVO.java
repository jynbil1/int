package hanpoom.internal_cron.crons.dashboard.dhl.vo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DHLTrackingVO {
    private String order_no;
    private String order_date;
    private String shipment_class;
    private String out_dtime;

    private String tracking_no;
    private String event_code;
    private String event;
    private String event_dtime;
    private String shipped_dtime;

    private String eventRemarkDetails;
    private String eventRemarkNextSteps;
    private String shipment_issue_type;

    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public DHLTrackingVO() {
    }

    // for Delayed Orders
    public DHLTrackingVO(String orderNo, String trackingNo, String shipped_dtime) {
        this.order_no = orderNo;
        this.tracking_no = trackingNo;
        // DLY - Customized Event Code -> Delayed Shipping
        this.event_code = "DLY";
        this.event = "발송물의 배송이 지연되고 있습니다.";
        this.event_dtime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
        this.shipped_dtime = shipped_dtime;
    }

    // for delivered
    public DHLTrackingVO(String orderNo, String trackingNo,
            String eventCode, String event, String eventDate,
            String eventTime, String shipped_dtime) {
        this.order_no = orderNo;
        this.tracking_no = trackingNo;
        this.event_code = eventCode;
        this.event = event;
        this.event_dtime = String.format("%s %s", eventDate, eventTime);
        this.shipped_dtime = shipped_dtime;
        // this.event_dtime = LocalDateTime.parse(eventDate + " " + eventTime,
        // DateTimeFormatter.ofPattern(DATETIME_FORMAT));

    }

    // for issue orders
    public DHLTrackingVO(String orderNo,
            String order_date,
            String trackingNo,
            String eventCode,
            String event,
            String eventDate,
            String eventTime,
            String shipped_dtime,
            String shipment_class,
            String shipment_issue_type) {
        this.order_no = orderNo;
        this.order_date = order_date;
        this.tracking_no = trackingNo;
        this.event_code = eventCode;
        this.event = event;
        this.event_dtime = String.format("%s %s", eventDate, eventTime);
        this.shipped_dtime = shipped_dtime;
        this.shipment_class = shipment_class;
        this.shipment_issue_type = shipment_issue_type;
    }

    // for customs clearanace issue orders
    public DHLTrackingVO(String orderNo,
            String order_date,
            String trackingNo,
            String eventCode,
            String event,
            String eventDate,
            String eventTime,
            String eventRemarkDetails,
            String eventRemarkNextSteps,
            String shipped_dtime,
            String shipment_class,
            String shipment_issue_type) {
        this.order_no = orderNo;
        this.order_date = order_date;
        this.tracking_no = trackingNo;
        this.event_code = eventCode;
        this.event = event;
        this.event_dtime = String.format("%s %s", eventDate, eventTime);
        this.eventRemarkDetails = eventRemarkDetails;
        this.eventRemarkNextSteps = eventRemarkNextSteps;
        this.shipped_dtime = shipped_dtime;
        this.shipment_class = shipment_class;
        this.shipment_issue_type = shipment_issue_type;

        // this.event_dtime = LocalDateTime.parse(eventDate + " " + eventTime,
        // DateTimeFormatter.ofPattern(DATETIME_FORMAT));

    }
    // public DHLTrackingVO(){}

}
