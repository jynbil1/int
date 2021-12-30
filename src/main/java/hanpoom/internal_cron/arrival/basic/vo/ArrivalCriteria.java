package hanpoom.internal_cron.arrival.basic.vo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ArrivalCriteria {
    private int current_page_no;
    private int records_per_page;
    private int page_range;
    private int item_index;
    
    private int total_items;
    private int last_page_no;

    private String search_keyword;
    private String search_type;
    private String order_keyword = "none";
    private String order_asc = "0";
    
    private List<Integer> items_per_page_range;
    private Map<String, String> search_types;
    private Map<String, String> order_keywords;
    private Map<String, String> order_ascs;

    public ArrivalCriteria(){
        this.current_page_no = 1;
        this.records_per_page = 10;
        this.page_range = 10;
        this.item_index = 0;

        this.search_type = "product_name";
        this.order_keyword = "none";
        this.order_asc = "0";
        this.items_per_page_range = Arrays.asList(10, 20, 30, 40, 50, 100, 150, 200, 500, 1000);
        this.search_types = Map.of("product_id", "상품 ID",
                                    "product_name", "상품명",
                                    "arrival_date", "입하일자",
                                    "exp_date", "유통기한",
                                    "create_user", "처리자");
        this.order_keywords = Map.of("none", "정렬기준",
                                    "arrival_date", "입하일자",
                                    "exp_date", "유통기한",
                                    "create_user", "처리자");
        this.order_ascs = Map.of("0", "오름차순",
                                    "1", "내림차순");
    }
}
