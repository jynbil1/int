package hanpoom.internal_cron.utility.shipment.dhl.vo.response.element;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ServiceArea {
    private String originServiceAreaCode;
    private String originDescription;
    private String originFacilityCode;
    private String destinationServiceAreaCode;
    private String destinationDescription;
    private String destinationFacilityCode;

    public void setOriginArea(String serviceAreaCode, String desc) {
        this.originServiceAreaCode = serviceAreaCode;
        this.originDescription = desc;
    }

    public void setOriginArea(String serviceAreaCode, String desc, String facilityCode) {
        this.originServiceAreaCode = serviceAreaCode;
        this.originDescription = desc;
        this.originFacilityCode = facilityCode;
    }

    public void setDestinationArea(String serviceAreaCode, String desc) {
        this.destinationServiceAreaCode = serviceAreaCode;
        this.destinationDescription = desc;
    }

    public void setDestinationArea(String serviceAreaCode, String desc, String facilityCode) {
        this.destinationServiceAreaCode = serviceAreaCode;
        this.destinationDescription = desc;
        this.destinationFacilityCode = facilityCode;
    }
}
