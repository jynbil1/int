package hanpoom.internal_cron.api.shipment.dhl.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;

@Service
public class DHLShipmentStatusCode {
    public DHLShipmentStatusCode() {

    }

    private final static String JSON_FILE = "/properties/dhl/shipment/status.json";

    public JSONObject getShipmentStatusJSON() throws IOException {
        try (InputStream in = getClass().getResourceAsStream(JSON_FILE);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            JSONTokener tokener = new JSONTokener(reader);
            return new JSONObject(tokener);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
