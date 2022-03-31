package hanpoom.internal_cron.crons.dashboard.spreadsheet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackResponse;
import hanpoom.internal_cron.utility.shipment.dhl.service.DHLShipmentTrackingService;

@RestController
public class DHLTrackingAPITestController {

    @Autowired
    private DHLShipmentTrackingService shipmentTracking;

    @GetMapping(value = "/dhl/api/track/{trackingNo}")
    public Object getTrackingShipments(@PathVariable(value = "trackingNo") String trackingNo) {
        Set<String> trackingNos = new HashSet<>();
        Arrays.asList(trackingNo.split(",")).stream().forEach(el -> {
            trackingNos.add(el);
        });
        List<DHLTrackResponse> responses = shipmentTracking.trackMultipleShipments(trackingNos);
        return responses;
    }
}
