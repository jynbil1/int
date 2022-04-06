package hanpoom.internal_cron.api.shipment.fedex.management;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import org.json.JSONObject;

import hanpoom.internal_cron.api.shipment.fedex.enumerate.FedexShipDuration;
import hanpoom.internal_cron.api.shipment.fedex.enumerate.FedexShipmentStatus;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse.ScanEvent;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse.TrackResult;

public abstract class FedexTrackManagement {
    public abstract FedexTrackResponse trackShipment(String trackingNo, boolean isDetailed);
    public abstract FedexTrackResponse trackShipmentWDate(String trackingNo, boolean isDetailed,
        LocalDateTime startDateTime, LocalDateTime endDateTime);

    public abstract List<FedexTrackResponse> trackMultipleShipments(HashSet<String> trackingNos, boolean isDetailed);

    public abstract void sendNotification();

    public abstract JSONObject trackByReferences();

    public abstract JSONObject trackByTrackControlNumber();

    public abstract JSONObject trackByDocument();

    // -------------------
    public abstract boolean isDelivered(TrackResult shipment);
    public abstract boolean isPickedUp(TrackResult shipment);
    public abstract boolean isProblematic(TrackResult shipment);
    public abstract boolean isReturned(TrackResult shipment);
    public abstract boolean isNotFound(TrackResult shipment);

    // public abstract LocalDateTime getDeliveredDatetime(TrackResult shipment);
    // public abstract LocalDateTime getPickedupDatetime(TrackResult shipment);
    // public abstract LocalDateTime getProblemDatetime(TrackResult shipment);
    // public abstract LocalDateTime getReturnedDatetime(TrackResult shipment);
    // public abstract float getDelayedDurationInHour(TrackResult shipment);
    public abstract boolean isDelayed(TrackResult shipment);
    // public abstract ScanEvent 
    public abstract LocalDateTime getEventDateTime(TrackResult shipment, FedexShipmentStatus shipmentStatus);
    public abstract ScanEvent getRecentEvent(List<ScanEvent> events);

}
