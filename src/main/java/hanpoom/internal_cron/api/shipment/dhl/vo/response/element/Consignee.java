package hanpoom.internal_cron.api.shipment.dhl.vo.response.element;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Consignee {
    private String name;
    private String city;
    private String suburb;
    private String stateOrProviceCode;
    private String postalCode;
    private String countryCode;

    public void setConsigneeDetail(String name, String city,
            String suburb, String stateOrProviceCode, String postalCode,
            String countryCode) {
        this.name = name;
        this.city = city;
        this.suburb = suburb;
        this.stateOrProviceCode = stateOrProviceCode;
        this.postalCode = postalCode;
        this.countryCode = countryCode;
    }
}
