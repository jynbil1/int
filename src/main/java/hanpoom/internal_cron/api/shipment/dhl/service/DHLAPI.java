package hanpoom.internal_cron.api.shipment.dhl.service;

// import org.json.JSONArray;
import org.json.JSONObject;

public interface DHLAPI {
    JSONObject callAPI(String requestJson);

    // JSONArray callAPI(String requestJson);
}
