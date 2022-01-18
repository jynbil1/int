package hanpoom.internal_cron.utility.shipment.dhl.vo.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DHLTrackingResponseStorage {

    List<DHLTrackingResponse> responses;

    public DHLTrackingResponseStorage(List<DHLTrackingResponse> responses) {
        this.responses = responses;
    }
}
