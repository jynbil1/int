package hanpoom.internal_cron.api.shipment.fedex.vo.track.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackingRequest {
    private boolean includeDetailedScans;
    private List<TrackingInfo> trackingInfo;
}

