package hanpoom.internal_cron.api.client;

import java.io.IOException;

import okhttp3.RequestBody;
import okhttp3.Response;

public interface Client {
    Response apiPost(String apiToken, String url, String json) throws IOException;

    Response apiGet(String apiToken, String url) throws IOException;

    Response post(String url, RequestBody body) throws IOException;
}
