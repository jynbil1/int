package hanpoom.internal_cron.utility.spreadsheet.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.utility.http.service.SpreadSheetHttpService;
import hanpoom.internal_cron.utility.spreadsheet.config.SpreadSheetConfig;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SpreadSheetAPIValidation {
    private SpreadSheetConfig config;

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

        SpreadSheetHttpService httpService = new SpreadSheetHttpService();
        httpService.setBody(sb);
        httpService.setContentType(CONTENT_TYPE);
        httpService.setUrl(validateUrl);

        return httpService.post();
    }

    public JSONObject refreshToken() {
        String refreshTokenURL = "https://oauth2.googleapis.com/token";

        StringBuilder sb = new StringBuilder();
        sb.append("grant_type=refresh_token");
        sb.append("&client_id=" + config.getClient_id());
        sb.append("&client_secret=" + config.getClient_secret());
        sb.append("&refresh_token=" + config.getRefresh_token());

        SpreadSheetHttpService httpService = new SpreadSheetHttpService();
        httpService.setBody(sb);
        httpService.setContentType(CONTENT_TYPE);
        httpService.setUrl(refreshTokenURL);

        return httpService.post();
    }
}
