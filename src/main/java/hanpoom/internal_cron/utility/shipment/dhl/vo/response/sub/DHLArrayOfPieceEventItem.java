package hanpoom.internal_cron.utility.shipment.dhl.vo.response.sub;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class DHLArrayOfPieceEventItem {
    @Getter
    @ToString
    @Setter
    private class ServiceArea {
        @JsonProperty("Description")
        private String description;
        @JsonProperty("ServiceAreaCode")
        private String serviceAreaCode;

    }

    @Getter
    @ToString
    @Setter
    private class ServiceEvent {
        @JsonProperty("Description")
        private String description;
        @JsonProperty("EventCode")
        private String eventCode;

    }

    @Getter
    @ToString
    @Setter
    private class ShipperReference {
        @JsonProperty("ReferenceID")
        private int referenceID;

    }

    private ServiceArea serviceArea;
    private ServiceEvent serviceEvent;
    private ShipperReference shipperReference;

    @JsonProperty("Time")
    private String time;

    @JsonProperty("Date")
    private String date;

    @JsonProperty("Signatory")
    private String signatory;
}
