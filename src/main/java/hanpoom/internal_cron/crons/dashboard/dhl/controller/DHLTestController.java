package hanpoom.internal_cron.crons.dashboard.dhl.controller;

import java.io.IOError;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hanpoom.internal_cron.crons.dashboard.dhl.service.DHLService;

@RestController
public class DHLTestController {

    @Autowired
    private DHLService dHLService;


    @GetMapping("/investigate-issue-occurred-orders")
    public String investigateIssueOccurredOrders() {
        return null;
    }

    @GetMapping("/investigate-shipped-orders")
    public String testStatus() {
        dHLService.investigateNProcessShippedOrders();
        System.out.println(dHLService.getCustomsIssueOrders().toString());
        System.out.println(dHLService.getOtherIssueOrders().toString());
        System.out.println(dHLService.getDeliveredOrders().toString());
        System.out.println(dHLService.getUntrackableOrders().toString());
        System.out.println(dHLService.getDelayedOrders().toString());

        return "test";
    }
}
