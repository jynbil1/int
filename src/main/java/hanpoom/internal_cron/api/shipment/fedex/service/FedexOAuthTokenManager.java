package hanpoom.internal_cron.api.shipment.fedex.service;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hanpoom.internal_cron.api.client.HttpClient;
import hanpoom.internal_cron.api.shipment.fedex.config.FedexAPIConfig;
import hanpoom.internal_cron.api.token.FedexToken;
import hanpoom.internal_cron.api.token.OAuthToken;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

// @NoArgsConstructor
@Component
public class FedexOAuthTokenManager extends OAuthToken {
    private static final String VALIDATE_TOKEN_URL = "/oauth/token";
    private static final String GRANT_TYPE_CLIENT = "client_credentials";
    private static final String GRANT_TYPE_CSP = "csp_credentials";

    @Autowired
    private FedexAPIConfig fedexApiConfig;

    @Override
    @PostConstruct
    public String validateToken() {
        FedexToken token = fedexApiConfig.getToken();
        Response response = null;
        try {
            RequestBody body = new FormBody.Builder()
                    .add("grant_type", GRANT_TYPE_CLIENT)
                    .add("client_id", token.getApiKey())
                    .add("client_secret", token.getSecretKey())
                    .build();

            response = new HttpClient().post(token.getUrl() + VALIDATE_TOKEN_URL, body);
            // System.out.println(response.body().string());
            if (response.code() == 200) {
                JSONObject json = new JSONObject(response.body().string());

                token.setAccessToken(json.optString("access_token"));
                token.setTokenType(json.optString("token_type"));
                token.setExpiredIn(json.optInt("expires_in"));
                token.setExpiredDateTime(LocalDateTime.now().plusSeconds(json.optInt("expires_in")));
                token.setScope(json.optString("scope"));
                fedexApiConfig.setToken(token);

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return token.getAccessToken();
    }
}
