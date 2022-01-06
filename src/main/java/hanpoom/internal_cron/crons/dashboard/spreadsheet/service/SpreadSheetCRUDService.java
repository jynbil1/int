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

@Service
public class SpreadSheetCRUDService {
    private final static String SPREADSHEET_ID = "114n3w9q8ytp0z5zFoiOo1xg_cP2nt3yspYKQJvT1KuU";
    private final static String SHEET = "22 GLOBAL_DASHBOARD";
    private final static int SHEET_ID = 1110823798;

    private SpreadSheetAPIService spreadSheet;

    public SpreadSheetCRUDService(SpreadSheetAPIService spreadSheet) {
        this.spreadSheet = spreadSheet;
    }

    public List<List<Object>> getContents(String range) {
        Sheets sheet = spreadSheet.getSheetsService();
        range = SHEET + "!" + range;
        try {
            ValueRange response = sheet.spreadsheets().values().get(SPREADSHEET_ID, range).execute();
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
                    Arrays.asList(
                            Arrays.asList(insertObj)));

            AppendValuesResponse resposne = sheet.spreadsheets().values()
                    .append(SPREADSHEET_ID, "22 GLOBAL_DASHBOARD", body)
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

    public boolean updateRow(List<Object> updateObj) {
        try {
            Sheets sheet = spreadSheet.getSheetsService();

            ValueRange body = new ValueRange().setValues(
                    Arrays.asList(
                            Arrays.asList(updateObj)));

            UpdateValuesResponse resposne = sheet.spreadsheets().values()
                    .update(SPREADSHEET_ID, "22 GLOBAL_DASHBOARD!D5:G5", body)
                    .setValueInputOption("RAW")
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
                                    .setSheetId(SHEET_ID)
                                    .setDimension("ROWS")
                                    .setStartIndex(rowNo));
            List<Request> requests = new ArrayList<>();
            requests.add(new Request().setDeleteDimension(deleteRequest));

            BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
            sheet.spreadsheets().batchUpdate(SPREADSHEET_ID, body).execute();
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }
}
