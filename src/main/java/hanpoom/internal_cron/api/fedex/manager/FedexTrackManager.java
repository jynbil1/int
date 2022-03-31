package hanpoom.internal_cron.api.fedex.manager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hanpoom.internal_cron.api.client.HttpClient;
import hanpoom.internal_cron.api.fedex.config.FedexAPIConfig;
import hanpoom.internal_cron.api.fedex.management.FedexTrackManagement;
import hanpoom.internal_cron.api.fedex.vo.track.TrackingResult;
import hanpoom.internal_cron.api.fedex.vo.track.TrackingResult.DateAndTime;
import hanpoom.internal_cron.api.fedex.vo.track.TrackingResult.TrackResult;
import hanpoom.internal_cron.api.fedex.vo.track.request.TrackingInfo;
import hanpoom.internal_cron.api.fedex.vo.track.request.TrackingNumberInformation;
import hanpoom.internal_cron.api.fedex.vo.track.request.TrackingRequest;
import hanpoom.internal_cron.api.token.FedexToken;
import okhttp3.Response;

@Component
public class FedexTrackManager extends FedexTrackManagement {
    private static final String MULTIPLE_PIECE_SHIPMENT = "/track/v1/associatedshipments";
    private static final String NOTIFICATION = "/track/v1/notifications";
    private static final String TRACK_BY_REFERENCES = "/track/v1/referencenumbers";
    private static final String TRACK_BY_CONTROL_NO = "/track/v1/tcn";
    private static final String TRACK_DOCUMENT = "/track/v1/trackingdocuments";
    private static final String TRACK_BY_TRACKING_NO = "/track/v1/trackingnumbers";

    @Autowired
    private FedexAPIConfig fedexConfig;

    @Override
    public void sendNotification() {

    }

    @Override
    public JSONObject trackByDocument() {
        return null;
    }

    @Override
    public JSONObject trackByReferences() {
        return null;
    }

    @Override
    public JSONObject trackByTrackControlNumber() {
        return null;
    }

    @Override
    public List<TrackingResult> trackMultipleShipments(HashSet<String> trackingNos, boolean includeDetailedScans) {
        // 한번에 30개만.
        if (trackingNos.size() > 30) {
            return null;
        }

        FedexToken token = fedexConfig.getToken();

        List<TrackingInfo> trackingInfos = new ArrayList<>();
        for (String trackingNo : trackingNos) {
            trackingInfos.add(TrackingInfo.builder()
                    .trackingNumberInfo(TrackingNumberInformation.builder()
                            .trackingNumber(trackingNo).build())
                    .build());
        }
        TrackingRequest requestBody = TrackingRequest.builder()
                .includeDetailedScans(includeDetailedScans)
                .trackingInfo(trackingInfos)
                .build();

        String requestJson = new Gson().toJson(requestBody);
        // System.out.println(requestJson);
        try {
            Response response = new HttpClient().apiPost(token.getValidAccessToken(),
                    token.getUrl() + TRACK_BY_TRACKING_NO, requestJson);
            if (response.code() == 200) {
                String responseBody = response.body().string();
                // String json = new JSONObject(responseBody)
                // .optJSONObject("output").optString("completeTrackResults");
                // System.out.println(json);
                // return new Gson().fromJson(json, new
                // TypeToken<ArrayList<TrackingResult>>(){}.getType());

                JSONArray jsonArray = new JSONObject(responseBody)
                        .optJSONObject("output").optJSONArray("completeTrackResults");

                List<TrackingResult> trackResults = new ArrayList<>();
                jsonArray.forEach(el -> {
                    // System.out.println(el.toString());
                    trackResults.add(new Gson().fromJson(el.toString(), TrackingResult.class));
                });
                return trackResults;

            } else {
                System.out.println(response.code());
                System.out.println(response.message());
                System.out.println(response.body().string());

            }
        } catch (IOException ieo) {
            ieo.printStackTrace();
        }

        return null;
    }

    @Override
    public TrackingResult trackShipment(String trackingNo, boolean includeDetailedScans) {

        FedexToken token = fedexConfig.getToken();
        TrackingRequest trackReq = TrackingRequest.builder()
                .includeDetailedScans(includeDetailedScans)
                .trackingInfo(Arrays.asList(
                        TrackingInfo.builder()
                                .trackingNumberInfo(
                                        TrackingNumberInformation.builder()
                                                .trackingNumber(trackingNo).build())
                                .build()))
                .build();
        String requestJson = new Gson().toJson(trackReq);
        try {
            Response response = new HttpClient().apiPost(token.getValidAccessToken(),
                    token.getUrl() + TRACK_BY_TRACKING_NO, requestJson);
            if (response.code() == 200) {
                String responseBody = response.body().string();
                String json = new JSONObject(responseBody)
                        .optJSONObject("output").optJSONArray("completeTrackResults").optString(0);
                System.out.println(json);
                return new Gson().fromJson(json.toString(), TrackingResult.class);
            } else {
                System.out.println(response.code());
                System.out.println(response.message());
                System.out.println(response.body().string());

            }
        } catch (IOException ieo) {
            ieo.printStackTrace();
        }

        return null;
    }

    @Override
    public TrackingResult trackShipmentWDate(String trackingNo, boolean isDetailed, LocalDateTime startDateTime,
            LocalDateTime endDateTime) {
        return null;
    }

    @Override
    public boolean isDelivered(TrackResult shipment) {
        for (DateAndTime event : shipment.getDateAndTimes()) {
            if (event.getType().equals("ACTUAL_DELIVERY")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPickedUp(TrackResult shipment) {
        for (DateAndTime event : shipment.getDateAndTimes()) {
            if (event.getType().equals("ACTUAL_PICKUP")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean occurredProblem(TrackResult shipment) {
        return false;
    }

    @Override
    public boolean isDelayed(TrackResult shipment) {
        return false;
    }
}
