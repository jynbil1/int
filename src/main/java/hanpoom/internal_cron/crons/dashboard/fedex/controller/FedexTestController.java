package hanpoom.internal_cron.crons.dashboard.fedex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hanpoom.internal_cron.crons.dashboard.fedex.service.FedexService;

@RestController
public class FedexTestController {

    @Autowired
    private FedexService fedexService;

    @GetMapping(value = "/api/fedex/test")
    public void cronFedexShipmentTrack() {
        fedexService.monitorNReportFedexShipments();

    }

    @GetMapping(value = "/api/fedex/excel")
    public void monitorFedexErrorShipment() {
        fedexService.reMonitorFedexIssueShipments();
    }
}
