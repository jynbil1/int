package hanpoom.internal_cron.api.shipment.fedex.enumerate;

public enum FedexShipDuration {
    PRIORITY_OVERNIGHT(2),
    OVERNIGHT(2),
    GROUND(7),
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
            case "OVERNIGHT":
            case "STANDARD_OVERNIGHT":
                return OVERNIGHT;
            case "GROUND":
            case "FEDEX_GROUND":
            case "GROUND_HOME_DELIVERY":
                return GROUND;
            case "2DAY":
            case "FEDEX_2_DAY":
                return DAY2;
            default:
                return null;
        }
    }
}
