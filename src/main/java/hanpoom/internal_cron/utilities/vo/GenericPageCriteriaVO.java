package hanpoom.internal_cron.utilities.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class GenericPageCriteriaVO {
    private String page_id;
    private String item_range;
    private String sort_by;
    private String order_by;
}
