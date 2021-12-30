package hanpoom.internal_cron.utilities.vo;

public enum ValidationMessageType {

    INVALID_REQUEST("400", "Invalid Request!"),
    AUTHENTICATION_FAILED("401", "Log in Failed!"),
    VALIDATION_SUCCESS("200", "Validation Successful!"),
    FOR_VALIDATION("200", "Order Number for Validation!"),
    VALIDATED("200", "Order Number has been Validated!"),
    VALIDATION_ERROR("200", "Error in Validating Request!"),
    INTERNAL_SERVER_ERROR("500", "Internal Server Error!");

    private final String code;
    private final String message;

    ValidationMessageType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
