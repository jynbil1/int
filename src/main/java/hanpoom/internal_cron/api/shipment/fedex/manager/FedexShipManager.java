package hanpoom.internal_cron.api.shipment.fedex.manager;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hanpoom.internal_cron.api.client.HttpClient;
import hanpoom.internal_cron.api.shipment.fedex.config.FedexAPIConfig;
import hanpoom.internal_cron.api.shipment.fedex.config.ShipperConfig;
import hanpoom.internal_cron.api.shipment.fedex.enumerate.FedexShipEnum;
import hanpoom.internal_cron.api.shipment.fedex.management.FedexShipManagement;
import hanpoom.internal_cron.api.shipment.fedex.vo.ship.request.UnrefinedShipData;
import hanpoom.internal_cron.api.shipment.fedex.vo.ship.response.CancelShipResponse;
import hanpoom.internal_cron.api.shipment.fedex.vo.ship.response.CreateShipResponse;
import hanpoom.internal_cron.api.token.FedexToken;
import okhttp3.Response;

@Component
public class FedexShipManager extends FedexShipManagement {

    private static final String CREATE_SHIPMENT_URL = "/ship/v1/shipments";
    private static final String DELETE_SHIPMENT_URL = "/ship/v1/shipments/cancel";

    @Autowired
    private FedexAPIConfig fedexAPIConfig;
    @Autowired
    private ShipperConfig shipperConfig;

    @Override
    public CreateShipResponse createShipment(JSONObject requestBody) {
        FedexToken token = fedexAPIConfig.getToken();
        HttpClient client = new HttpClient();
        try {
            Response response = client.apiPost(token.getAccessToken(),
                    token.getUrl() + CREATE_SHIPMENT_URL,
                    requestBody.toString());
            return new Gson().fromJson(response.body().string(), CreateShipResponse.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CreateShipResponse createShipments(JSONArray requestBodies) {
        return null;
    }

    @Override
    public CancelShipResponse deleteShipment(String accountNo, String trackingNumber) {
        return null;
    }

    public JSONObject getDomesticShipment(FedexShipEnum serviceType, List<UnrefinedShipData> shipData) {

        // Common Setting is Retreived.
        JSONObject json = shipperConfig.getShipperInfo();
        UnrefinedShipData recipient = shipData.get(0);

        JSONObject body = json.getJSONObject("requestedShipment");

        JSONArray recipients = new JSONArray();
        JSONArray requestedPackageLineItems = new JSONArray();

        body.put("recipients", recipients);
        body.put("requestedPackageLineItems", requestedPackageLineItems);

        System.out.println(json.toString());
        return json;
    }

}
