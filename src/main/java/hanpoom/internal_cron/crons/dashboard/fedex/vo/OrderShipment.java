package hanpoom.internal_cron.crons.dashboard.fedex.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderShipment {
    private int orderNo;
    private String orderDate;
    private String shipmentClass;
    private String trackingNo;
    private String serviceType;
}
