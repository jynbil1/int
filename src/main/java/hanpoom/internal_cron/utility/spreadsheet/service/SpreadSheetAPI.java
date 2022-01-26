package hanpoom.internal_cron.utility.spreadsheet.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.utility.http.service.HttpService;
import hanpoom.internal_cron.utility.spreadsheet.config.SpreadSheetConfig;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SpreadSheetAPI {
    private SpreadSheetConfig config;
    private HttpService httpService;

    private static final String CONTENT_TYPE = "x-www-form-urlencoded";

    public String getIntialURL() {
        String initialUrl = "https://accounts.google.com/o/oauth2/v2/auth?"
                + "client_id=" + config.getClient_id()
                + "&redirect_uri=" + config.getRedirect_uri()
                + "&response_type=code"
                + "&scope=https://www.googleapis.com/auth/spreadsheets"
                + "&access_type=offline";

        return initialUrl;
    }

    public JSONObject validateToken(String code) {
        String validateUrl = "https://oauth2.googleapis.com/token";

        StringBuilder sb = new StringBuilder();
        sb.append("grant_type=authorization_code");
        sb.append("&client_id=" + config.getClient_id());
        sb.append("&client_secret=" + config.getClient_secret());
        sb.append("&redirect_uri=" + config.getRedirect_uri());
        sb.append("&code=" + code);

        httpService.setBody(sb);
        httpService.setContentType(CONTENT_TYPE);
        httpService.setUrl(validateUrl);

        return httpService.post();
    }

    public String refreshToken() {
        String refreshTokenURL = "https://oauth2.googleapis.com/token";

        StringBuilder sb = new StringBuilder();
        sb.append("grant_type=refresh_token");
        sb.append("&client_id=" + config.getClient_id());
        sb.append("&client_secret=" + config.getClient_secret());
        sb.append("&refresh_token=" + config.getRefresh_token());

        try {
            URL url = new URL(refreshTokenURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Host", "oauth2.googleapis.com");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoInput(true);
            con.setDoOutput(true);

            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()))) {
                bw.write(sb.toString());
                bw.flush();
            }

            String readLine;
            StringBuilder builder = new StringBuilder();
            if (con.getResponseCode() != 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getErrorStream(), "utf-8"))) {
                    while ((readLine = reader.readLine()) != null) {
                        builder.append(readLine);
                    }
                }
            } else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    while ((readLine = reader.readLine()) != null) {
                        builder.append(readLine);
                    }
                }
            }
            System.out.println(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
