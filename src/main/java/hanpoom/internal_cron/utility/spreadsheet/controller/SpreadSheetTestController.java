package hanpoom.internal_cron.utility.spreadsheet.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import hanpoom.internal_cron.utility.spreadsheet.service.SpreadSheetAPI;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class SpreadSheetTestController {

    private SpreadSheetAPI api;

    private static final String common = "/internal_cron/dashboard/spreadsheet/pages/";

    @GetMapping("/")
    public String initiateAPIValidation(Map<String, String> map) {
        String url = api.getIntialURL();
        return "redirect:" + url;
    }

    @ResponseBody
    @GetMapping("/validateGoogleAPI")
    public void validateCode(@RequestParam(required = false) String code) {
        JSONObject simpleJson = api.validateToken(code);
        System.out.println(simpleJson.toString());
    }

    @ResponseBody
    @GetMapping("/refreshToken")
    public void validateCode() {
        api.refreshToken();
    }
}
