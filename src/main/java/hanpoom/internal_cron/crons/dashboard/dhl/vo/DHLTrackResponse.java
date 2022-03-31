package hanpoom.internal_cron.crons.dashboard.dhl.vo;

import java.util.Arrays;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DHLTrackResponse {
    // @SerializedName(value = "ArrayOfAWBInfoItem")
    // private AWBInfo awbInfo;

    // @Data
    // public static class AWBInfo {
        @SerializedName(value = "AWBNumber")
        private String trackingNumber;

        @SerializedName(value = "Status")
        private Status status;

        @SerializedName(value = "ShipmentInfo")
        private ShipmentInfo shipmentInfo;
    // }

    @Data
    public static class Status {
        @SerializedName(value = "ActionStatus")
        private String actionStatus;
        @SerializedName(value = "Condition")
        private Condition condition;
    }

    @Data
    public static class Condition {
        @SerializedName(value = "ArrayOfConditionItem")
        private ConditionItem conditionItem;
    }

    @Data
    public static class ConditionItem {
        @SerializedName(value = "ConditionCode")
        private String conditionCode;
        @SerializedName(value = "ConditionData")
        private String conditionData;
    }

    @Data
    public static class ShipmentInfo {
        @SerializedName(value = "OriginServiceArea")
        private ServiceArea originServiceArea;

        @SerializedName(value = "DestinationServiceArea")
        private ServiceArea destinationServiceArea;

        @SerializedName(value = "ShipperName")
        private String shipperName;
        @SerializedName(value = "ConsigneeName")
        private String consigneeName;
        @SerializedName(value = "ShipmentDate")
        private String shipmentDate;
        @SerializedName(value = "Pieces")
        private int pieces;
        @SerializedName(value = "Weight")
        private float weight;
        @SerializedName(value = "WeightUnit")
        private String weightUnit;
        @SerializedName(value = "ServiceType")
        private String serviceType;
        @SerializedName(value = "ShipmentDescription")
        private String shipmentDescription;

        @SerializedName(value = "Shipper")
        private Address shipper;

        @SerializedName(value = "Consignee")
        private Address consignee;

        @SerializedName(value = "ShipmentEvent")
        private ShipmentEvent shipmentEvent;

        @SerializedName(value = "ShipperReference")
        private ShipperReference shipperReference;
    }

    @Data
    public static class ServiceArea {
        @SerializedName(value = "ServiceAreaCode")
        private String serviceAreaCode;

        @SerializedName(value = "Description")
        private String description;

        @SerializedName(value = "FacilityCode")
        private String facilityCode;
    }

    @Data
    public static class Address {
        @SerializedName(value = "City")
        private String city;

        @SerializedName(value = "Suburb")
        private String suburb;

        @SerializedName(value = "StateOrProvinceCode")
        private String StateOrProvinceCode;

        @SerializedName(value = "PostalCode")
        private String postalCode;

        @SerializedName(value = "CountryCode")
        private String countryCode;
    }

    @Data
    public static class ShipmentEvent {
        @SerializedName(value = "ArrayOfShipmentEventItem")
        private List<ShipmentEventItem> shipmentEventItems;

        public void setShipmentEventItems(List<ShipmentEventItem> items) {
            this.shipmentEventItems = items;
        }

        public void setShipmentEventItems(ShipmentEventItem item) {
            this.shipmentEventItems = Arrays.asList(item);
        }
    }

    @Data
    public static class ShipmentEventItem {
        @SerializedName(value = "Date")
        private String date;

        @SerializedName(value = "Time")
        private String time;

        @SerializedName(value = "ServiceEvent")
        private ServiceEvent serviceEvent;

        @SerializedName(value = "ServiceArea")
        private ServiceArea serviceArea;

        @SerializedName(value = "EventRemarks")
        private EventRemark eventRemark;
    }

    @Data
    public static class ServiceEvent {
        @SerializedName(value = "EventCode")
        private String eventCode;
        @SerializedName(value = "Description")
        private String description;
    }

    @Data
    public static class EventRemark{
        @SerializedName(value = "FurtherDetails")
        private String furtherDetail;
        @SerializedName(value = "NextSteps")
        private String nextStep;
    }

    @Data
    public static class ShipperReference {
        @SerializedName(value = "ReferenceID")
        private String referenceId;
    }
}
