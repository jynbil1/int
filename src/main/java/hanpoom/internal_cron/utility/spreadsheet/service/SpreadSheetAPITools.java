package hanpoom.internal_cron.utility.spreadsheet.service;

import java.util.List;

import lombok.Setter;

@Setter
public abstract class SpreadSheetAPITools {
    protected String spreadSheetID;
    protected String sheetName;
    protected int sheetID;
    protected String token;

    abstract List<List<Object>> readSheetData(String parameter);

    abstract Integer insertRow(List<Object> row);

    abstract Integer insertRows(List<List<Object>> rows);

    abstract Integer updateRows();

    abstract Integer deleteRows();

    abstract String tokenValidator();
    
}
