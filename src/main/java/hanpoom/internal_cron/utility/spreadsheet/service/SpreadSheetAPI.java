package hanpoom.internal_cron.utility.spreadsheet.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.utility.http.service.SpreadSheetHttpService;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Service
public class SpreadSheetAPI extends SpreadSheetAPITools {

    private SpreadSheetAPIValidation api;
    // private SpreadSheetConfig config;

    private static final String TOKEN = "access_token";

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
        httpService.setToken(api.refreshToken().getString(TOKEN));

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
    public Integer insertRow() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer insertRows() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer updateRows() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer deleteRows() {
        // TODO Auto-generated method stub
        return null;
    }

}
