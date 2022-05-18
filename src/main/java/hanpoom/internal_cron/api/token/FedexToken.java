package hanpoom.internal_cron.api.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hanpoom.internal_cron.api.shipment.fedex.manager.FedexOAuthTokenManager;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class FedexToken extends Token {

    @Autowired
    private FedexOAuthTokenManager fedexOAuthTokenManager;

    @Builder
    public FedexToken(String url, String apiKey, String secretKey){
        super(url, apiKey, secretKey);
    }

    @Override
    public String getValidAccessToken() {
        if (mustRefreshToken()) {
            return fedexOAuthTokenManager.validateToken();
        }
        return this.accessToken;
    }
}
