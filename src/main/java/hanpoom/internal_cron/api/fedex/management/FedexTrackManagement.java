package hanpoom.internal_cron.api.fedex.management;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import org.json.JSONObject;

import hanpoom.internal_cron.api.fedex.vo.track.TrackingResult;
import hanpoom.internal_cron.api.fedex.vo.track.TrackingResult.TrackResult;

public abstract class FedexTrackManagement {
    public abstract TrackingResult trackShipment(String trackingNo, boolean isDetailed);
    public abstract TrackingResult trackShipmentWDate(String trackingNo, boolean isDetailed,
        LocalDateTime startDateTime, LocalDateTime endDateTime);

    public abstract List<TrackingResult> trackMultipleShipments(HashSet<String> trackingNos, boolean isDetailed);

    public abstract void sendNotification();

    public abstract JSONObject trackByReferences();

    public abstract JSONObject trackByTrackControlNumber();

    public abstract JSONObject trackByDocument();

    // -------------------
    public abstract boolean isDelivered(TrackResult shipment);
    public abstract boolean isPickedUp(TrackResult shipment);
    public abstract boolean occurredProblem(TrackResult shipment);
    public abstract boolean isDelayed(TrackResult shipment);

}
