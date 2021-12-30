package hanpoom.internal_cron.utilities.email.service;

import hanpoom.internal_cron.utilities.email.mapper.EmailingServiceMapper;
import hanpoom.internal_cron.utilities.email.vo.EmailContentVO;
import hanpoom.internal_cron.utilities.email.vo.EmailCredentialsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.List;
import java.util.Objects;
import java.util.Properties;


@Service
public class GenericEmailingService {

    public static final String MAIL_SMTP_HOST = "mail.smtp.host";
    public static final String SMTP_ZEPTOMAIL = "smtp.zeptomail.com";
    public static final String MAIL_SMTP_PORT = "mail.smtp.port";
    public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    public static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    public static final String MAIL_SMTP_FROM = "mail.smtp.from";
    public static final String MAIL_SMTP_SSL_PROTOCOLS = "mail.smtp.ssl.protocols";
    public static final String PORT_VALUE = "587";
    public static final String TRUE = "true";
    public static final String FROM_ADDRESS = "fromaddress";
    public static final String SSL_PROTOCOL = "TLSv1.2";
    public static final String SMT_PROTOCOL = "smtp";
    public static final String CONTENT_TYPE = "text/html; charset=UTF-8";
    public static final String CHARSET = "UTF-8";

    private EmailingServiceMapper emailingServiceMapper;

    @Autowired
    public GenericEmailingService(EmailingServiceMapper emailingServiceMapper) {
        this.emailingServiceMapper = emailingServiceMapper;
    }

    public void sendEmail(EmailContentVO email_content) {
        Properties properties = getDefaultEmailProperties();
        Session session = Session.getDefaultInstance(properties);
        EmailCredentialsVO email_credentials = getEmailCredentials();
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email_content.getFrom_email()));
            List<String> email_addresses = email_content.getEmail_recipients();
            if (email_addresses != null && !email_addresses.isEmpty()) {
                email_addresses.stream().filter(Objects::nonNull).forEach(recipient -> {
                    try {
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(email_content.getTo_email()));
            }
            if (email_content.getBcc_email() != null){
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(email_content.getBcc_email()));
            }
            message.setSubject(email_content.getSubject(), CHARSET);
            message.setContent(email_content.getEmail_content(), CONTENT_TYPE);
            Transport transport = session.getTransport(SMT_PROTOCOL);
            assert email_credentials != null;
            transport.connect(email_credentials.getSmtp_server(), email_credentials.getSmtp_port(),
                    email_credentials.getSmtp_user(), email_credentials.getSmtp_password());
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            System.out.println("Mail successfully sent!~");
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
    }

    public void sendEmailWithAttachment(EmailContentVO email_content) {
        Properties properties = getDefaultEmailProperties();
        Session session = Session.getDefaultInstance(properties);
        EmailCredentialsVO email_credentials = getEmailCredentials();
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email_content.getFrom_email()));
            List<String> email_addresses = email_content.getEmail_recipients();
            if (email_addresses != null && !email_addresses.isEmpty()) {
                email_addresses.stream().filter(Objects::nonNull).forEach(recipient -> {
                    try {
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(email_content.getTo_email()));
            }

            List<String> email_cc_add = email_content.getCc_email();
            if (email_cc_add != null && !email_cc_add.isEmpty()){
                email_cc_add.stream().filter(Objects::nonNull).forEach(cc_recipient -> {
                    try {
                        message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc_recipient));
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                });
            }

            if (email_content.getBcc_email() != null){
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(email_content.getBcc_email()));
            }
            message.setSubject(email_content.getSubject(), CHARSET);

            MimeBodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setContent(email_content.getEmail_text(), CONTENT_TYPE);

            MimeBodyPart messageBodyPart2 = new MimeBodyPart();
            messageBodyPart2.setDataHandler(
                    new DataHandler(email_content.getAttachment(), email_content.getAttachment_mime_type()));
            messageBodyPart2.setFileName(email_content.getAttachment_name());

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart1);
            multipart.addBodyPart(messageBodyPart2);

            message.setContent(multipart);

            Transport transport = session.getTransport(SMT_PROTOCOL);
            assert email_credentials != null;
            transport.connect(email_credentials.getSmtp_server(), email_credentials.getSmtp_port(),
                    email_credentials.getSmtp_user(), email_credentials.getSmtp_password());
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            System.out.println("Mail successfully sent!~");
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
    }

    private EmailCredentialsVO getEmailCredentials() {
        try {
            return emailingServiceMapper.getEmailCredentialsVO();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Properties getDefaultEmailProperties() {
        Properties properties = System.getProperties();
        properties.setProperty(MAIL_SMTP_HOST, SMTP_ZEPTOMAIL);
        properties.put(MAIL_SMTP_PORT, PORT_VALUE);
        properties.put(MAIL_SMTP_AUTH, TRUE);
        properties.put(MAIL_SMTP_STARTTLS_ENABLE, TRUE);
        properties.put(MAIL_SMTP_FROM, FROM_ADDRESS);
        properties.put(MAIL_SMTP_SSL_PROTOCOLS, SSL_PROTOCOL);
        return properties;
    }
}
