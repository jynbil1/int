package hanpoom.internal_cron.api.client;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClient implements Client {
    final static MediaType JSON = MediaType.parse("application/json");
    final static String FORM = "application/x-www-form-urlencoded";

    @Override
    public Response apiPost(String apiToken, String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-locale", "en_US")
                .addHeader("Authorization", "Bearer " + apiToken)
                .build();

        return client.newCall(request).execute();
    }

    @Override
    public Response apiGet(String apiToken, String url) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response post(String url, RequestBody body) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", FORM)
                .build();

        return client.newCall(request).execute();
    }

}
