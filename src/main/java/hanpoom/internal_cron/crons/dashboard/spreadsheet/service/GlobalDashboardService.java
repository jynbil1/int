package hanpoom.internal_cron.crons.dashboard.spreadsheet.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.crons.dashboard.common.mapper.CommonMapper;
import hanpoom.internal_cron.crons.dashboard.common.vo.DateRangeVO;
import hanpoom.internal_cron.utility.calendar.CalendarManager;
import hanpoom.internal_cron.utility.slack.service.SlackService;
import hanpoom.internal_cron.utility.spreadsheet.service.SpreadSheetAPI;
import hanpoom.internal_cron.utility.spreadsheet.vo.UpdateSheetVO;

@Service
public class GlobalDashboardService {
    private final static String SPREADSHEET_ID = "114n3w9q8ytp0z5zFoiOo1xg_cP2nt3yspYKQJvT1KuU";
    private final static String SHEET = "22 GLOBAL_DASHBOARD";

    private CommonMapper commonMapper;
    private CalendarManager calendar;
    private SpreadSheetAPI sheetApi;

    private GlobalDashboardService(CommonMapper commonMapper,
            CalendarManager calendar,
            SpreadSheetAPI sheetApi) {
        this.commonMapper = commonMapper;
        this.calendar = calendar;
        this.sheetApi = sheetApi;
    }

    private String getLastWeekRevenue() {
        // System.out.println(calendar.getPreviousWeekMonday(true));
        // System.out.println(calendar.getPreviousWeekSunday(true));
        String lastWeekRevenue;
        try {
            lastWeekRevenue = commonMapper
                    .getRevenue(new DateRangeVO(calendar.getPreviousWeekMonday(true),
                            calendar.getPreviousWeekSunday(true)));
        } catch (Exception e) {
            lastWeekRevenue = "N/A";
            e.printStackTrace();
        }
        return lastWeekRevenue;
    }

    private String getLastWeekNewUsers() {
        String newUsers;
        try {
            // get the corresponding startdate and enddate
            newUsers = commonMapper.getNewUsers(
                    new DateRangeVO(calendar.getPreviousWeekMonday(true), calendar.getPreviousWeekSunday(true)));
        } catch (Exception e) {
            newUsers = "N/A";
            e.printStackTrace();
        }
        return newUsers;
    }

    private String getLastWeekNewPurchase() {
        String newPurchase;
        try {
            // get the corresponding startdate and enddate
            newPurchase = commonMapper.getNewPurchasers(new DateRangeVO(calendar.getPreviousWeekMonday(true),
                    calendar.getPreviousWeekSunday(true)));
        } catch (Exception e) {
            newPurchase = "N/A";
            e.printStackTrace();
        }
        return newPurchase;
    }

    private String getLastWeekRePurchase() {
        String rePurchase;
        try {
            // get the corresponding startdate and enddate
            rePurchase = commonMapper.getNewRepurchasers(new DateRangeVO(calendar.getPreviousWeekMonday(true),
                    calendar.getPreviousWeekSunday(true)));
        } catch (Exception e) {
            rePurchase = "N/A";
            e.printStackTrace();
        }
        return rePurchase;
    }

    private String getLastWeekTotalOrders() {
        String totalOrders;
        try {
            totalOrders = commonMapper.getTotalOrders(new DateRangeVO(calendar.getPreviousWeekMonday(true),
                    calendar.getPreviousWeekSunday(true)));
        } catch (Exception e) {
            totalOrders = "N/A";
            e.printStackTrace();
        }
        return totalOrders;
    }

    private String getLastWeekMargins() {
        String totalMargins;
        try {
            totalMargins = commonMapper.getTotalMargins(new DateRangeVO(calendar.getPreviousWeekMonday(true),
                    calendar.getPreviousWeekSunday(true)));
        } catch (Exception e) {
            totalMargins = "N/A";
            e.printStackTrace();
        }
        return totalMargins;
    }

    // ?????? ????????? ??????.
    public void reportWeeklyGobalDashboard(){
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern(CalendarManager.DATE_TIME_FORMAT_PATTERN));
        System.out.println(now + " ??? ????????? ?????????");

        // ???????????? ????????????.
        // 1. ??????
        String revenue = getLastWeekRevenue();
        // 2. ?????? ???
        // 2.1. ?????? ?????????
        String newCustomers = getLastWeekNewUsers();
        // 2.2. ?????? ????????? ??? ????????? ?????? ?????????
        String newPurchasers = getLastWeekNewPurchase();
        // 2.3. ?????? ????????? ??? ?????? ??? ?????????
        String newRepurchasers = getLastWeekRePurchase();

        // 3. ????????? ???
        String totalOrders = getLastWeekTotalOrders();

        // 4. ?????? ??????
        String totalMargins = getLastWeekMargins();

        sheetApi.setSheetName(SHEET);
        sheetApi.setSpreadSheetID(SPREADSHEET_ID);

        // C ????????? ??? ?????? ?????????. ??? ?????? ?????? ?????? ?????? ????????????.
        // ????????? ??? ??? ????????? ????????? 1 ?????? ????????????.
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
            // ????????? ??? ????????? 1??? ??????.
            maxInt += 1;

            // ????????? ???????????? ?????? ????????? ????????????.
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
                System.out.println("??????????????? ???????????? ??????????????????.");
            } else {
                System.out.println("??????");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // ?????? ????????? ?????????????????? ???????????? ????????? ?????? ????????????.
            try {
                new SlackService(true).sendNotification(e.getMessage());

            } catch (Exception se) {
                System.out.println(se.getMessage());
            }
        }
    }
}
