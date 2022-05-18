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

@Configuration
public class FedexShipStatusCode {
    private final static String JSON_FILE = "/assets/api/fedex/shipment/code/status_code.json";

    private JSONObject statusCodes;

    public JSONObject getStatusCodes() {
        return this.statusCodes;
    }

    public FedexShipStatusCode() {
        try (InputStream in = getClass().getResourceAsStream(JSON_FILE);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            this.statusCodes = new JSONObject(new JSONTokener(reader));
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
