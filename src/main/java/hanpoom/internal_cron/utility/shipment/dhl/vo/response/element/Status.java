package hanpoom.internal_cron.utility.shipment.dhl.vo.response.element;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Status {
    private String actionStatus;
    private String conditionCode;
    private String conditionData;

    public void setStatus(String actionStatus,
            String conditionCode, String conditionData) {
        this.actionStatus = actionStatus;
        this.conditionCode = conditionCode;
        this.conditionData = conditionData;
    }
}
