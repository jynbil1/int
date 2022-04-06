package hanpoom.internal_cron.api.shipment.fedex.manager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hanpoom.internal_cron.api.client.HttpClient;
import hanpoom.internal_cron.api.shipment.fedex.config.FedexAPIConfig;
import hanpoom.internal_cron.api.shipment.fedex.config.FedexShipStatusCode;
import hanpoom.internal_cron.api.shipment.fedex.enumerate.FedexShipDuration;
import hanpoom.internal_cron.api.shipment.fedex.enumerate.FedexShipmentStatus;
import hanpoom.internal_cron.api.shipment.fedex.management.FedexTrackManagement;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse.DateAndTime;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse.ScanEvent;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse.TrackResult;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.request.TrackingInfo;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.request.TrackingNumberInformation;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.request.TrackingRequest;
import hanpoom.internal_cron.api.token.FedexToken;
import hanpoom.internal_cron.utility.calendar.CalendarFormatter;
import hanpoom.internal_cron.utility.calendar.CalendarManager;
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
    @Autowired
    private FedexShipStatusCode statusCode;

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
    public List<FedexTrackResponse> trackMultipleShipments(HashSet<String> trackingNos, boolean includeDetailedScans) {
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

                List<FedexTrackResponse> trackResults = new ArrayList<>();
                jsonArray.forEach(el -> {
                    // System.out.println(el.toString());
                    trackResults.add(new Gson().fromJson(el.toString(), FedexTrackResponse.class));
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
    public FedexTrackResponse trackShipment(String trackingNo, boolean includeDetailedScans) {

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
                return new Gson().fromJson(json.toString(), FedexTrackResponse.class);
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
    public FedexTrackResponse trackShipmentWDate(String trackingNo, boolean isDetailed, LocalDateTime startDateTime,
            LocalDateTime endDateTime) {
        return null;
    }

    @Override
    public boolean isDelivered(TrackResult shipment) {
        for (DateAndTime event : shipment.getDateAndTimes()) {
            if (event.getType().equals(FedexShipmentStatus.DELIVERED.getValue())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPickedUp(TrackResult shipment) {
        for (DateAndTime event : shipment.getDateAndTimes()) {
            if (event.getType().equals(FedexShipmentStatus.SHIPPED.getValue())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isProblematic(TrackResult shipment) {
        for (DateAndTime event : shipment.getDateAndTimes()) {
            if (event.getType().equals(FedexShipmentStatus.TENDER.getValue()) ||
                    event.getType().equals(FedexShipmentStatus.EXPECT_TENDER.getValue())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isDelayed(TrackResult shipment) {
        if (isDelivered(shipment)) {
            return false;
        } else if (shipment
                .getScanEvents()
                .stream()
                .map(obj -> obj.getEventType().equals("DY"))
                .findFirst()
                .get()) {
            return true;
        } else if (shipment.getDateAndTimes().size() < 2) {
            return false;
        } else {

            FedexShipDuration shipDuration = FedexShipDuration.findByServiceType(shipment.getServiceDetail().getType());
            List<DateAndTime> dateAndTimes = shipment
                    .getDateAndTimes()
                    .stream()
                    .sorted(Comparator.comparing(DateAndTime::getDateTime))
                    .collect(Collectors.toList());

            float dayDiff = CalendarManager.getDayDifference(dateAndTimes.get(0).getDateTime(),
                    dateAndTimes.get(dateAndTimes.size() - 1).getDateTime());
            if (dayDiff >= shipDuration.getValue()) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean isReturned(TrackResult shipment) {
        return false;
    }

    @Override
    public boolean isNotFound(TrackResult shipment) {
        if (shipment == null || shipment.getDateAndTimes().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public LocalDateTime getEventDateTime(TrackResult shipment, FedexShipmentStatus shipmentStatus) {
        try {
            String strDateTime = shipment
                    .getDateAndTimes()
                    .stream()
                    .filter(obj -> obj.getType().equals(shipmentStatus.getValue()))
                    .findFirst()
                    .get()
                    .getDateTime();

            strDateTime = strDateTime.substring(0, 19);
            return LocalDateTime.parse(strDateTime, DateTimeFormatter.ofPattern(CalendarFormatter.TZONE_DATETIME));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public ScanEvent getRecentEvent(List<ScanEvent> events) {
        return events.stream()
                .sorted(Comparator.comparing(ScanEvent::getDate).reversed())
                .findFirst()
                .get();
    }

}
