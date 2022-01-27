package hanpoom.internal_cron.utility.spreadsheet.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UpdateSheetVO {
    private String spreadSheetId;
    private String updatedSheetName;
    private String updatedRange;
    private int updatedRows;
    private int updatedColumns;
    private int updatedCells;

    public UpdateSheetVO(String spreadSheetId, String updatedRange, int updatedRows, int updatedColumns,
            int updatedCells) {

        this.spreadSheetId = spreadSheetId;
        String[] splitedSheetNRange = updatedRange.split("!");

        this.updatedSheetName = splitedSheetNRange[0].replace("'", "");
        this.updatedRange = splitedSheetNRange[1];

        this.updatedRows = updatedRows;
        this.updatedColumns = updatedColumns;
        this.updatedCells = updatedCells;

        // this.updatedRows = String.valueOf(updatedRows);
        // this.updatedColumns = String.valueOf(updatedColumns);
        // this.updatedCells = String.valueOf(updatedCells);
    }

}
