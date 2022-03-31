package hanpoom.internal_cron.api.fedex.vo.ship.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnrefinedShipData {
    private int orderNo;
    private int shipmentNo;
    private String customerName;
    private String addressLine;
    private String city;
    private String state;
    private int postalCode;
    private String countryCode;
    
    private float orderValue;
    
    private int productId;
    private String productName;
    private float cog;
    private float unitPrice;
    private float discount;
    private int qty;
    private float lineTotal;

}
