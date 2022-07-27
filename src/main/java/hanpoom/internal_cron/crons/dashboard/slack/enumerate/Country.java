package hanpoom.internal_cron.crons.dashboard.slack.enumerate;

import java.util.Arrays;
import java.util.List;

public enum Country {
    KOREA("KR"),
    US("US");

    private String countryCode;

    Country(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return this.countryCode;
    }
}
