package hanpoom.internal_cron.api.shipment.fedex.vo.track.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackingInfo {
    private String shipDateBigin;
    private String shipDateEnd;
    private TrackingNumberInformation trackingNumberInfo;
}

