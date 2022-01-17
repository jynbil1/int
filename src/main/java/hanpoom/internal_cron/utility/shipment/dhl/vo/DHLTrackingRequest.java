package hanpoom.internal_cron.utility.shipment.dhl.vo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

import lombok.Setter;
import lombok.ToString;

@ToString
public class DHLTrackingRequest {
    private static final String FULL_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final String DATETIME_FORMAT = "yyyy-MM-ddn HH:mm";


    private String messageTime;
    private final String messageReference = "2d7c271a93b346e1aaa7051c107738d0";

    private String arrayOfAWBNumberItem;

    @Setter
    private String levelOfDetails;
    @Setter
    private String piecesEnabled;

    private String languageCode;
    private String languageScriptCode;
    private String languageCountryCode;

    public DHLTrackingRequest(String trackingNos) {
        LocalDateTime now = LocalDateTime.now();
        this.messageTime = now.format(DateTimeFormatter.ofPattern(FULL_DATETIME_FORMAT));
        this.levelOfDetails = "ALL_CHECKPOINTS";
        this.piecesEnabled = "B";
        this.arrayOfAWBNumberItem = trackingNos;
        ;
    }

    public DHLTrackingRequest(String trackingNos, String levelOfDetails) {
        LocalDateTime now = LocalDateTime.now();
        this.messageTime = now.format(DateTimeFormatter.ofPattern(FULL_DATETIME_FORMAT));
        this.levelOfDetails = levelOfDetails;
        this.piecesEnabled = "B";
        this.arrayOfAWBNumberItem = trackingNos;

    }

    public DHLTrackingRequest(String trackingNos, boolean isKorean) {
        LocalDateTime now = LocalDateTime.now();
        this.messageTime = now.format(DateTimeFormatter.ofPattern(FULL_DATETIME_FORMAT));
        this.levelOfDetails = "ALL_CHECK_POINTS";
        this.piecesEnabled = "B";
        this.arrayOfAWBNumberItem = trackingNos;
        ;
    }

    public DHLTrackingRequest(String trackingNos, String levelOfDetails, boolean isKorean) {
        LocalDateTime now = LocalDateTime.now();
        this.messageTime = now.format(DateTimeFormatter.ofPattern(FULL_DATETIME_FORMAT));
        this.messageTime = levelOfDetails;
        this.levelOfDetails = "B";

        this.arrayOfAWBNumberItem = trackingNos;

        if (isKorean) {
            this.languageCode = "kor";
            this.languageScriptCode = "kore";
            this.languageCountryCode = "kr";
        }
    }

    public JSONObject getValidatedJSONRequest() {
    
        JSONObject jsonObject = new JSONObject();
        JSONObject serviceHeader = new JSONObject();
        JSONObject request = new JSONObject();
        JSONObject trackingRequest = new JSONObject();
        JSONObject requestSet = new JSONObject();
        JSONObject awbNo = new JSONObject();

        serviceHeader.put("MessageTime", this.messageTime);
        serviceHeader.put("MessageReference", this.messageReference);
        request.put("ServiceHeader", serviceHeader);

        awbNo.put("ArrayOfAWBNumberItem", this.arrayOfAWBNumberItem);

        trackingRequest.put("Request", request);
        trackingRequest.put("AWBNumber", awbNo);
        trackingRequest.put("LevelOfDetails", this.levelOfDetails);
        trackingRequest.put("PiecesEnabled", this.piecesEnabled);

        requestSet.put("TrackingRequest", trackingRequest);

        jsonObject.put("trackShipmentRequest", new JSONObject().put("trackingRequest", requestSet));

        return jsonObject;
    }
}
