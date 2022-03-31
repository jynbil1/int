package hanpoom.internal_cron.api.fedex.management;

import org.json.JSONArray;
import org.json.JSONObject;

import hanpoom.internal_cron.api.fedex.vo.ship.response.CancelShipResponse;
import hanpoom.internal_cron.api.fedex.vo.ship.response.CreateShipResponse;

public abstract class FedexShipManagement {
    public abstract CreateShipResponse createShipment(JSONObject requestBody);
    public abstract CreateShipResponse createShipments(JSONArray requestBodies);
    public abstract CancelShipResponse deleteShipment(String accountNo, String trackingNumber);

    
}
