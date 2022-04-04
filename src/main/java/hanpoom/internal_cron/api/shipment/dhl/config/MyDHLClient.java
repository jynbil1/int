package hanpoom.internal_cron.api.shipment.dhl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Configuration
// @EnableConfigurationProperties
// @PropertySource("classpath:api/dhl-keys.properties")
@PropertySource("classpath:properties/dhl/api/keys.properties")

@ConfigurationProperties(prefix = "mydhl")
// @ConstructorBinding
// @RequiredArgsConstructor
public class MyDHLClient {
    @Value("${mydhl.url}")
    private String url;

    @Value("${mydhl.username}")
    private String username;

    @Value("${mydhl.password}")
    private String password;

}
