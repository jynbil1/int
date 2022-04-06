package hanpoom.internal_cron.crons.dashboard.fedex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hanpoom.internal_cron.crons.dashboard.fedex.service.FedexService;

@RestController
public class FedexTestController {

    // Live Slack URL
    private static final String FEDEX_SLACK_ALARM_URL = "https://hooks.slack.com/services/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";
    // private static final String FEDEX_SLACK_ALARM_URL =
    // "https://hooks.slack.com/services/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";

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
