package hanpoom.internal_cron.crons.dashboard.fedex.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.Put;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hanpoom.internal_cron.crons.dashboard.fedex.vo.FedexExcelRow;
import hanpoom.internal_cron.crons.dashboard.fedex.vo.FedexExcelRow.BooleanRow;
import hanpoom.internal_cron.crons.dashboard.fedex.vo.FedexExcelRow.FloatRow;
import hanpoom.internal_cron.crons.dashboard.fedex.vo.FedexExcelRow.IntRow;
import hanpoom.internal_cron.crons.dashboard.fedex.vo.FedexExcelRow.StringRow;
import hanpoom.internal_cron.crons.dashboard.fedex.vo.OrderShipment;
import hanpoom.internal_cron.utility.calendar.CalendarManager;
import hanpoom.internal_cron.utility.spreadsheet.service.SpreadSheetAPI;
import hanpoom.internal_cron.utility.spreadsheet.vo.UpdateSheetVO;

@Component
public class FedexSpreadSheet {

    private final static String SHEET_ID = "1G3Y2CWeYveB2KNVRduKTSgFZuOIh7Cb8JQZOO0gBDqw";
    private final static String SHEET = "Fedex";

    @Autowired
    private SpreadSheetAPI spreadSheet;

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

    public List<FedexExcelRow> readUnresolvedShipmentExcel() {
        List<FedexExcelRow> excelRows = new ArrayList<>();

        spreadSheet.setSheetName(SHEET);
        spreadSheet.setSpreadSheetID(SHEET_ID);

        try {
            List<List<String>> contents = spreadSheet.read("A:N");
            List<String> columns = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
                    "O");

            boolean isFirstLine = true;
            int rowNo = 0;
            for (List<String> content : contents) {
                ++rowNo;
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                } else if (content.get(0).equals("TRUE")) {
                    continue;
                } else {
                    excelRows.add(
                            FedexExcelRow.builder()
                                    .isCompleted(
                                            new BooleanRow(columns.get(0), rowNo, Boolean.parseBoolean(content.get(0))))
                                    .createdAt(new StringRow(columns.get(1), rowNo, content.get(1)))
                                    .orderNo(new IntRow(columns.get(2), rowNo, Integer.parseInt(content.get(2))))
                                    .shipmentNo(new IntRow(columns.get(3), rowNo, Integer.parseInt(content.get(3))))
                                    .trackingNo(new StringRow(columns.get(4), rowNo, content.get(4)))
                                    .newTrackingNo(new StringRow(columns.get(5), rowNo, content.get(5)))
                                    .shipmentClass(new StringRow(columns.get(6), rowNo, content.get(6)))
                                    .orderedAt(new StringRow(columns.get(7), rowNo, content.get(7)))
                                    .shippedAt(new StringRow(columns.get(8), rowNo, content.get(8)))
                                    .deliveredAt(new StringRow(columns.get(9), rowNo, content.get(9)))
                                    .shipDuration(new FloatRow(columns.get(10), rowNo,
                                            Float.parseFloat(content.get(10).isEmpty() ? "0" : "")))
                                    .shipIssueType(new StringRow(columns.get(11), rowNo, content.get(11)))
                                    .remark(new StringRow(columns.get(12), rowNo, content.get(12)))
                                    .detail(new StringRow(columns.get(13), rowNo, content.get(13)))
                                    .build());
                }
            }
            return excelRows;
        } catch (NullPointerException npe) {
            System.out.println(SHEET);
            System.out.println(SHEET_ID);
            System.out.println(LocalDateTime.now().toString() + " -> 통합시트 데이터 불러오는 과정에서 오류 남.");
            return null;
        }
    }

    public void insertNewTrackingNumbers(List<FedexExcelRow> updatableRows) {
        spreadSheet.setSheetName(SHEET);
        spreadSheet.setSpreadSheetID(SHEET_ID);

        for (FedexExcelRow row : updatableRows) {
            String cellAt = new StringBuilder()
                    .append(row.getNewTrackingNo().getColumn())
                    .append(String.valueOf(row.getNewTrackingNo().getRow()))
                    .toString();

            spreadSheet.updateRow(new JSONArray().put(row.getTrackingNo().getValue()), cellAt);
        }
    }

    public void checkNRecordShipment(FedexExcelRow row) {
        spreadSheet.setSheetName(SHEET);
        spreadSheet.setSpreadSheetID(SHEET_ID);
        JSONArray array = new JSONArray(row.toString()); 
                
        String cellAt = new StringBuilder()
                .append("A")
                .append(String.valueOf(row.getIsCompleted().getRow()))
                .toString();

        spreadSheet.updateRow(array, cellAt);

    }

}
