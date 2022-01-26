package hanpoom.internal_cron.utility.spreadsheet.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import lombok.Setter;

@Setter
public abstract class SpreadSheetAPITools {
    protected String spreadSheetID;
    protected String sheetName;
    protected int sheetID;

    abstract List<List<Object>> readSheetData(String parameter);

    abstract Integer insertRow();

    abstract Integer insertRows();

    abstract Integer updateRows();

    abstract Integer deleteRows();
}
