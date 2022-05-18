package hanpoom.internal_cron.api.token;

public abstract class OAuthToken {
    public abstract String validateToken();

    public String refreshToken() {
        return null;
    }

    public void revokeToken() {
    }
}
