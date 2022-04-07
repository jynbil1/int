package hanpoom.internal_cron.api.shipment.fedex.vo.track;

import java.util.List;

import lombok.Data;

@Data
public class FedexTrackResponse {
    private String trackingNumber;
    private List<TrackResult> trackResults;

    @Data
    public static class TrackResult {
        private TrackingNumberInfo trackingNumberInfo;
        private AdditionalTrackingInfo additionalTrackingInfo;
        private DistanceToDestination distanceToDestination;
        private List<ConsolidationDetail> consolidationDetails;

        private int meterNumber;
        private ReturnDetail returnDetail;
        private ServiceDetail serviceDetail;
        private DestinationLocation destinationLocation;
        private LatestStatusDetail latestStatusDetail;
        private ServiceCommitMessage serviceCommitMessage;
        private List<InformationNote> informationNotes;

        private Error error;
        private List<SpecialHandling> specialHandlings;
        private List<AvailableImage> availableImages;
        private DeliveryDetails deliveryDetails;
        private List<ScanEvent> scanEvents;
        private List<DateAndTime> dateAndTimes;
        private PackageDetails packageDetails;
        private String goodsClassificationCode;
        private HoldAtLocation holdAtLocation;
        private List<CustomDeliveryOption> customDeliveryOptions;
        private EstimatedDeliveryTimeWindow estimatedDeliveryTimeWindow;
        private List<PieceCount> pieceCounts;
        private OriginLocation originLocation;
        private RecipientInformation recipientInformation;
        private StandardTransitTimeWindow standardTransitTimeWindow;
        private ShipmentDetails shipmentDetails;
        private ReasonDetail reasonDetail;
        private List<String> availableNotifications;
        private ShipperInformation shipperInformation;
        private Address lastUpdatedDestinationAddress;
    }

    @Data
    public static class TrackingNumberInfo {
        private String trackingNumber;
        private String carrierCode;
        private String trackingNumberUniqueId;
    }

    @Data
    public static class AdditionalTrackingInfo {
        private boolean hasAssociatedShipments;
        private String nickname;
        private List<PackageIdentifier> packageIdentifiers;
        private String shipmentNotes;

    }

    @Data
    public static class DistanceToDestination {
        private String units;
        private float value;
    }

    @Data
    public static class ConsolidationDetail {
        private String timeStamp;
        private String consolidationID;
        private ReasonDetail reasonDetail;

        private int packageCount;
        private String eventType;
    }

    @Data
    public static class ReturnDetail {
        private String authorizationName;
        private ReasonDetail reasonDetail;
    }

    @Data
    public static class ServiceDetail {
        private String description;
        private String shortDescription;
        private String type;
    }

    @Data
    public static class DestinationLocation {
        private String locationId;
        private LocationContactAndAddress locationContactAndAddress;
        private String locationType;
    }

    @Data
    public static class LatestStatusDetail {
        private ScanLocation scanLocation;
        private String code;
        private String derivedCode;
        private List<AncillaryDetail> ancillaryDetails;
        private String statusByLocale;
        private String description;
        private DelayDetail delayDetail;
    }

    @Data
    public static class ScanEvent {
        private String date;
        private String derivedStatus;
        private ScanLocation scanLocation;
        private String exceptionDescription;
        private String eventDescription;
        private String eventType;
        private String derivedStatusCode;
        private String exceptionCode;
        private DelayDetail delayDetail;
    }

    @Data
    public static class ServiceCommitMessage {
        private String message;
        private String type;
    }

    @Data
    public static class InformationNote {
        private String code;
        private String description;
    }

    @Data
    public static class Error {
        private String code;
        private List<Parameter> parameterList;
        private String message;
    }

    @Data
    public static class SpecialHandling {
        private String description;
        private String type;
        private String paymentType;
    }

    @Data
    public static class AvailableImage {
        private String size;
        private String type;
    }

    @Data
    public static class DeliveryDetails {
        private String receivedByName;
        private String destinationServiceArea;
        private String destinationServiceAreaDescription;
        private String locationDescription;
        private Address actualDeliveryAddress;
        private boolean deliveryToday;
        private String locationType;
        private String signedByName;
        private String officeOrderDeliveryMethod;
        private int deliveryAttempts;
        private List<DeliveryOptionEligibilityDetail> deliveryOptionEligibilityDetails;
    }

    

    @Data
    public static class DateAndTime {
        private String dateTime;
        private String type;
    }

    @Data
    public static class PackageDetails {
        private String physicalPackagingType;
        private String sequenceNumber;
        private String undeliveredCount;
        private PackageDescription packagingDescription;
        private String count;
        private WeightAndDimensions weightAndDimensions;
        private List<String> packageContent;
        private String contentPieceCount;
        private DeclaredValue declaredValue;
    }

    @Data
    public static class HoldAtLocation {
        private String locationId;
        private LocationContactAndAddress locationContactAndAddress;
        private String locationType;
    }

    @Data
    public static class CustomDeliveryOption {
        private RequestedAppointmentDetail requestedAppointmentDetail;
        private String description;
        private String type;
        private String status;
    }

    @Data
    public static class EstimatedDeliveryTimeWindow {
        private String description;
        private Window window;
        private String type;
    }

    @Data
    public static class PieceCount {
        private String count;
        private String description;
        private String type;
    }

    @Data
    public static class OriginLocation {
        private String locationId;
        private LocationContactAndAddress locationContactAndAddress;
        private String locationType;
    }

    @Data
    public static class RecipientInformation {
        private Contact contact;
        private Address address;
    }

    @Data
    public static class StandardTransitTimeWindow {
        private String description;
        private Window window;
        private String type;
    }

    @Data
    public static class ShipmentDetails {
        private List<Content> contents;
        private boolean beforePossessionStatus;
        private List<Weight> weight;
        // private Weight weight;
        private String contentPieceCount;
        private List<SplitShipment> splitShipments;
    }

    @Data
    public static class ReasonDetail {
        private String description;
        private String type;
    }

    @Data
    public static class ShipperInformation {
        private Contact contact;
        private Address address;
    }

    // Level 3

    @Data
    public static class PackageIdentifier {
        private String type;
        private String value;
        private String trackingNumberUniqueId;
    }

    @Data
    public static class LocationContactAndAddress {
        private Contact contact;
        private Address address;
    }

    @Data
    public static class ScanLocation {
        private String classification;
        private boolean residential;
        private List<String> streetLines;
        private String city;
        private String urbanizationCode;
        private String stateOrProvinceCode;
        private String postalCode;
        private String countryCode;
        private String countryName;
    }

    @Data
    public static class AncillaryDetail {
        private String reason;
        private String reasonDescription;
        private String action;
        private String actionDescription;
    }

    @Data
    public static class DelayDetail {
        private String type;
        private String subType;
        private String status;
    }

    @Data
    public static class Parameter {
        private String key;
        private String value;
    }

    @Data
    public static class DeliveryOptionEligibilityDetail {
        private String option;
        private String eligibility;
    }

    @Data
    public static class PackageDescription {
        private String description;
        private String type;
    }

    @Data
    public static class WeightAndDimensions {
        private List<Weight> weight;
        private List<Dimension> dimensions;
    }

    @Data
    public static class DeclaredValue {
        private String currency;
        private float value;
    }

    @Data
    public static class RequestedAppointmentDetail {
        private String date;
        private List<Window> window;
    }

    @Data
    public static class Content {
        private String itemNumber;
        private String receivedQuantity;
        private String description;
        private String partNumber;
    }

    @Data
    public static class SplitShipment {
        private String pieceCount;
        private String statusDescription;
        private String timestamp;
        private String statusCode;
    }

    // Level 4
    @Data
    public static class Contact {
        private String personName;
        private String phoneNumber;
        private String companyName;
    }

    @Data
    public static class Address {
        private String classification;
        private boolean residential;
        private List<String> streetLines;
        private String city;
        private String urbanizationCode;
        private String stateOrProvinceCode;
        private String postalCode;
        private String countryCode;
    }

    @Data
    public static class Weight {
        private String unit;
        private String value;
    }

    @Data
    public static class Dimension {
        private float length;
        private float width;
        private float height;
        private String units;
    }

    @Data
    public static class Window {
        private String description;
        private SubWindow window;
        private String type;
    }

    @Data
    public static class SubWindow {
        private String begins;
        private String ends;
    }
}
