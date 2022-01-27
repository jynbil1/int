package hanpoom.internal_cron.utility.spreadsheet.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.utility.http.service.SpreadSheetHttpService;
import hanpoom.internal_cron.utility.spreadsheet.config.SpreadSheetConfig;
import hanpoom.internal_cron.utility.spreadsheet.vo.UpdateSheetVO;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Service
public class SpreadSheetAPI extends SpreadSheetAPITools {

    private SpreadSheetAPIValidation api;
    private SpreadSheetConfig config;

    @Override
    public List<List<Object>> readSheetData(String range) {
        String url = "https://sheets.googleapis.com/v4/spreadsheets/" + this.spreadSheetID + "/values/";

        try {
            url += URLEncoder.encode(range, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        SpreadSheetHttpService httpService = new SpreadSheetHttpService();

        httpService.setContentType("json");
        httpService.setUrl(url);
        tokenValidator();
        httpService.setToken(this.token);

        JSONArray jsonArray = httpService.get().optJSONArray("values");

        List<List<Object>> data = new ArrayList<>();
        for (int i = 0; i < jsonArray.length() - 0; ++i) {
            List<Object> row = new ArrayList<>();
            for (int j = 0; j < jsonArray.optJSONArray(i).length() - 0; ++j) {
                row.add(jsonArray.optJSONArray(i).getString(j));
            }
            data.add(row);
        }
        return data;

    }

    // 몇개 넣든 무조건 2차원 JSONArray.
    @Override
    public UpdateSheetVO insertRows(JSONArray row) {
        String url = "https://sheets.googleapis.com/v4/spreadsheets/%s/values/%s:append";
        try {
            url = String.format(url, this.spreadSheetID, URLEncoder.encode(this.sheetName, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("valueInputOption=" + "USER_ENTERED");
        sb.append("&insertDataOption=" + "INSERT_ROWS");
        sb.append("&includeValuesInResponse=" + false);
        sb.append("&responseDateTimeRenderOption=" + "FORMATTED_STRING");

        url = url + "?" + sb.toString();

        SpreadSheetHttpService httpService = new SpreadSheetHttpService();

        httpService.setContentType("json");
        httpService.setUrl(url);
        httpService.setJsonBody(new JSONObject().put("values", row));
        tokenValidator();
        httpService.setToken(this.token);
        JSONObject response = httpService.post().optJSONObject("updates");

        return new UpdateSheetVO(response.optString("spreadsheetId"),
                response.optString("updatedRange"),
                response.optInt("updatedRows"),
                response.optInt("updatedColumns"),
                response.optInt("updatedCells"));
    }

    @Override
    public UpdateSheetVO updateRows() {
        SpreadSheetHttpService httpService = new SpreadSheetHttpService();

        tokenValidator();
        httpService.setToken(this.token);
        return null;
    }

    @Override
    public UpdateSheetVO deleteRows() {
        SpreadSheetHttpService httpService = new SpreadSheetHttpService();

        tokenValidator();
        httpService.setToken(this.token);
        return null;
    }

    @Override
    protected String tokenValidator() {
        if (this.token == null) {
            this.token = api.refreshToken();
            return this.token;
        } else {
            this.token = api.validateTokenOpt(this.token);
            return this.token;
        }
    }

}
