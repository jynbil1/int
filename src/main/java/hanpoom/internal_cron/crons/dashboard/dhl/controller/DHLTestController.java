package hanpoom.internal_cron.crons.dashboard.dhl.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hanpoom.internal_cron.utility.shipment.dhl.service.DHLShipmentTrackingService;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.DHLTrackingResponse;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.DHLTrackingResponseStorage;

@RestController
public class DHLTestController {

    @Autowired
    private DHLShipmentTrackingService trackingService;

    @GetMapping("/test-dhl")
    public String testDHL() {
        // DHLTrackingRequest()
        // String trackingNo = "3440195883";
        List<String> trackingNos = Arrays.asList("3440195883", "1231231231");
        DHLTrackingResponseStorage storage = trackingService.trackShipments(trackingNos);
        System.out.println(storage.toString());
        return "test";
    }
}
