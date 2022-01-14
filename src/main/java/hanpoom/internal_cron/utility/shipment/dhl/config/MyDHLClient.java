package hanpoom.internal_cron.utility.shipment.dhl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@PropertySource("classpath:/api/api-keys.properties")
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties("dhl.mydhl.production")
public class MyDHLClient {
    private final String url;
    private final String username;
    private final String password;
}
