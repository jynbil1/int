package hanpoom.internal_cron.utility.email.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EmailCredentialsVO {
    private String smtp_server;
    private Integer smtp_port;
    private String smtp_user;
    private String smtp_password;
}
