package hanpoom.internal_cron.utility.spreadsheet.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONArray;

import hanpoom.internal_cron.utility.spreadsheet.vo.UpdateSheetVO;
import lombok.Setter;

@Setter
public abstract class SpreadSheetAPITools {
    protected String spreadSheetID;
    protected String sheetName;
    protected int sheetID;
    protected String token;
    protected String cellAt;

    private static final String ENCODING = "utf-8";

    // public void setSheetName(String sheetName, boolean containsNonEnglishChar) {
    // if (containsNonEnglishChar){
    // try {
    // this.sheetName = URLEncoder.encode(sheetName, ENCODING);
    // } catch (UnsupportedEncodingException e) {
    // e.printStackTrace();
    // }
    // } else {
    // this.sheetName = sheetName;
    // }

    // }
    public void setSheetName(String sheetName) {
        try {
            this.sheetName = URLEncoder.encode(sheetName, ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    abstract List<List<Object>> readSheetData(String parameter);
    abstract List<List<String>> read(String range);
    abstract UpdateSheetVO insertRows(JSONArray rows);
    abstract UpdateSheetVO updateRow(JSONArray rows, String range);
    abstract UpdateSheetVO updateRows(JSONArray rows);
    abstract UpdateSheetVO deleteRows(JSONArray rows);

    abstract String tokenValidator();

}
