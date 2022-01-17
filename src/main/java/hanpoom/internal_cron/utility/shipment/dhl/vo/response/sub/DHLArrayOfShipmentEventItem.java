package hanpoom.internal_cron.utility.shipment.dhl.vo.response.sub;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DHLArrayOfShipmentEventItem {
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
    @Setter
    @ToString
    private class ServiceEvent {
        @JsonProperty("Description")
        private String description;
        @JsonProperty("EventCode")
        private String eventCode;
    }

    private ServiceArea serviceArea;
    private ServiceEvent serviceEvent;

    @JsonProperty("Time")
    private String time;

    @JsonProperty("Date")
    private String date;
}
