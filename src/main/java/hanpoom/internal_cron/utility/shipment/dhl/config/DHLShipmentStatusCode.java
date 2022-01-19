package hanpoom.internal_cron.utility.shipment.dhl.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
public class DHLShipmentStatusCode {
    public DHLShipmentStatusCode() {

    }
    private final static String JSON_FILE = "classpath:properties/dhl/shipment/status.json";

    public JSONObject getShipmentStatusJSON() throws IOException {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        try {
            fis = new FileInputStream(ResourceUtils.getFile(JSON_FILE));
            isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            JSONTokener tokener = new JSONTokener(isr);
            return new JSONObject(tokener);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fis.close();
        }
        return null;
    }
}
