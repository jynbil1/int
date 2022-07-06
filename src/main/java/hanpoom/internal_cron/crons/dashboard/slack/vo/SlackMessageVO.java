package hanpoom.internal_cron.crons.dashboard.slack.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Setter
@Getter
@ToString
public class SlackMessageVO {

    private String product_id;
    private String type;
    private String message;
    private List<String> product_ids;

}
