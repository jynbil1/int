package hanpoom.internal_cron.crons.dashboard.dhl.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.filefilter.TrueFileFilter;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackingResult;
import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackingVO;
import hanpoom.internal_cron.utility.spreadsheet.service.SpreadSheetAPI;

@Service
public class DHLReport {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private final static String SPREADSHEET_ID = "1G3Y2CWeYveB2KNVRduKTSgFZuOIh7Cb8JQZOO0gBDqw";
    private final static String SHEET = "배송상황";

    @Autowired
    private DHLService dhlService;
    @Autowired
    private SpreadSheetAPI spreadSheetApi;
    @Autowired
    private DHLShipmentHandler dhlHandler;

    // Excuter Methods Below:
    public void monitorShipments() {
        LocalDateTime now = LocalDateTime.now();
        String executeTime = now.format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
        System.out.println(executeTime + " 에 작업이 시작되었습니다.");

        DHLTrackingResult report = dhlHandler.filterShipments();

        if (!report.getDeliveredOrders().isEmpty()) {
            dhlService.insertDeliveredShipments(report.getDeliveredOrders());
        }

        List<List<DHLTrackingVO>> reportSets = Arrays.asList(
                report.getCustomIssueOrders(),
                report.getOtherIssueOrders(),
                report.getDelayedOrders(),
                report.getUntrackableOrders(),
                report.getReturnedOrders());

        for (List<DHLTrackingVO> trackingVo : reportSets) {
            if (!trackingVo.isEmpty()) {
                dhlService.processIssueShipments(trackingVo);
            }
        }

        String executeMessage = now.format(DateTimeFormatter.ofPattern(DATE_PATTERN + " HH"));

        int totalNoOfUpates = report.getDeliveredOrders().size() +
                report.getDelayedOrders().size() +
                report.getCustomIssueOrders().size() +
                report.getUntrackableOrders().size() +
                report.getOtherIssueOrders().size() +
                report.getReturnedOrders().size();

        String message = "";

        if (totalNoOfUpates > 0) {
            String messageText = "%s시 발송 모니터링 현황\n"
                    + "---------------------------------------------------\n"
                    + "배송 완료: %s 건\n" + "배송 지연: %s 건\n"
                    + "통관 문제: %s 건\n\n" + "조회 불가: %s 건\n"
                    + "이외 문제: %s 건\n" + "반송 완료: %s 건\n"
                    + "---------------------------------------------------\n"
                    + "배송중: %s 건\n"
                    + "<https://docs.google.com/spreadsheets/d/1G3Y2CWeYveB2KNVRduKTSgFZuOIh7Cb8JQZOO0gBDqw/edit#gid=448567097|문제 보러가기>";

            message = String.format(messageText,
                    executeMessage,
                    DHLTrackingResult.getMoneyFormat(report.getDeliveredOrders().size()),

                    DHLTrackingResult.getMoneyFormat(report.getDelayedOrders().size()),
                    DHLTrackingResult.getMoneyFormat(report.getCustomIssueOrders().size()),

                    DHLTrackingResult.getMoneyFormat(report.getUntrackableOrders().size()),
                    DHLTrackingResult.getMoneyFormat(report.getOtherIssueOrders().size()),
                    DHLTrackingResult.getMoneyFormat(report.getReturnedOrders().size()),

                    report.getTotalInTransit());
        } else {
            String messageText = "%s시 발송 모니터링 현황\n"
                    + "---------------------------------------------------\n"
                    + "금일 모니터링 결과에는 갱신된 운송장이 없습니다.\n"
                    + "---------------------------------------------------\n"
                    + "배송중: %s 건\n"
                    + "<https://docs.google.com/spreadsheets/d/1G3Y2CWeYveB2KNVRduKTSgFZuOIh7Cb8JQZOO0gBDqw/edit#gid=448567097|문제 보러가기>";

            message = String.format(messageText,
                    executeMessage,
                    report.getTotalInTransit());
        }

        boolean isSent = dhlService.sendSlackMessage(message);
        if (!isSent) {
            System.out.println("현황 결과를 출력하지 못했습니다.");
        } else {
            System.out.println("현황 데이터를 성공적으로 출력했습니다.");
        }

        LocalDateTime then = LocalDateTime.now();
        String endTime = then.format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
        long timeSpent = LocalDateTime.from(now).until(then, ChronoUnit.SECONDS);
        System.out.println(endTime + " 에 작업이 끝마쳤습니다.");
        System.out.println(String.format("소요시간: %s 분 %s 초",
                String.valueOf(timeSpent / 60),
                String.valueOf(timeSpent % 60)));
    }

    public int updateSheetRowToNewTrackingNo() {
        // 1. 엑셀 데이터 가져오기
        // 2. order no 로 wphpm_postmeta 데이터 찾기
        // 3. DB 데이터랑 엑셀 데이터 비교
        // 4. 다른거 갱신

        // 이미 처리된 건은 제외한 건들임.
        // C - 운송장
        // D - 새로운 운송장

        Map<String, Map<String, Object>> contents = dhlService.readIncompleteSheetOrders();
        // 주문번호로 운송장 번호를 조회함.
        List<DHLTrackingVO> trackingVos = dhlService.getTrackingLists(new ArrayList<String>(contents.keySet()));

        for (DHLTrackingVO vo : trackingVos) {
            // C 컬럼에 운송장 번호가 있음.
            String oldTrackingNo = (String) contents.get(vo.getOrder_no()).get("E");
            String newTrackingNo = vo.getTracking_no();

            if (oldTrackingNo.equals(newTrackingNo)) {
                contents.remove(vo.getOrder_no());
            } else {
                // 같지 않으면
                Map<String, Object> tmp = contents.get(vo.getOrder_no());
                tmp.put("F", vo.getTracking_no());
                contents.put(vo.getOrder_no(), tmp);
            }
        }

        if (contents.size() < 1) {
            return 0;
        } else {
            int noOfRowsUpdated = 0;
            for (String orderNo : new ArrayList<>(contents.keySet())) {
                Map<String, Object> selectedRow = contents.get(orderNo);
                String range = "F" + String.valueOf((Integer) selectedRow.get("row"));

                spreadSheetApi.setSheetName(SHEET);
                spreadSheetApi.setSpreadSheetID(SPREADSHEET_ID);
                spreadSheetApi.setCellAt(range);

                JSONArray array = new JSONArray();
                array.put((String) selectedRow.get("F"));
                if (spreadSheetApi.updateRows(new JSONArray().put(array)).getUpdatedRows() > 0) {
                    ++noOfRowsUpdated;
                }
                ;
            }
            return noOfRowsUpdated;
        }

    }

    public boolean checkNUpdateCompleteShipments() {
        // 1. 엑셀 데이터 가져오기
        // 2. 운송장 번호로 현황 조회하기
        // 3. 완료 되었으면, 완료 일자, 완료일자 - 발송일자 => 소요기간
        // 4. 3번 값 갱신하고 완료여부에 체크하기.
        // * 발송일자가 없는 것이 있을 수도 있으니 예외처리가 필요함.

        // 스프레드시트 데이터를 불러와서 완료여부가 없는 건들에 대해 운송장 검사를 한다.
        // order_no{rowNo: val, colAlph:datum... }

        // 업데이트는 한번에 칠 것.
        List<DHLTrackingVO> trackingVOs = new ArrayList<>();
        Map<String, Map<String, Object>> contents = dhlService.readIncompleteSheetOrders();
        System.out.println("------------------");
        System.out.println("checkNupdateCompleteShipments 중 readIncompleteSheetOrders 를 처리하는 과정");
        System.out.println(contents.toString());
        System.out.println("=---------=--------");
        String searchableTrackingNo = "";
        for (String orderNo : new ArrayList<>(contents.keySet())) {
            try {
                String newTrackingNo = (String) contents.get(orderNo).get("F");
                if (newTrackingNo.strip().length() > 9) {
                    // 새로운 운송장 자리에 값이 있으면
                    searchableTrackingNo = newTrackingNo;
                } else {
                    searchableTrackingNo = (String) contents.get(orderNo).get("E");
                }
                // 개별 운송장 조회 함수 & // 구분 작업
                DHLTrackingVO trackingVo = dhlHandler
                        .filterShipment(new DHLTrackingVO(orderNo, searchableTrackingNo));
                // 완료 안되었으면, false
                if (trackingVo.getTypeOfIssue().equals("delivered")) {
                    // 완료 되었으면, DB update and SpreadSheet Update.
                    // 1. 배송 완료일자 - I
                    // 2. 배송 기간 - J
                    // 3. 완료 여부 - A
                    LocalDateTime shippedDtime = LocalDateTime.parse(trackingVo.getShipped_dtime(),
                            DateTimeFormatter.ofPattern(DATETIME_PATTERN));
                    LocalDateTime deliveredDtime = LocalDateTime.parse(trackingVo.getEvent_dtime(),
                            DateTimeFormatter.ofPattern(DATETIME_PATTERN));

                    float shippingDuration = (float) LocalDateTime.from(shippedDtime).until(deliveredDtime,
                            ChronoUnit.HOURS);

                    if (trackingVo.getOrder_no().length() > 9) {
                        trackingVo.setOrder_no(orderNo);
                    }

                    trackingVOs.add(trackingVo);
                    String row = String.valueOf(contents.get(orderNo).get("row"));

                    spreadSheetApi.setSheetName(SHEET);
                    spreadSheetApi.setSpreadSheetID(SPREADSHEET_ID);

                    spreadSheetApi.updateRow(new JSONArray().put("True"), "A" + row);
                    spreadSheetApi.updateRow(new JSONArray().put(trackingVo.getEvent_dtime()), "I" + row);
                    spreadSheetApi.updateRow(
                            new JSONArray().put(String.format("%.2g%n", (float) shippingDuration / 24)),
                            "J" + row);

                } else {
                    continue;
                }
            } catch (NullPointerException npe) {
                continue;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        // 한번에 업데이트 칠 것.
        return dhlService.insertDeliveredShipments(trackingVOs) > 0 ? true : false;
    }

}
