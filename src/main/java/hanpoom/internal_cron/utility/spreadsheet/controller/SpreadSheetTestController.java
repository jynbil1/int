package hanpoom.internal_cron.utility.spreadsheet.controller;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import hanpoom.internal_cron.utility.spreadsheet.service.SpreadSheetAPI;
import hanpoom.internal_cron.utility.spreadsheet.service.SpreadSheetAPIValidation;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class SpreadSheetTestController {

    private SpreadSheetAPIValidation apiValdation;
    private SpreadSheetAPI api;

    private static final String SHEET_NAME = "배송상황";
    private static final int SHEET_ID = 448567097;
    private static final String SPREADSHEET_ID = "1G3Y2CWeYveB2KNVRduKTSgFZuOIh7Cb8JQZOO0gBDqw";

    private static final String common = "/internal_cron/dashboard/spreadsheet/pages/";

    @GetMapping("/")
    public String initiateAPIValidation(Map<String, String> map) {
        String url = apiValdation.getIntialURL();
        return "redirect:" + url;
    }

    @ResponseBody
    @GetMapping("/validateGoogleAPI")
    public void validateCode(@RequestParam(required = false) String code) {
        JSONObject simpleJson = apiValdation.validateToken(code);
        System.out.println(simpleJson.toString());
    }

    @ResponseBody
    @GetMapping("/refreshToken")
    public void validateCode() {
        System.out.println(apiValdation.refreshToken().toString());
    }

    @ResponseBody
    @GetMapping("/readSheet")
    public void readSheet() {
        api.setSheetID(SHEET_ID);
        api.setSpreadSheetID(SPREADSHEET_ID);

        String range = SHEET_NAME + "!" + "A:L";
        List<List<Object>> object = api.readSheetData(range);
        // System.out.println(object.toString());
    }

    @ResponseBody
    @GetMapping("/insertData")
    public void insertData() {
        api.setSheetID(SHEET_ID);
        api.setSpreadSheetID(SPREADSHEET_ID);

        String range = SHEET_NAME + "!" + "A:L";
        List<List<Object>> object = api.readSheetData(range);
        System.out.println(object.toString());
    }

    @ResponseBody
    @GetMapping("/updateData")
    public void updateData() {
        api.setSheetID(SHEET_ID);
        api.setSpreadSheetID(SPREADSHEET_ID);

        String range = SHEET_NAME + "!" + "A:L";
        List<List<Object>> object = api.readSheetData(range);
        System.out.println(object.toString());
    }

}
