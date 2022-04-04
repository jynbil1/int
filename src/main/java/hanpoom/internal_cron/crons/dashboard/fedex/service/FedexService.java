package hanpoom.internal_cron.crons.dashboard.fedex.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.crons.dashboard.fedex.mapper.FedexMapper;
import hanpoom.internal_cron.crons.dashboard.fedex.vo.OrderShipment;
import hanpoom.internal_cron.utility.calendar.CalendarFormatter;
import hanpoom.internal_cron.utility.spreadsheet.service.SpreadSheetAPI;
import hanpoom.internal_cron.utility.spreadsheet.vo.UpdateSheetVO;

@Service
public class FedexService {
    private final static String SHEET_ID = "1G3Y2CWeYveB2KNVRduKTSgFZuOIh7Cb8JQZOO0gBDqw";
    private final static String SHEET = "Fedex";

    @Autowired
    private FedexMapper fedexMapper;
    @Autowired
    private SpreadSheetAPI spreadSheet;

    public List<OrderShipment> getShippedFedexOrders() {
        try {
            return fedexMapper.getOrderShipments();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void insertDeliveredShipments(List<OrderShipment> orders) {
        try {
            fedexMapper.insertDeliveredShipments(orders);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertErrorShipments(List<OrderShipment> orders) {
        try {
            fedexMapper.insertErrorShipments(orders);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String getTrackReportMsg(Map<String, String> workResult) {
        StringBuilder sb = new StringBuilder();
        sb
                .append("***")
                .append(CalendarFormatter.toKoreanDate(LocalDate.now()))
                .append(" ")
                .append(String.valueOf(LocalDateTime.now().getHour()))
                .append("시 Fedex 배송 현황***\n")
                .append("---------------------------------------------------")
                .append("배송 완료: ")
                .append(workResult.get("delivered"))
                .append("건\n\n 배송 지연: ")
                .append(workResult.get("delayed"))
                .append("건\n 조회 불가: ")
                .append(workResult.get("untrackable"))
                .append("건\n 이외 문제: ")
                .append(workResult.get("others"))

                .append("건\n\n 반송 완료: ")
                .append(workResult.get("returned"))

                .append("---------------------------------------------------")
                .append("배송중: ")
                .append(workResult.get("inTransit"))
                .append("건\n")
                .append("<https://docs.google.com/spreadsheets/d/1G3Y2CWeYveB2KNVRduKTSgFZuOIh7Cb8JQZOO0gBDqw/edit#gid=1386751274|문제 보러가기>");

        return sb.toString();
    }

    public UpdateSheetVO insertIntoFedexSheet(List<OrderShipment> shipments) {
        JSONArray rows = new JSONArray();
        try {
            spreadSheet.setSheetName(SHEET);
            spreadSheet.setSpreadSheetID(SHEET_ID);

            for (OrderShipment shipment : shipments) {

                rows.put(
                        new JSONArray().put(
                                Arrays.asList(
                                        "FALSE", shipment.getToday(), shipment.getShipmentClass(),
                                        shipment.getOrderNo(), shipment.getShipmentNo(), shipment.getTrackingNo(),
                                        shipment.getOrderDate(), shipment.getShippedDate(), "",
                                        "", shipment.getServiceType(),
                                        shipment.getIssueType(),
                                        shipment.getDetail())));

            }
            return spreadSheet.insertRows(rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
