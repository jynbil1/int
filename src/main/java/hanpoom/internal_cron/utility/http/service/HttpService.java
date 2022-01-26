package hanpoom.internal_cron.utility.http.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
@Service
public class HttpService {
    private final static String ENCODING = "UTF-8";

    private String url;
    private StringBuilder body;
    private String contentType;

    public JSONObject get() {
        try {
            HttpURLConnection con = initiateConncetion();
            con.setRequestMethod("GET");
            return new JSONObject(getResponse(con, "GET"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public JSONObject post() {
        try {
            HttpURLConnection con = initiateConncetion();
            con.setRequestMethod("POST");
            return new JSONObject(getResponse(con, "POST"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    private HttpURLConnection initiateConncetion() throws IOException {
        URL url = new URL(this.url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/" + this.contentType);
        con.setDoInput(true);
        con.setDoOutput(true);
        return con;
    }

    private String getResponse(HttpURLConnection con, String requestMethod) {
        String readLine;
        StringBuilder builder = new StringBuilder();

        try {
            if (requestMethod.equals("POST")) {
                try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()))) {
                    bw.write(this.body.toString());
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
