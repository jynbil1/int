package hanpoom.internal_cron.crons.dashboard.dhl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hanpoom.internal_cron.crons.dashboard.dhl.service.DHLReport;

@RestController
@RequestMapping(value = "/shipment/api/dhl")
public class DHLTestController {

    @Autowired
    private DHLReport dhlReport;

    @GetMapping("/investigate-issue-occurred-orders")
    public String investigateIssueOccurredOrders() {
        return null;
    }

    @GetMapping("/monitor")
    public void testStatus() {
        dhlReport.monitorShipments();
    }

    @GetMapping("/monitor/past-orders")
    public void monitorPastOrders() {

    }
}
