package hanpoom.internal_cron.utility.shipment.dhl.service;

import org.json.JSONArray;
import org.json.JSONObject;

public interface DHLAPI {
    // public JSONObject callAPI(String requestJson);

    public JSONArray callAPI(String requestJson);
}