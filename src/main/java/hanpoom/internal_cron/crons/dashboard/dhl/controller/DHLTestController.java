package hanpoom.internal_cron.crons.dashboard.dhl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hanpoom.internal_cron.utility.shipment.dhl.config.MyDHLClient;
import hanpoom.internal_cron.utility.shipment.dhl.service.DHLShipmentTrackingService;
import hanpoom.internal_cron.utility.shipment.dhl.vo.DHLTrackingRequest;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.DHLTrackingResponse;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.DHLTrackingResponseStorage;

@RestController
public class DHLTestController {

    @Autowired
    private DHLShipmentTrackingService trackingService;

    @Autowired
    private MyDHLClient client;

    @GetMapping("/test-dhl")
    public String testDHL() {
        // DHLTrackingRequest()
        DHLTrackingRequest request = new DHLTrackingRequest("3440195883");
        String requestJson = request.getValidatedJSONRequest().toString();
        System.out.println(requestJson);
        JSONArray object = trackingService.callAPI(requestJson.toString());

        DHLTrackingResponseStorage responseStorage = new DHLTrackingResponseStorage();
        // DHLTrackingResponse response = new DHLTrackingResponse();
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                // System.out.println(json.getString("comments").getString("comment"));
                responseStorage = mapper.readValue(object.toString(), responseStorage.getClass());
                // return responseStorage;
                System.out.println(responseStorage.toString());
            } catch (MismatchedInputException mie) {
                mie.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return "test";
    }
}
