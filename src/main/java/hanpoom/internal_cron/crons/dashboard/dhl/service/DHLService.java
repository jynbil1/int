package hanpoom.internal_cron.crons.dashboard.dhl.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.crons.dashboard.dhl.mapper.DHLMapper;
import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackingResult;
import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackingVO;
import hanpoom.internal_cron.utility.spreadsheet.service.SpreadSheetAPI;
import hanpoom.internal_cron.utility.spreadsheet.vo.UpdateSheetVO;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;

@Service
public class DHLService {
    // Real
    private static final String SHIPMENT_REPORT_HOOK_URL = "https://hooks.slack.com/services/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";

    // Testing
    // private static final String SHIPMENT_REPORT_HOOK_URL =
    // "https://hooks.slack.com/services/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";

    private final static String SPREADSHEET_ID = "1G3Y2CWeYveB2KNVRduKTSgFZuOIh7Cb8JQZOO0gBDqw";
    private final static String SHEET = "배송상황";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private DHLMapper mapper;
    private SpreadSheetAPI spreadSheetApi;

    public DHLService(DHLMapper mapper,
            SpreadSheetAPI spreadSheetApi) {
        this.mapper = mapper;
        this.spreadSheetApi = spreadSheetApi;
    }

    // DB DATA Processing
    public List<DHLTrackingVO> getTrackableOrders() {
        List<DHLTrackingVO> trackingVOs = new ArrayList<>();
        try {
            // 지금 현재부터 60일 전 까지의 Shipped 데이터를 가져오기.
            String start_dtime = LocalDateTime.now().minusDays(100)
                    .format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
            String end_dtime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));

            Map<String, String> dateRange = new HashMap<>();
            dateRange.put("start_dtime", start_dtime);
            dateRange.put("end_dtime", end_dtime);
            System.out.println(String.format("%s 부터 %s 까지의 기록을 조회합니다.", start_dtime, end_dtime));
            trackingVOs = mapper.getTrackableOrders();
            System.out.println(String.valueOf(trackingVOs.size()) + " 개를 조회합니다.");

            return trackingVOs;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    };

    public DHLTrackingVO getOrderDetailByTrackingNo(DHLTrackingVO searcVo) {
        try {
            return mapper.getOrderDetailByTrackingNo(searcVo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DHLTrackingVO> getTrackingLists(List<String> orderNos) {
        try {
            return mapper.getTrackingNos(orderNos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 데이터 삽입하는 부분
    // 배송 완료된 데이터를 삽입함.
    public int insertDeliveredShipments(List<DHLTrackingVO> deliveredShipments) {
        try {
            Integer isSuccessful = mapper.insertDeliveredShipments(deliveredShipments);
            if (isSuccessful <= 1) {
                return deliveredShipments.size();
            } else {
                return 0;
            }
        } catch (DataIntegrityViolationException sqlCons) {
            System.out.println(sqlCons.getMessage());
            System.out.println("이미 있는 데이터를 삽입하려고 하고 있습니다.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 문제가 있는 배송건들에 대해 삽입함.
    private int insertErrorShipments(List<DHLTrackingVO> erraneousShipments) {
        for (DHLTrackingVO vo : erraneousShipments) {
            System.out.println(vo.toString());
        }
        try {
            Integer isSuccessful = mapper.insertErrorShipments(erraneousShipments);
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

    // 통관/지연 등의 문제가 파악 된 건들을 처리
    public void processIssueShipments(List<DHLTrackingVO> trackingVoList) {
        insertErrorShipments(trackingVoList);
        insertIntoSpreadSheet(trackingVoList);
    }

    // 슬랙 메시지 보내는 부분.
    public boolean sendSlackMessage(String messageText) {
        SlackApi api = new SlackApi(SHIPMENT_REPORT_HOOK_URL);
        SlackMessage slackMessage = new SlackMessage();

        try {
            // System.out.println(messageText);
            slackMessage.setText(messageText);
            api.call(slackMessage);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public Map<String, Map<String, Object>> readIncompleteSheetOrders() {

        spreadSheetApi.setSheetName(SHEET);
        spreadSheetApi.setSpreadSheetID(SPREADSHEET_ID);

        List<List<Object>> contents = new ArrayList<>();
        try {
            contents = spreadSheetApi.readSheetData("A:N");
        } catch (NullPointerException npe) {
            System.out.println(SHEET);
            System.out.println(SPREADSHEET_ID);
            System.out.println(LocalDateTime.now().toString() + "통합시트 데이터 불러오는 과정에서 오류 남.");
            return null;
        }

        List<String> columns = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N");

        Map<String, Map<String, Object>> excelMap = new HashMap<>();
        // rowIndex 2 부터가 실제 값임.
        int rowIndex = 0;
        for (List<Object> content : contents) {
            ++rowIndex;
            boolean isCompleted = (boolean) content.get(0).equals("TRUE");
            if (rowIndex <= 1) {
                // 헤더 부분 점프
                continue;
            } else if (isCompleted) {
                // 완료 여부 -> 체크 인 것들은 점프
                continue;
            } else {
                Map<String, Object> row = new HashMap<>();
                for (int i = 0; i < columns.size() - 1; ++i) {
                    // Alphabet: 값
                    row.put(columns.get(i), content.get(i));
                }
                row.put("row", rowIndex);
                excelMap.put((String) content.get(3), row);
            }
        }
        return excelMap;
    }

    // 스프레드 시트에 CRUD하는 부분.
    public UpdateSheetVO insertIntoSpreadSheet(List<DHLTrackingVO> orderShipments) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        // List<List<Object>> dataSets = new ArrayList<>();
        // 생성일자 주문번호 송장번호 변경된송장
        // 주문일자 출고일자 배송완료일자 배송기간(n.m일)
        // 배송상태구분 배송특이사항 DHL문의여부 DHL확인내용
        // 완료여부 비고
        try {
            spreadSheetApi.setSheetName(SHEET);
            spreadSheetApi.setSpreadSheetID(SPREADSHEET_ID);

            JSONArray jsonArray = new JSONArray();
            for (DHLTrackingVO orderShipment : orderShipments) {
                JSONArray subArray = new JSONArray();

                // 반짝이랑 무무는 포함하면 안됨. 여기에서 입력에 성공한 값에 대해서만 집계를 해야함.

                for (Object obj : Arrays.asList(
                        "FALSE", today, orderShipment.getShipment_class(), orderShipment.getOrder_no(),
                        orderShipment.getTracking_no(), "",
                        orderShipment.getOrder_date() == null ? "" : orderShipment.getOrder_date(),
                        orderShipment.getShipped_dtime() == null ? "" : orderShipment.getShipped_dtime(),
                        "", "",
                        orderShipment.getShipment_issue_type() == null ? "" : orderShipment.getShipment_issue_type(),
                        orderShipment.getEvent(), "FALSE", "", "")) {
                    subArray.put(obj);
                }
                jsonArray.put(subArray);
            }
            return spreadSheetApi.insertRows(jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
