package hanpoom.internal_cron.utilities.email.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EmailRecipientVO {
    private String receiver_seq;
    private String receiver_name;
    private String email;
    private String cc;
    private String company;
    private String use_flag;
}
