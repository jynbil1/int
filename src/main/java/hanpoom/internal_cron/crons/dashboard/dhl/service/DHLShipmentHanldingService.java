package hanpoom.internal_cron.crons.dashboard.dhl.service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.crons.dashboard.dhl.mapper.DHLMapper;
import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackingVO;
import hanpoom.internal_cron.crons.dashboard.spreadsheet.service.SpreadSheetCRUDService;
import lombok.Getter;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;

@Service
public class DHLShipmentHanldingService {
    private static final String SHIPMENT_REPORT_HOOK_URL = "https://hooks.slack.com/services/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";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private final static String SPREADSHEET_ID = "1G3Y2CWeYveB2KNVRduKTSgFZuOIh7Cb8JQZOO0gBDqw";
    private final static String SHEET = "배송상황";
    private final static int SHEET_ID = 448567097;
    // public final String STARTING_COLUMN = "D";
    // public final int STARTIIG_ROW = 5;

    private DHLMapper dhlMapper;
    private SpreadSheetCRUDService spreadSheet;
    private DHLService dHLService;

    public DHLShipmentHanldingService(DHLMapper dhlMapper,
            SpreadSheetCRUDService spreadSheet,
            DHLService dHLService) {
        this.dhlMapper = dhlMapper;
        this.spreadSheet = spreadSheet;
        this.dHLService = dHLService;
    }

    @Getter
    private int deliverdOrders;
    @Getter
    private int delayedOrders;
    @Getter
    private int customsIssueOrders;
    @Getter
    private int otherIssueOrders;

    // 배송 완료 처리 건.
    public Integer processDeliveredOrders(List<DHLTrackingVO> trackingVoList) {
        // 값을 넣고, 성공하면 넣은 값을 슬랙 알림을 위해 입력한 수 만큼을 리턴
        return insertDeliveredShipments(trackingVoList);

    }

    // 통관 문제 건.
    public Integer processCustomsIssueOrders(List<DHLTrackingVO> trackingVoList) {
        int dbInserted = insertErrorShipments(trackingVoList);
        boolean spreadSheetInserted = insertIntoSpreadSheet(trackingVoList);
        System.out.println(trackingVoList.size());
        System.out.println(dbInserted);
        if (dbInserted == trackingVoList.size() && spreadSheetInserted) {
            return 1;
        }
        return 0;
    }

    // 기타 문제 건.
    public Integer processOtherIssueOrders(List<DHLTrackingVO> trackingVoList) {
        int dbInserted = insertErrorShipments(trackingVoList);
        boolean spreadSheetInserted = insertIntoSpreadSheet(trackingVoList);
        System.out.println(trackingVoList.size());
        System.out.println(dbInserted);
        if (dbInserted == trackingVoList.size() && spreadSheetInserted) {
            return 1;
        }
        return 0;
    }

    // 지연되고 있는 건.
    public Integer processDelayedOrders(List<DHLTrackingVO> trackingVoList) {
        int dbInserted = insertErrorShipments(trackingVoList);
        boolean spreadSheetInserted = insertIntoSpreadSheet(trackingVoList);
        System.out.println(trackingVoList.size());
        System.out.println(dbInserted);
        if (dbInserted == trackingVoList.size() && spreadSheetInserted) {
            return 1;
        }
        return 0;
    }

    // 기간이 지나 검색결과가 조회되지 않는 건.
    public Integer processUntrackableOrders(List<DHLTrackingVO> trackingVoList) {
        // 조회되지 않는 건들은 insert 하고 슬랙으로 집계알림과 다르게 따로 안내 나갈것.
        int dbInserted = insertErrorShipments(trackingVoList);
        boolean spreadSheetInserted = insertIntoSpreadSheet(trackingVoList);
        System.out.println(trackingVoList.size());
        System.out.println(dbInserted);
        if (dbInserted == trackingVoList.size() && spreadSheetInserted) {
            return 1;
        }
        return 0;
    }

    // 통관 문제건 다시 검사 및 스프레드시트 값 변경
    public Integer recheckCustomsIssueOrders() {
        // 1. 운송장 재 조회
        // 2. 스프레드시트 갱신
        // 3. 배송 완료 테이블에 값 추가
        return 0;
    }

    // 기타 문제건 다시 검사 및 스프레드시트 값 변경
    public Integer recheckOtherIssueOrders() {
        // 1. 운송장 재 조회
        // 2. 스프레드시트 갱신
        // 3. 배송 완료 테이블에 값 추가
        return 0;
    }

    // 슬랙 메시지 보내는 부분.
    public boolean sendSlackMessage(String messageText) {
        SlackApi api = new SlackApi(SHIPMENT_REPORT_HOOK_URL);
        SlackMessage slackMessage = new SlackMessage();

        try {
            slackMessage.setText(messageText);
            api.call(slackMessage);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    // 스프레드 시트에 CRUD하는 부분.
    public boolean insertIntoSpreadSheet(List<DHLTrackingVO> orderShipments) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        List<List<Object>> dataSets = new ArrayList<>();
        // 생성일자 주문번호 송장번호 변경된송장
        // 주문일자 출고일자 배송완료일자 배송기간(n.m일)
        // 배송상태구분 배송특이사항 DHL문의여부 DHL확인내용
        // 완료여부 비고
        try {
            spreadSheet.setSheet(SHEET);
            spreadSheet.setSpreadSheetID(SPREADSHEET_ID);
            spreadSheet.setSheetID(SHEET_ID);

            for (DHLTrackingVO orderShipment : orderShipments) {
                // 반짝이랑 무무는 포함하면 안됨. 여기에서 입력에 성공한 값에 대해서만 집계를 해야함.
                if (orderShipment.getShipment_class() != "regular") {
                    continue;
                }
                dataSets.add(Arrays.asList(
                        today, orderShipment.getOrder_no(), orderShipment.getTracking_no(), "",
                        orderShipment.getOrder_date(), orderShipment.getShipped_dtime(), "", "",
                        orderShipment.getShipment_issue_type(), orderShipment.getEvent(), "False", "",
                        "False", ""));
            }
            return spreadSheet.insertRows(dataSets);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Map<String, Map<String, Object>> readIncompleteSheetOrders() {
        List<String> columns = Arrays.asList("A", "B", "C", "E", "F", "G", "H", "I", "J", "K", "L", "M");

        Map<String, Map<String, Object>> excelMap = new HashMap<>();
        // rowIndex 2 부터가 실제 값임.
        int rowIndex = 1;
        for (List<Object> content : spreadSheet.getContents("A:M")) {
            if (rowIndex == 1) {
                // 헤더 부분 점프
                continue;
            } else if (content.get(13).equals("True")) {
                // 완료 여부 -> 체크 인 것들은 점프
                continue;
            } else {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i < 13; ++i) {
                    row.put(columns.get(i), content.get(i));
                }
                row.put("row", rowIndex);
                excelMap.put((String) content.get(1), row);
            }
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
            ++rowIndex;
        }
        return excelMap;
    }

    public int updateSheetRowToNewTrackingNo() {
        // 아직 완료되지 않은 건들의 운송장 리스트를 가져와서
        // DB 에서 Tracking No 최신거랑 대조해봄.
        // 기존거랑 다르면 새로 값 넣어주기.

        // 이미 처리된 건은 제외한 건들임.
        // C - 운송장
        // D - 새로운 운송장
        Map<String, Map<String, Object>> contents = readIncompleteSheetOrders();
        List<DHLTrackingVO> trackingVos = getTrackingLists(new ArrayList<String>(contents.keySet()));

        for (DHLTrackingVO vo : trackingVos) {
            String oldTrackingNo = (String) contents.get(vo.getOrder_no()).get("tracking_no");
            String newTrackingNo = vo.getTracking_no();

            if (oldTrackingNo.equals(newTrackingNo)) {
                contents.remove(vo.getOrder_no());
            }
        }

        // Map<> Contents is now only conatained with the tracking no changed data.
        if (contents.size() < 1) {
            return 0;
        } else {
            for (String orderNo : new ArrayList<>(contents.keySet())) {
                Map<String, Object> selectedRow = contents.get(orderNo);
                String range = "D" + (String) selectedRow.get("row");
                boolean isSuccessful = spreadSheet.updateRow(Arrays.asList(selectedRow.get("D")), range);
            }
            return contents.size();
        }

    }

    public boolean checkNUpdateCompleteShipments() {
        // 스프레드시트 데이터를 불러와서 완료여부가 없는 건들에 대해 운송장 검사를 한다.
        // order_no{rowNo: val, colAlph:datum... }

        // 업데이트는 한번에 칠 것.
        List<DHLTrackingVO> trackingVOs = new ArrayList<>();
        Map<String, Map<String, Object>> contents = readIncompleteSheetOrders();
        String searchableTrackingNo = "";
        for (String orderNo : new ArrayList<>(contents.keySet())) {
            try {
                String newTrackingNo = (String) contents.get(orderNo).get("D");
                if (newTrackingNo.strip().length() > 9) {
                    // 새로운 운송장 자리에 값이 있으면
                    searchableTrackingNo = newTrackingNo;
                } else {
                    searchableTrackingNo = (String) contents.get(orderNo).get("C");
                }
                // 개별 운송장 조회 함수 & // 구분 작업
                DHLTrackingVO trackingVo = dHLService.filterShipment(searchableTrackingNo);
                // 완료 안되었으면, false
                if (trackingVo.getTypeOfIssue().equals("delivered")) {
                    // 완료 되었으면, DB update and SpreadSheet Update.
                    trackingVOs.add(trackingVo);
                    String range = "M" + String.valueOf(contents.get(orderNo).get("row"));
                    spreadSheet.updateRow(Arrays.asList("True"), range);

                } else {
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        // 한번에 업데이트 칠 것.
        return insertDeliveredShipments(trackingVOs) > 0 ? true : false;
    }

    // 데이터 삽입하는 부분
    private int insertDeliveredShipments(List<DHLTrackingVO> deliveredShipments) {
        try {
            Integer isSuccessful = dhlMapper.insertDeliveredShipments(deliveredShipments);
            if (isSuccessful <= 1) {
                return deliveredShipments.size();
            } else {
                return 0;
            }
        } catch (DataIntegrityViolationException sqlCons) {
            System.out.println("이미 있는 데이터를 삽입하려고 하고 있습니다.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int insertErrorShipments(List<DHLTrackingVO> erraneousShipments) {
        for (DHLTrackingVO vo: erraneousShipments){
            System.out.println(vo.toString());
        }
        try {
            Integer isSuccessful = dhlMapper.insertErrorShipments(erraneousShipments);
            if (isSuccessful <= 1) {
                return erraneousShipments.size();
            } else {
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private List<DHLTrackingVO> getTrackingLists(List<String> orderNos) {
        try {
            return dhlMapper.getTrackingNos(orderNos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
