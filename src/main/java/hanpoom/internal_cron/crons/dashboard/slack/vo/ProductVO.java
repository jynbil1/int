package hanpoom.internal_cron.crons.dashboard.slack.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@ToString
public class ProductVO {

    private String product_id;
    private String product_name;
    private Date sale_start_datetime;
    private Date sale_end_datetime;
    private String cost;
    private String regular_price;
    private String sale_price;
    private String price;
    private String country;

}
