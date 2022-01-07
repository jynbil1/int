package hanpoom.internal_cron.crons.dashboard.spreadsheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hanpoom.internal_cron.crons.dashboard.common.vo.APIResponse;
import hanpoom.internal_cron.crons.dashboard.common.vo.SuccessResponse;
import hanpoom.internal_cron.crons.dashboard.spreadsheet.service.SpreadSheetCRUDService;
import hanpoom.internal_cron.utility.calendar.service.CalendarService;

@RestController
public class TestController {
    private SpreadSheetCRUDService spreadSheet;
    private CalendarService calendar;

    public TestController(SpreadSheetCRUDService spreadSheet, CalendarService calendar) {
        this.spreadSheet = spreadSheet;
        this.calendar = calendar;
    }

    @GetMapping(value = "/test")
    public APIResponse getTest() {

        // C 컬럼은 주 차를 뜻한다. 주 차가 제일 높은 값을 가져온다.
        // 가져온 값 중 숫자가 없으면 1 부터 시작한다.
        int maxInt = 0;
        List<List<Object>> currentCContent = spreadSheet.getContents("C:C");
        for (List<Object> data : currentCContent) {
            for (Object datum : data) {
                String strValue = String.valueOf(datum);
                if (strValue.length() > 0) {
                    try {
                        int intVal = Integer.parseInt(strValue);
                        if (maxInt < intVal) {
                            maxInt = intVal;
                        } else {
                            continue;
                        }
                    } catch (NumberFormatException nfe) {
                        continue;
                    }
                }
            }
        }
        // 가져온 값 더하기 1을 한다.
        maxInt += 1;

        // 가져온 데이터를 엑셀 시트에 기입한다.
        List<Object> dataSet = Arrays
                .asList(calendar.getPreviousWeekMonday(false),
                        calendar.getPreviousWeekSunday(false),
                        String.valueOf(maxInt),
                        "1", "2", "3", "4", "5", "6");
        boolean isInserted = spreadSheet.insertRow(dataSet);
        return new SuccessResponse();
    }
}
