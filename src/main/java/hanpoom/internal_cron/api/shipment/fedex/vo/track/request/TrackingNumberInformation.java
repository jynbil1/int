package hanpoom.internal_cron.api.shipment.fedex.vo.track.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackingNumberInformation {
    private String trackingNumber;
    private String carrierCode;
    private String trackingNumberUniqueId;

}
