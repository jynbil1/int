package hanpoom.internal_cron.crons.dashboard.slack.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SlackMessageVO {

    private String product_id;
    private String message;

}
