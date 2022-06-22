package hanpoom.internal_cron.crons.dashboard.slack.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ExpirationVO {

    private String product_id;
    private String product_name;
    private String warehouse;
    private String location;
    private String expiration_date;
    private int available_qty;
    private float level;

}
