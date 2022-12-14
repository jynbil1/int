package hanpoom.internal_cron.api.shipment.dhl.vo.response.element;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class ShipmentEvent {
    private String date;
    private String time;
    @Setter
    private String eventCode;
    @Setter
    private String eventDesc;
    private String areaCode;
    private String areaDesc;

    // Event Remarks espcially for Clearance Issue.
    private String furtherDetails;
    private String nextSteps;

    public void setShipmentEventDetail(String date, String time, String eventCode,
            String eventDesc, String areaCode, String areaDesc,
            String furtherDetails, String nextSteps) {
        this.date = date;
        this.time = time;
        this.eventCode = eventCode;
        this.eventDesc = eventDesc;
        this.areaCode = areaCode;
        this.areaDesc = areaDesc;
        this.furtherDetails = furtherDetails;
        this.nextSteps = nextSteps;
    }

}
