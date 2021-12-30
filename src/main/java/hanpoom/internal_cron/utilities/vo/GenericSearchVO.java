package hanpoom.internal_cron.utilities.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GenericSearchVO {
    private int page;
    private int item_range;
    private String keyword;
    private int sort;
    private int order;

    private int start_page;
    
    private String date;

    // order-transaction
    private int is_urgent; 
    private int see_all;    
    private int is_today;

    private int banjjak_order;

    public GenericSearchVO(){
        this.page = 1;
        this.item_range = 10;
        this.keyword = "";

        this.sort = 1;
        this.order = 0;
        this.start_page = 0;

        this.is_urgent = 0;
        this.date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
