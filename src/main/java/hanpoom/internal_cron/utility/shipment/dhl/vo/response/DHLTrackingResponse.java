package hanpoom.internal_cron.utility.shipment.dhl.vo.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DHLTrackingResponse {
    
    @JsonProperty("ShipmentEvent")
    private DHLShipmentInfo shipmentInfo;
    
    @JsonProperty("AWBNumber")
    private int aWBNumber;

    @JsonProperty("Pieces")
    private DHLPieces pieces;
}
