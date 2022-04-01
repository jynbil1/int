package hanpoom.internal_cron.api.shipment.fedex.enumerate;

public enum FedexShipEnum {
    FEDEX_2DAY("FEDEX_2_DAY"),
    FEDEX_2DAY_AM("FEDEX_2_DAY_AM"),
    CUSTOM_CRIT_CHARTER_AIR("FEDEX_CUSTOM_CRITICAL_CHARTER_AIR"),
    CUSTOM_CRIT_AIR_EXPED("FEDEX_CUSTOM_CRITICAL_AIR_EXPEDITE"),
    CUSTOM_CRIT_AIR_EXPED_EXC("FEDEX_CUSTOM_CRITICAL_AIR_EXPEDITE_EXCLUSIVE_USE"),
    CUSTOM_CRIT_AIR_EXPED_NETWORK("FEDEX_CUSTOM_CRITICAL_AIR_EXPEDITE_NETWORK"),
    CUSTOM_CRIT_P2P("FEDEX_CUSTOM_CRITICAL_POINT_TO_POINT"),
    CUSTOM_CRIT_SURFACE_EXPED("FEDEX_CUSTOM_CRITICAL_SURFACE_EXPEDITE"),
    CUSTOM_CRIT_SURFACE_EXPED_EXC("FEDEX_CUSTOM_CRITICAL_SURFACE_EXPEDITE_EXCLUSIVE_USE"),
    DISTANCE_DEFERRED("FEDEX_DISTANCE_DEFERRED"),
    EXP_SAVER("FEDEX_EXPRESS_SAVER"),
    FIRST_OVERNIGHT("FIRST_OVERNIGHT"),
    FIRST_OVERNIGHT_EXTRA_HRS("FEDEX_FIRST_OVERNIGHT_EXTRA_HOURS"),
    GROUND("FEDEX_GROUND"),
    GROUND_HOME("GROUND_HOME_DELIVERY"),
    CARGO_AIRPORT_TO_AIRPORT("FEDEX_CARGO_AIRPORT_TO_AIRPORT"),
    INTL_CONN_PLUS("FEDEX_INTERNATIONAL_CONNECT_PLUS"),
    INTL_ECO("INTERNATIONAL_ECONOMY"),
    INTL_ECO_DIST("INTERNATIONAL_ECONOMY_DISTRIBUTION"),
    INTL_FIRST("INTERNATIONAL_FIRST"),
    CARGO_MAIL("FEDEX_CARGO_MAIL"),
    CARGON_INTL_PREMIUM("FEDEX_CARGO_INTERNATIONAL_PREMIUM"),
    INTL_PRIOR_DIST("INTERNATIONAL_PRIORITY_DISTRIBUTION"),
    INTL_PRIOR_EXP("FEDEX_INTERNATIONAL_PRIORITY_EXPRESS"),
    INTL_PRIOR("FEDEX_INTERNATIONAL_PRIORITY"),
    INTL_PRIOR_PLUS("FEDEX_INTERNATIONAL_PRIORITY_PLUS"),
    PRIORITY_OVERNIGHT("PRIORITY_OVERNIGHT"),
    PRIORITY_OVERNIGHT_EXTRA_HRS("PRIORITY_OVERNIGHT_EXTRA_HOURS"),
    SAME_DAY("SAME_DAY"),
    SAME_DAY_CITY("SAME_DAY_CITY"),
    SMART_POST("SMART_POST"),
    STANDARD_OVERNIGHT_EXTRA_HRS("FEDEX_STANDARD_OVERNIGHT_EXTRA_HOURS"),
    STANDARD_OVERNIGHT("STANDARD_OVERNIGHT"),
    TRANSBORDER_DISTRIBUTION_CONSOLIDATION("TRANSBORDER_DISTRIBUTION_CONSOLIDATION"),
    CUST_CRIT_TMP_ASSURE_AIR("FEDEX_CUSTOM_CRITICAL_TEMP_ASSURE_AIR"),
    CUST_CRIT_TMP_ASSURE_VALID_AIR("FEDEX_CUSTOM_CRITICAL_TEMP_ASSURE_VALIDATED_AIR"),
    CUST_CRIT_WHITE_GLOVE_SERVICE("FEDEX_CUSTOM_CRITICAL_WHITE_GLOVE_SERVICES"),
    REGIONAL_ECONOMY("FEDEX_REGIONAL_ECONOMY"),
    REGIONAL_ECONOMY_FREIGHT("FEDEX_REGIONAL_ECONOMY_FREIGHT"),
    FREIGHT_1DAY("FEDEX_1_DAY_FREIGHT"),
    FREIGHT_2DAY("FEDEX_2_DAY_FREIGHT"),
    FREIGHT_3DAY("FEDEX_3_DAY_FREIGHT"),
    FIRST_OVERNIGHT_FEIGHT("FIRST_OVERNIGHT_FREIGHT"),
    NEXT_DAY_AFTERNOON("FEDEX_NEXT_DAY_AFTERNOON"),
    NEXT_DAY_EARLY_MORNING("FEDEX_NEXT_DAY_EARLY_MORNING"),
    NEXT_DAY_END_OF_DAY("FEDEX_NEXT_DAY_END_OF_DAY"),
    NEXT_DAY_MID_MORNING("FEDEX_NEXT_DAY_MID_MORNING"),
    INTL_ECO_FREIGHT("INTERNATIONAL_ECONOMY_FREIGHT"),
    INTL_PRIOR_FREIGHT("INTERNATIONAL_PRIORITY_FREIGHT");

    private String title;

    FedexShipEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
