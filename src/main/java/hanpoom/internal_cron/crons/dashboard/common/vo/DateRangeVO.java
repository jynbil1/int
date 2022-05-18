package hanpoom.internal_cron.crons.dashboard.common.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DateRangeVO {
    private String start_date;
    private String end_date;

    public DateRangeVO(String start_date, String end_date) {
        this.start_date = start_date;
        this.end_date = end_date;
    }
}
