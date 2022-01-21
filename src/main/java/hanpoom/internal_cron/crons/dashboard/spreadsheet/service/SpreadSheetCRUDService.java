package hanpoom.internal_cron.crons.dashboard.spreadsheet.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import org.springframework.stereotype.Service;

import hanpoom.internal_cron.utility.spreadsheet.service.SpreadSheetAPIService;
import lombok.Setter;

@Service
public class SpreadSheetCRUDService {
    @Setter
    private String spreadSheetID;
    @Setter
    private String sheet;
    @Setter
    private int sheetID;

    @Setter
    public String startingColumn;
    @Setter
    public int startingRow;

    private SpreadSheetAPIService spreadSheet;

    public SpreadSheetCRUDService(SpreadSheetAPIService spreadSheet) {
        this.spreadSheet = spreadSheet;
    }

    public List<List<Object>> getContents(String range) {
        Sheets sheets = spreadSheet.getSheetsService();
        range = sheet + "!" + range;
        try {
            ValueRange response = sheets.spreadsheets().values().get(this.spreadSheetID, range).execute();
            List<List<Object>> values = response.getValues();

            if (values == null || values.isEmpty()) {
                return null;
            } else {
                return values;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public boolean insertRow(List<Object> insertObj) {
        try {
            Sheets sheet = spreadSheet.getSheetsService();

            ValueRange body = new ValueRange().setValues(
                    Arrays.asList(insertObj));

            AppendValuesResponse response = sheet.spreadsheets().values()
                    .append(this.spreadSheetID, this.sheet, body)
                    // .setValueInputOption("USER_ENTERED")
                    .setValueInputOption("RAW")
                    .setInsertDataOption("INSERT_ROWS")
                    .setIncludeValuesInResponse(true)
                    .execute();
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;

    }

    public boolean insertRows(List<List<Object>> insertObjs) {
        try {
            Sheets sheet = spreadSheet.getSheetsService();

            ValueRange body = new ValueRange().setValues(
                    insertObjs);

            AppendValuesResponse response = sheet.spreadsheets().values()
                    .append(this.spreadSheetID, this.sheet, body)
                    // .setValueInputOption("USER_ENTERED")
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .setIncludeValuesInResponse(true)
                    .execute();
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;

    }

    public boolean updateRow(List<Object> updateObj, String range) {
        try {
            Sheets sheet = spreadSheet.getSheetsService();

            ValueRange body = new ValueRange().setValues(
                    Arrays.asList(updateObj));

            UpdateValuesResponse response = sheet.spreadsheets().values()
                    .update(this.spreadSheetID, this.sheet + "!" + range, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }

    public boolean deleteRow(int rowNo) {
        try {
            Sheets sheet = spreadSheet.getSheetsService();
            DeleteDimensionRequest deleteRequest = new DeleteDimensionRequest()
                    .setRange(
                            new DimensionRange()
                                    .setSheetId(this.sheetID)
                                    .setDimension("USER_ENTERED")
                                    .setStartIndex(rowNo));
            List<Request> requests = new ArrayList<>();
            requests.add(new Request().setDeleteDimension(deleteRequest));

            BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
            sheet.spreadsheets().batchUpdate(this.spreadSheetID, body).execute();
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }
}
