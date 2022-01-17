package hanpoom.internal_cron.utility.shipment.dhl.vo.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DHLTrackingResponseStorage {

    List<DHLTrackingResponse> trackingResponse;
}
