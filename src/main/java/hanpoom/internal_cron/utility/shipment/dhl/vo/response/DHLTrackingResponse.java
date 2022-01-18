package hanpoom.internal_cron.utility.shipment.dhl.vo.response;

import java.util.List;

import hanpoom.internal_cron.utility.shipment.dhl.vo.response.element.Consignee;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.element.PieceDetail;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.element.ServiceArea;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.element.ShipmentDetail;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.element.ShipmentEvent;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.element.Shipper;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.element.Status;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Setter
public class DHLTrackingResponse {

    @Setter
    private String trackingNo;

    private ServiceArea serviceArea;

    private Shipper shipper;
    private Consignee consignee;
    private ShipmentDetail shipmentDetail;
    private List<ShipmentEvent> shipmentEvents;
    private PieceDetail pieceDetail;

    private Status status;
}
