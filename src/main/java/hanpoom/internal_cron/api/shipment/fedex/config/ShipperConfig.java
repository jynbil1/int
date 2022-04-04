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
public class ShipperConfig {
    private final static String JSON_FILE = "/assets/api/fedex/shipper-info.json";

    private JSONObject shipperInfo;

    public ShipperConfig() {
        try (InputStream in = getClass().getResourceAsStream(JSON_FILE);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            this.shipperInfo = new JSONObject(new JSONTokener(reader));
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject getShipperInfo() {
        return this.shipperInfo;
    }
}
