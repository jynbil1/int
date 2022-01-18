package hanpoom.internal_cron.crons.dashboard.dhl.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hanpoom.internal_cron.crons.dashboard.dhl.service.DHLService;
import hanpoom.internal_cron.utility.shipment.dhl.service.DHLShipmentTrackingService;

@RestController
public class DHLTestController {

    @Autowired
    private DHLService dHLService;

    @GetMapping("/investigate-shipped-orders")
    public String investigateShippedOrders() {
        dHLService.investigateNProcessShippedOrders();
        return null;
    }

    @GetMapping("/investigate-issue-occurred-orders")
    public String investigateIssueOccurredOrders() {
        return null;
    }

}
