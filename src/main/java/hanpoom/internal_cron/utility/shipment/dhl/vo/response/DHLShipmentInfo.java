package hanpoom.internal_cron.utility.shipment.dhl.vo.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import hanpoom.internal_cron.utility.shipment.dhl.vo.response.sub.DHLShipmentEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DHLShipmentInfo {
    @Getter
    @ToString
    @Setter
    private class OriginServiceArea {
        @JsonProperty("Description")
        private String originAreaDescription;

        @JsonProperty("ServiceAreaCode")
        private String originAreaCode;
    }

    @Getter
    @ToString
    @Setter
    private class DestinationServiceArea {
        @JsonProperty("Description")
        private String destinationAreaDescription;

        @JsonProperty("ServiceAreaCode")
        private String destinationAreaCode;

        @JsonProperty("FacilityCode")
        private String destinationFacilityCode;
    }

    @Getter
    @ToString
    @Setter
    private class ShipperReference {
        @JsonProperty("ReferenceID")
        private int referenceId;

    }

    @Setter
    @ToString
    @Getter
    private class Consignee {
        @JsonProperty("PostalCode")
        private String postalCode;

        @JsonProperty("City")
        private String city;

        @JsonProperty("CountryCode")
        private String countryCode;

        @JsonProperty("StateOrProvinceCode")
        private String stateOrProvinceCode;
    }

    @Setter
    @ToString
    @Getter
    private class Shipper {
        @JsonProperty("Suburb")
        private String suburb;

        @JsonProperty("PostalCode")
        private int postalCode;

        @JsonProperty("City")
        private String city;

        @JsonProperty("CountryCode")
        private String countryCode;

        @JsonProperty("StateOrProvinceCode")
        private String stateOrProvinceCode;
    }

    // Declaration
    private OriginServiceArea originServiceArea;
    private DestinationServiceArea destinationServiceArea;

    @JsonProperty("ShipperName")
    private String shipperName;

    private ShipperReference shipperReference;

    @JsonProperty("WeightUnit")
    private String weightUnit;

    @JsonProperty("Weight")
    private float weight;

    private Consignee consignee;

    @JsonProperty("ShipmentEvent")
    private DHLShipmentEvent shipmentEvent;

    @JsonProperty("ConsigneeName")
    private String consigneeName;

    @JsonProperty("ShipmentDescription")
    private String shipmentDescription;

    @JsonProperty("ShipmentDate")
    private String shipmentDate;

    @JsonProperty("ServiceType")
    private String serviceType;

    private Shipper shipper;

    @JsonProperty("Pieces")
    private int pieces;

}
