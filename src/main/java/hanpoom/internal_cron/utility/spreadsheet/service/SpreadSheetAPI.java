package hanpoom.internal_cron.utility.spreadsheet.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.utility.spreadsheet.config.SpreadSheetConfig;
import io.netty.util.internal.SystemPropertyUtil;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SpreadSheetAPI {
    private SpreadSheetConfig config;

    private static final String url = "";

    public String getIntialURL() {
        String initialUrl = String.format(
                "https://accounts.google.com/o/oauth2/auth?access_type=offline&approval_prompt=auto&client_id=%s&response_type=code&scope=https://www.googleapis.com/auth/spreadsheets&redirect_uri=%s",
                config.getClient_id(), config.getRedirect_uri());
        return initialUrl;
    }

    public void validateToken(String code) {
        String validateUrl = "https://accounts.google.com/o/oauth2/token";
        String requestType = "POST";
        Map<String, String> requestBodyMap = new LinkedHashMap<>();

        requestBodyMap.put("grant_type", "authorization_code");
        requestBodyMap.put("code", code);
        requestBodyMap.put("client_id", config.getClient_id());
        requestBodyMap.put("client_secret", config.getClient_secret());
        requestBodyMap.put("redirect_url", config.getRedirect_uri());

        try {
            URL url = new URL(validateUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(requestType);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoOutput(true);

            JSONObject json = new JSONObject();
            for (Map.Entry<String, String> param : requestBodyMap.entrySet()) {
                json.put(param.getKey(), param.getValue());
            }
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            writer.write(json.toString());
            writer.flush();
            writer.close();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
                System.out.println(response.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshToken() {

    }
}
