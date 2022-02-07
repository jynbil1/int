package hanpoom.internal_cron.crons.dashboard.spreadsheet.cron;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hanpoom.internal_cron.crons.dashboard.spreadsheet.service.GlobalDashboardService;
import hanpoom.internal_cron.utility.calendar.service.CalendarService;
import hanpoom.internal_cron.utility.slack.service.SlackService;
import hanpoom.internal_cron.utility.spreadsheet.service.SpreadSheetAPI;
import hanpoom.internal_cron.utility.spreadsheet.vo.UpdateSheetVO;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class GlobalDashboardCron {

    private GlobalDashboardService dashboard;
    private CalendarService calendar;
    private SpreadSheetAPI sheetApi;

    private final static String SPREADSHEET_ID = "114n3w9q8ytp0z5zFoiOo1xg_cP2nt3yspYKQJvT1KuU";
    private final static String SHEET = "22 GLOBAL_DASHBOARD";

    // "0 0 * * * *" = the top of every hour of every day.
    // "* * * * * *" = 매초 실행 합니다.
    // "*/10 * * * * *" = 매 10초마다 실행한다.
    // 0 */1 * * * = 매시간 실행 합니다.
    // "0 0 8-10 * * *" = 매일 8, 9, 10시에 실행한다
    // "0 0 6,19 * * *" = 매일 오전 6시, 오후 7시에 실행한다.
    // "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30, 10:00 and 10:30 every day.
    // "0 0 9-17 * * MON-FRI" = 오전 9시부터 오후 5시까지 주중(월~금)에 실행한다.
    // "0 0 0 25 12 ?" = every Christmas Day at midnight

    // 매주 월요일 자정이 되면 수행.
    // @Scheduled(cron = "1 * * * * *", zone = "Asia/Seoul")
    @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Seoul")
    public void cronJobGloabalWeeklyDashboard() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern(CalendarService.DATE_TIME_FORMAT_PATTERN));
        System.out.println(now + " 에 작업을 수행함");

        // 데이터를 가져온다.
        // 1. 매출
        String revenue = dashboard.getLastWeekRevenue();
        // 2. 유저 수
        // 2.1. 신규 가입자
        String newCustomers = dashboard.getLastWeekNewUsers();
        // 2.2. 신규 가입자 중 기간내 신규 구매자
        String newPurchasers = dashboard.getLastWeekNewPurchase();
        // 2.3. 신규 가입자 중 기간 내 재구매
        String newRepurchasers = dashboard.getLastWeekRePurchase();

        // 3. 주문건 수
        String totalOrders = dashboard.getLastWeekTotalOrders();

        // 4. 상품 마진
        String totalMargins = dashboard.getLastWeekMargins();

        sheetApi.setSheetName(SHEET);
        sheetApi.setSpreadSheetID(SPREADSHEET_ID);

        // C 컬럼은 주 차를 뜻한다. 주 차가 제일 높은 값을 가져온다.
        // 가져온 값 중 숫자가 없으면 1 부터 시작한다.
        try {
            int maxInt = 0;
            List<List<Object>> currentCContent = sheetApi.readSheetData("C:C");
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
                            String.valueOf(maxInt), revenue,
                            newCustomers, newPurchasers, newRepurchasers,
                            totalOrders, totalMargins);

            JSONArray array = new JSONArray();
            for (Object obj : dataSet) {
                array.put(obj);
            }
            UpdateSheetVO sheetVo = sheetApi.insertRows(new JSONArray().put(array));
            System.out.println(sheetVo.toString());
            if (sheetVo.getUpdatedCells() > 0) {
                System.out.println("성공적으로 데이터를 입력했습니다.");
            } else {
                System.out.println("실패");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // 어떤 문제가 발생했는지의 메세지를 슬렉을 통해 알려준다.
            try {
                new SlackService(true).sendNotification(e.getMessage());

            } catch (Exception se) {
                System.out.println(se.getMessage());
            }
        }

    }
}
