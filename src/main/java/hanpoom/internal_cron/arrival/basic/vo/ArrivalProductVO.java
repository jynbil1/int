package hanpoom.internal_cron.arrival.basic.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ArrivalProductVO {

	private String wh_arrival_seq;
	private String wh_arrival_date;
	private String wh_arrival_barcode;
	private String wh_arrival_expdate;
	private int wh_arrival_qty;
	private int stocking_arrival_qty;
	private int operation_arrival_qty;
	private String wh_arrival_location;
	private String wh_rearrival_detail_seq;
	private String wh_arrival_productid;
	private String create_user;
	private Date create_time;
	private String update_user;
	private Date update_time;
	private String md_order_detail_seq;
	private String proc_time;
	// Add join colum
	// product
	private String product_name;
	private String wh_in_location;
	private String wh_pick_location;
	private int available_qty;
	private int print_label_qty;

	private String order_date;
	private String arrival_user;
	private int unshippable_qty;
}
