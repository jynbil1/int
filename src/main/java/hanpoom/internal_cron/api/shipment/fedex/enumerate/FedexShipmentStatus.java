package hanpoom.internal_cron.api.shipment.fedex.enumerate;

public enum FedexShipmentStatus {
    DELIVERED("ACTUAL_DELIVERY"),
    SHIPPED("ACTUAL_PICKUP"),
    TENDER("ACTUAL_TENDER"),
    EXPECT_TENDER("ANTICIPATED_TENDER"),
    APPOINTMENT_DELIVERY("APPOINTMENT_DELIVERY"),
    ATTEMPTED_DELIVERY("ATTEMPTED_DELIVERY"),
    COMMITMENT("COMMITMENT"),
    ESTIMATED_ARRIVAL_AT_GATEWAY("ESTIMATED_ARRIVAL_AT_GATEWAY"),
    ESTIMATED_DELIVERY("ESTIMATED_DELIVERY"),
    ESTIMATED_PICKUP("ESTIMATED_PICKUP"),
    ESTIMATED_RETURN_TO_STATION("ESTIMATED_RETURN_TO_STATION"),
    SHIP("SHIP"),
    SHIPMENT_DATA_RECEIVED("SHIPMENT_DATA_RECEIVED");

    private final String value;

    private FedexShipmentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
