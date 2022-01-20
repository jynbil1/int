package hanpoom.internal_cron.utility.shipment.dhl.vo.response.element;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class ShipmentDetail {
    private int noOfPiece;
    private float weight;
    private String weightUnit;
    private String serviceType;
    private String shipmentDesc;

    private String shippedDate;

    @Setter
    private String signatory;
    @Setter
    private int shipmentReference;

    public void setShipmentDetail(int noOfPiece,
            float weight, String weightUnit, String serviceType,
            String shipmentDesc, int shipmentReference,
            String shippedDate) {
        this.noOfPiece = noOfPiece;
        this.weight = weight;
        this.weightUnit = weightUnit;
        this.serviceType = serviceType;
        this.shipmentDesc = shipmentDesc;
        this.shipmentReference = shipmentReference;
        this.shippedDate = shippedDate;
    }
}
