package hanpoom.internal_cron.utility.spreadsheet.config;

import com.google.api.client.util.Value;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Configuration
@PropertySource("classpath:properties/google/spreadsheet/api/credentials.properties")
@ConfigurationProperties(prefix = "google.spreadsheet")
public class SpreadSheetConfig {
    
    @Value("${client_id}")
    private String client_id;

    @Value("${client_secret}")
    private String client_secret;

    @Value("${redirect_uri}")
    private String redirect_uri;

    @Value("${code}")
    private String code;
    
    @Value("${token}")
    private String token;

    @Value("${refresh_token}")
    private String refresh_token;
}
