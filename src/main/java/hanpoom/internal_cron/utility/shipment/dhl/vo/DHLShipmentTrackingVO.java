package hanpoom.internal_cron.utility.shipment.dhl.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DHLShipmentTrackingVO {
    private String order_no;
    private String tracking_no;
    private String event_code;
    private String event;
    private String event_dtime;
    private String shipped_dtime;
}
