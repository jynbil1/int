package hanpoom.internal_cron.api.shipment.fedex.enumerate;

public enum FedexShipDuration {
    PRIORITY_OVERNIGHT(2),
    OVERNIGHT(2),
    GROUND(10),
    DAY2(3);

    private final int value;

    private FedexShipDuration(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static FedexShipDuration findByServiceType(String serviceType) {
        switch (serviceType.toUpperCase()) {
            case "PRIORITY_OVERNIGHT":
                return OVERNIGHT;
            case "OVERNIGHT":
                return OVERNIGHT;
            case "GROUND":
                return GROUND;
            case "2DAY":
                return DAY2;
            default:
                return null;
        }
    }
}
