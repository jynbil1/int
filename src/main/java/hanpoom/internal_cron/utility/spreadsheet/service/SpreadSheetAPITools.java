package hanpoom.internal_cron.utility.spreadsheet.service;

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

    abstract List<List<Object>> readSheetData(String parameter);

    abstract UpdateSheetVO insertRows(JSONArray row);

    abstract UpdateSheetVO updateRows();

    abstract UpdateSheetVO deleteRows();

    abstract String tokenValidator();
    
}
