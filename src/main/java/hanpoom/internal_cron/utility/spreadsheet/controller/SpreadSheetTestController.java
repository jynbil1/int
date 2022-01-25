package hanpoom.internal_cron.utility.spreadsheet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hanpoom.internal_cron.utility.spreadsheet.service.SpreadSheetAPI;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class SpreadSheetTestController {

    private SpreadSheetAPI api;

    @GetMapping("/spreadsheet-url")
    public String getURL() {
        return api.getIntialURL();
    }

    // @GetMapping("/validate-token")
    // public void validateToken() {
    //     api.validateToken();
    // }
    
    @GetMapping("/validate-code")
    public void validateCode(@RequestParam String code){
        api.validateToken(code);
    }
}
