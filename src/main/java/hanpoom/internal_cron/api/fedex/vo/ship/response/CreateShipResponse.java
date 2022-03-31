package hanpoom.internal_cron.api.fedex.vo.ship.response;

import java.util.List;

import lombok.Data;

@Data
public class CreateShipResponse {
    private String transactionId;
    private String customerTransactionId;
    private Output output;

    @Data
    public static class Output {
        private List<TransactionShipment> transactionShipments;
        private List<Alert> alerts;
        private String jobId;
    }

    @Data
    public static class TransactionShipment {
        private String serviceType;
        private String shipDatestamp;
        private String serviceCategory;
        private List<Document> shipmentDocuments;
        private List<PieceResponse> pieceResponses;
        private String serviceName;
        private List<Alert> alerts;
        private CompletedShipmentDetail completedShipmentDetail;
        private ShipmentAdvisoryDetails shipmentAdvisoryDetails;
        private String masterTrackingNumber;
    }

    @Data
    public static class Document {
        private String contentKey;
        private int copiesToPint;
        private String contentType;
        private String trackingNumber;
        private String docType;
        private List<Alert> alerts;
        private String encodedLabel;
        private String url;
    }

    @Data
    public static class PieceResponse {
        private float netChargeAmount;
        private List<TransactionDetail> transactionDetails;
        private List<Document> packageDocument;
        private String acceptanceTrackingNumber;
        private String serviceCategory;
        private String listCustomerTotalCharge;
        private String deliveryTimestamp;
        private String trackingIdType;
        private float additionalChargesDiscount;
        private float netListRateAmount;
        private float baseRateAmount;
        private float packageSequenceNumber;
        private float netDiscountAmount;
        private float codcollectionAmount;
        private String masterTrackingNumber;
        private String acceptaceType;
        private String trackingNumber;
        private boolean successful;
        private List<CustomerReference> customerReferences;
    }

    @Data
    public static class CompletedShipmentDetail {
        private List<CompletedPackageDetail> completedPackageDetails;
        private OperationalDetail operationalDetail;
        private String carrierCode;
        private CompletedHoldAtLocationDetail completedHoldAtLocationDetail;
        private CompletedEtdDetail completedEtdDetail;
        private String packagingDescription;
        private MasterTrackingId masterTrackingId;
        private ServiceDescription serviceDescription;
        private boolean usDomestic;
        private HazardousShipmentDetail hazardousShipmentDetail;
        private ShipmentRating shipmentRating;
        private DocumentRequirements documentRequirements;
        private String exportComplianceStatement;
        private AccessDetail accessDetail;
    }

    @Data
    public static class CompletedPackageDetail {
        private int sequenceNumber;
        private OperationalDetail operationalDetail;
        private String signatureOption;
        private List<TrackingId> trackingIds;
        private int groupNumber;
        private String oversizeClass;
        private PackageRating packageRating;
        private Weight dryIceWeight;
        private HazardousPackageDetail hazardousPackageDetail;
    }

    @Data
    public static class PackageRating {
        private int effectiveNetDiscount;
        private String actualRateType;
        private List<PackageRateDetail> packageRateDetails;
    }

    @Data
    public static class PackageRateDetail {
        private String ratedWeightMethod;
        private float totalFreightDiscounts;
        private float totalTaxes;
        private String minimumChargeType;
        private float baseCharge;
        private float totalRebates;
        private String rateType;
        private Weight billingWeight;
        private float netFreight;
        private List<Surcharge> surcharges;
        private float totalSurcharges;
        private float netFedExCharge;
        private float netCharge;
        private String currency;
    }

    @Data
    public static class Surcharge {
        private float amount;
        private String surchargeType;
        private String level;
        private String description;
    }

    @Data
    public static class TrackingId {
        private String formId;
        private String trackingIdType;
        private String uspsApplicationId;
        private String trackingNumber;
    }

    @Data
    public static class Weight {
        private String units;
        private int value;
    }

    @Data
    public static class OperationalDetail {
        private String originServiceArea;
        private String serviceCode;
        private String airportId;
        private String postalCode;
        private String scac;
        private String deliveryDay;
        private String originLocationId;
        private String countryCode;
        private String astraDescription;
        private String originLocationNumber;
        private String deliveryDate;

        private List<String> deliveryEligibilities;
        private boolean ineligibleForMoneyBackGuarantee;

        private String maximumTransitTime;
        private String destinationLocationStateOrProvinceCode;
        private String astraPlannedServiceLevel;
        private String destinationLocationId;
        private String transitTime;
        private String stateOrProvinceCode;
        private int destinationLocationNumber;

        private String packagingCode;
        private String commitDate;
        private String publishedDeliveryTime;
        private String ursaSuffixCode;
        private String ursaPrefixCode;
        private String destinationServiceArea;
        private String commitDay;
        private String customTransitTime;
    }

    @Data
    public static class CompletedHoldAtLocationDetail {
        private String holdingLocationType;
        private HoldingLocation holdingLocation;
    }

    @Data
    public static class HoldingLocation {
        private Address address;
        private Contact contact;
    }

    @Data
    public static class Contact {
        private String personName;
        private String tollFreePhoneNumber;
        private String emailAddress;
        private String phoneNumber;
        private String phoneExtension;
        private String faxNumber;
        private String pagerNumber;
        private String companyName;
        private String title;
    }

    @Data
    public static class Address {
        private List<String> streetLines;
        private String city;
        private String stateOrProvinceCode;
        private String postalCode;
        private String countryCode;
        private boolean residential;
    }

    @Data
    public static class CompletedEtdDetail {
        private String folderId;
        private String type;
        private List<UploadDocumentReferenceDetail> uploadDocumentReferenceDetails;
    }

    @Data
    public static class UploadDocumentReferenceDetail {
        private String documentType;
        private String documentReference;
        private String description;
        private String documentId;
    }

    @Data
    public static class MasterTrackingId {
        private String formId;
        private String trackingIdType;
        private String uspsApplicationId;
        private String trackingNumber;
    }

    @Data
    public static class ServiceDescription {
        private String serviceType;
        private String code;
        private List<Name> names;
        private List<String> operatingOrgCodes;
        private String astraDescription;
        private String description;
        private String serviceId;
        private String serviceCategory;

    }

    @Data
    public static class Name {
        private String type;
        private String encoding;
        private String value;
    }

    @Data
    public static class HazardousShipmentDetail {
        private HazardousSummaryDetail hazardousSummaryDetail;
        private AdrLicense adrLicense;
        private DryIceDetail dryIceDetail;
    }

    @Data
    public static class HazardousSummaryDetail {
        private int smallQuantityExceptionPackageCount;
    }

    @Data
    public static class AdrLicense {
        private LicenseOrPermitDetail licenseOrPermitDetail;
    }

    @Data
    public static class LicenseOrPermitDetail {
        private String number;
        private String effectiveDate;
        private String expirationDate;
    }

    @Data
    public static class DryIceDetail {
        private Weight totalWeight;
        private int pacakageCount;
        private ProcessingOptions processingOptions;
    }

    @Data
    public static class ProcessingOptions {
        private List<String> options;
    }

    @Data
    public static class HazardousPackageDetail {
        private String regulation;
        private String accessibility;
        private String labelType;
        private List<Container> containers;
        private boolean cargoAircraftOnly;
        private String referenceId;
        private float radioactiveTransportIndex;
    }

    @Data
    public static class Container {
        private int qvalue;
        private List<HazardousCommodity> hazardousCommodities;
    }

    @Data
    public static class HazardousCommodity {
        private Quantity quantity;
        private List<Option> options;
        private Description description;
        private NetExplosiveDetail netExplosiveDetail;
        private int massPoints;
    }

    @Data
    public static class NetExplosiveDetail {
        private int amount;
        private String units;
        private String type;
    }

    @Data
    public static class Quantity {
        private String quantityType;
        private float amount;
        private String units;
    }

    @Data
    public static class Option {
        private Quantity quantity;
        private List<InnerReceptacle> innerReceptacles;
        private List<OptionOption> options;
        private Description description;
    }

    @Data
    public static class OptionOption {
        private String labelTextOption;
        private String customerSuppliedLabelText;
    }

    @Data
    public static class InnerReceptacle {
        private Quantity quantity;
    }

    @Data
    public static class Description {
        private int sequenceNumber;
        private List<String> processingOptions;
        private List<String> subsidiaryClasses;
        private String labelText;
        private String technicalName;
        private PackingDetails packingDetails;

        private String authorization;
        private boolean reportableQuantity;
        private float percentage;
        private String id;

        private String packingGroup;
        private String properShippingName;
        private String hazardClass;
    }

    @Data
    public static class PackingDetails {
        private String packingInstructions;
        private boolean cargoAircraftOnly;
    }

    @Data
    public static class ShipmentRating {
        private String actualRateType;
        private ShipmentRateDetails shipmentRateDetails;
    }

    @Data
    public static class ShipmentRateDetails {
        private String rateZone;
        private String ratedWeightMethod;
        private float totalDutiesTaxesAndFees;
        private String pricingCode;
        private float totalFreightDiscounts;
        private float totalTaxes;
        private float totalDutiesAndTaxes;
        private float totalAncillaryFeesAndTaxes;
        private List<Tax> taxes;
        private float totalRebates;
        private float fuelSurchargePercent;
        private CurrencyExchangeRate currencyExchangeRate;
        private float totalNetFreight;
        private float totalNetFedExCharge;
        private List<ShipmentLegRateDetail> shipmentLegRateDetails;
        private float dimDivisor;
        private String rateType;
        private List<Surcharge> surcharges;
        private float totalSurcharges;
        private Weight totalBillingWeight;
        private List<FreightDiscount> freightDiscounts;
        private String rateScale;
        private float totalNetCharge;
        private float totalBaseCharge;
        private float totalNetChargeWithDutiesAndTaxes;
        private String currency;
    }

    @Data
    public static class Tax {
        private int amount;
        private String level;
        private String description;
        private String type;
    }

    @Data
    public static class CurrencyExchangeRate {
        private float rate;
        private String fromCurrency;
        private String intoCurrency;
    }

    @Data
    public static class ShipmentLegRateDetail {
        private String rateZone;
        private String pricingCode;
        private List<Tax> taxes;
        private Weight totalDimWeight;
        private int totalRebates;
        private int fuelSurchargePercent;
        private CurrencyExchangeRate currencyExchangeRate;
        private int dimDivisor;
        private String rateType;
        private String legDestinationLocationId;
        private String dimDivisorType;
        private int totalBaseCharge;
        private String ratedWeightMethod;
        private float totalFreightDiscounts;
        private float totalTaxes;
        private String minimumChargeType;
        private float totalDutiesAndTaxes;
        private float totalNetFreight;
        private float totalNetFedExCharge;

        private List<Surcharge> surcharges;
        private int totalSurcharges;
        private Weight totalBillingWeight;
        private List<FreightDiscount> freightDiscounts;
        private String rateScale;
        private float totalNetCharge;
        private float totalNetChargeWithDutiesAndTaxes;
        private String currency;
    }

    @Data
    public static class FreightDiscount {
        private float amount;
        private String rateDiscountType;
        private float percent;
        private String description;
    }

    @Data
    public static class DocumentRequirements {
        private List<String> requiredDocuments;
        private List<String> prohibitedDocuments;
        private List<GenerationDetail> generationDetails;
    }

    @Data
    public static class GenerationDetail {
        private String type;
        private String minimumCopiesRequired;
        private String letterhead;
        private String electronicSignature;
    }

    @Data
    public static class AccessDetail {
        private List<AccessorDetail> accessDetails;
    }

    @Data
    public static class AccessorDetail {
        private String password;
        private String role;
        private String emailLabelUrl;
        private String userId;
    }

    @Data
    public static class ShipmentAdvisoryDetails {
        private RegularAdvisory regularAdvisory;
    }

    @Data
    public static class RegularAdvisory {
        private List<CommodityClarification> commondityClarifications;
        private List<Prohibition> prohibitions;
    }

    @Data
    public static class CommodityClarification {
        private int commondityIndex;
        private List<Suggestion> suggestions;
    }

    @Data
    public static class Suggestion {
        private String description;
        private String harmonizedCode;
    }

    @Data
    public static class Prohibition {
        private String derivedHarmonizedCode;
        private Advisory advisory;
        private int commondityIndex;
        private String source;
        private List<String> categories;
        private String type;
        private Waiver waiver;
        private String status;
    }

    @Data
    public static class Advisory {
        private String code;
        private String text;
        private List<Parameter> parameters;
        private String localizedText;
    }

    @Data
    public static class Parameter {
        private String id;
        private String value;
    }

    @Data
    public static class Waiver {
        private List<Advisory> advisories;
        private String description;
        private String id;
    }

    @Data
    public static class Alert {
        private String code;
        private String alertType;
        private String message;
    }

    @Data
    public static class TransactionDetail {
        private String transactionDetails;
        private String transactionId;
    }

    @Data
    public static class CustomerReference {
        private String customerReferenceType;
        private String value;
    }
}