package hanpoom.internal_cron.utility.http.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
public class SpreadSheetHttpService {

    private final static String ENCODING = "UTF-8";

    private String url;
    private StringBuilder body;
    private String contentType;
    private String token;
    private JSONObject jsonBody;

    public JSONObject get() {
        try {
            HttpURLConnection con = initiateConnection();
            con.setRequestMethod("GET");
            return new JSONObject(getResponse(con, "GET"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public JSONObject post() {
        try {
            HttpURLConnection con = initiateConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            return new JSONObject(getResponse(con, "POST"));
        } catch (JSONException je) {
            System.out.println("response 가 JSON 형식이 아니므로 확인해 볼 것.");
            je.printStackTrace();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public JSONObject put() {
        try {
            HttpURLConnection con = initiateConnection();
            con.setRequestMethod("PUT");
            con.setDoOutput(true);
            return new JSONObject(getResponse(con, "PUT"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    private HttpURLConnection initiateConnection() throws IOException {
        URL url = new URL(this.url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/" + this.contentType);
        con.setDoInput(true);

        if (this.token != null) {
            con.setRequestProperty("Authorization", "Bearer " + this.token);
        }

        return con;
    }

    private String getResponse(HttpURLConnection con, String requestMethod) {
        String readLine;
        StringBuilder builder = new StringBuilder();
        try {
            if (requestMethod.equals("POST") || requestMethod.equals("PUT")) {
                try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), ENCODING))) {
                    if (this.jsonBody != null) {
                        // System.out.println(this.jsonBody.toString());
                        bw.write(this.jsonBody.toString());

                    } else {
                        bw.write(this.body.toString());

                    }
                    bw.flush();
                }
            }
            if (con.getResponseCode() != 200) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(con.getErrorStream(), ENCODING))) {
                    while ((readLine = reader.readLine()) != null) {
                        builder.append(readLine);
                    }
                }
            } else {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), ENCODING))) {
                    while ((readLine = reader.readLine()) != null) {
                        builder.append(readLine);
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return builder.toString();
    }
}
