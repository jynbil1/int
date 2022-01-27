package hanpoom.internal_cron.utility.spreadsheet.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.utility.http.service.SpreadSheetHttpService;
import hanpoom.internal_cron.utility.spreadsheet.config.SpreadSheetConfig;
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

    @Override
    public Integer insertRow(List<Object> row) {
        String url = "https://sheets.googleapis.com/v4/spreadsheets/%s/values/%s:append";
        try {
            url = String.format(url, this.spreadSheetID, URLEncoder.encode(this.sheetName, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        SpreadSheetHttpService httpService = new SpreadSheetHttpService();

        httpService.setContentType("json");
        httpService.setUrl(url);
        tokenValidator();
        httpService.setToken(this.token);

        return null;
    }

    @Override
    public Integer insertRows(List<List<Object>> rows) {
        SpreadSheetHttpService httpService = new SpreadSheetHttpService();

        tokenValidator();
        httpService.setToken(this.token);
        return null;
    }

    @Override
    public Integer updateRows() {
        SpreadSheetHttpService httpService = new SpreadSheetHttpService();

        tokenValidator();
        httpService.setToken(this.token);
        return null;
    }

    @Override
    public Integer deleteRows() {
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
