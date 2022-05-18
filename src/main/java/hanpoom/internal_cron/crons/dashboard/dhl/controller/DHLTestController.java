package hanpoom.internal_cron.crons.dashboard.dhl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hanpoom.internal_cron.crons.dashboard.dhl.service.DHLReport;

@RestController
public class DHLTestController {

    @Autowired
    private DHLReport dhlReport;

    @GetMapping("/investigate-issue-occurred-orders")
    public String investigateIssueOccurredOrders() {
        return null;
    }

    @GetMapping("/api/dhl/monitor/shipment")
    public void testStatus() {
        dhlReport.monitorShipments();
    }
}
