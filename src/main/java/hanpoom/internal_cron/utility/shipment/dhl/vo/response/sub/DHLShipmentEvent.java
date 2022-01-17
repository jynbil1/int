package hanpoom.internal_cron.utility.shipment.dhl.vo.response.sub;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DHLShipmentEvent {
    
    @JsonProperty("ArrayOfShipmentEventItem")
    private List<DHLArrayOfShipmentEventItem> arrayOfShipmentEventItem;
}
