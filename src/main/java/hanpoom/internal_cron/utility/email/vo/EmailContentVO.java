package hanpoom.internal_cron.utility.email.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
public class EmailContentVO {


    private String subject;
    private String email_text;
    private String email_content;//<- must be html string

    private byte [] attachment;
    private String attachment_mime_type;
    private String attachment_name;

    private String from_name;
    private String from_email;
    private String from_contact_no;

    private String to_name;
    private String to_email;
    private String to_contact_no;

    private List<String> cc_email = new ArrayList<>();
    private String bcc_email;
    private List<String> email_recipients = new ArrayList<>();

    private String salutation;
    private String tbd;

    private String b64_attachment;
    
}
