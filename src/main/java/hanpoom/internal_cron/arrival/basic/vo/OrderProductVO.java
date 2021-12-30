package hanpoom.internal_cron.arrival.basic.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class OrderProductVO {

	private String product_id;
	private String company_name;
	private String product_name;
	private String barcode;
	private String order_date;
	private int order_qty;
	private int unit_bundle;
	private int unit_box;
	private int arrival_qty;
	private int available_qty;
	private int site_qty;
	private int print_label_qty;
	private String md_order_detail_seq;
	private String create_user; //나중에 발주기록을 따로할수도 있기때문에 남겨둠
	private Date create_time;
	private String update_user;
	private Date update_time;
	private Integer has_shipment_validated;


}
