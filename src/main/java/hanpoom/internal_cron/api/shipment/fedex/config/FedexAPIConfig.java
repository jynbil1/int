package hanpoom.internal_cron.api.shipment.fedex.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.context.annotation.Configuration;

import hanpoom.internal_cron.api.token.FedexToken;
import lombok.Data;

@Configuration
@Data
public class FedexAPIConfig {

    private final static String JSON_FILE = "/assets/api/fedex/fedex-api-credentials.json";
    private final boolean IS_PRODUCTION = true;
    // private final boolean IS_PRODUCTION = false;

    private FedexToken token;

    public FedexAPIConfig() {
        try (InputStream in = getClass().getResourceAsStream(JSON_FILE);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            JSONObject json = new JSONObject();

            // this.apiCredentials = new JSONObject(new JSONTokener(reader));

            if (IS_PRODUCTION) {
                json = new JSONObject(new JSONTokener(reader)).getJSONObject("production");
            } else {
                json = new JSONObject(new JSONTokener(reader)).getJSONObject("test");
            }

            // FedexToken token = new Gson().fromJson(json.toString(), FedexToken.class);
            // this.token = token;
            this.token = FedexToken.builder()
                .url(json.optString("url"))
                .apiKey(json.optString("api_key"))
                .secretKey(json.optString("secret_key"))            
            .build();

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
