package hanpoom.internal_cron.crons.dashboard.common.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class APIResponse {
    protected int statusCode;
    protected String message;
    protected String strObject;
    protected Object object;

}
