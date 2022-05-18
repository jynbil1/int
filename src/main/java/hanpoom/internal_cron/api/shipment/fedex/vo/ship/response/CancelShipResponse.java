package hanpoom.internal_cron.api.shipment.fedex.vo.ship.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CancelShipResponse {
    private String transactionId;
    private String customerTransactionId;
    private Output output;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class Output {
    private boolean cancelledShipment;
    private boolean cancelledHistory;
    private String successMessage;
    private List<Alert> alerts;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class Alert {
    private String code;
    private String alertType;
    private String message;
}
