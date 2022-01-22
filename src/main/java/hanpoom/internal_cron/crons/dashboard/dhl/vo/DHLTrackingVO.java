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
    private String typeOfIssue;

    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public DHLTrackingVO() {
    }

    // 스프레드 시트에서 읽어드린 데이터 로우로 운송장 재탐색하는 목적
    public DHLTrackingVO(String orderNo, String trackingNo) {
        this.order_no = orderNo;
        this.tracking_no = trackingNo;
    }

    // for Untracable Orders
    public DHLTrackingVO(String orderNo, String trackingNo, String shipmentClass) {
        this.order_no = orderNo;
        this.tracking_no = trackingNo;
        // DLY - Customized Event Code -> Delayed Shipping
        this.event_code = "UTR";
        this.event = "발송물 조회가 되지 않습니다.";
        this.event_dtime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
        this.typeOfIssue = "untrackable";
        this.shipment_issue_type = "untrackable";
        this.shipment_class = shipmentClass;
    }

    // for Delayed Orders
    public DHLTrackingVO(String orderNo, String trackingNo, String shipped_dtime, String shipmentClass) {
        this.order_no = orderNo;
        this.tracking_no = trackingNo;
        // DLY - Customized Event Code -> Delayed Shipping
        this.event_code = "DLY";
        this.event = "발송물의 배송이 지연되고 있습니다.";
        this.event_dtime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
        this.shipped_dtime = shipped_dtime;
        this.typeOfIssue = "delay";
        this.shipment_class = shipmentClass;
    }
}
