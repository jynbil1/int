package hanpoom.internal_cron.api.token;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class Token {
    protected String url;
    protected String apiKey;
    protected String secretKey;

    protected String accessToken;
    protected String tokenType;
    protected int expiredIn;
    protected LocalDateTime expiredDateTime;
    protected String scope;

    protected String refreshToken;
    
    public Token(String url, String apiKey, String secretKey){
        this.url = url;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }
    
    public void setExpiredIn(int expiredIn) {
        this.expiredIn = expiredIn;
        this.expiredDateTime = LocalDateTime.now().plusSeconds(expiredIn);
    };

    public boolean mustRefreshToken() {
        if (this.expiredDateTime == null) {
            return true;
        } else if (ChronoUnit.SECONDS.between(LocalDateTime.now(), expiredDateTime) <= 10) {
            return true;
        } else {
            return false;
        }
    }

    public abstract String getValidAccessToken();
}
