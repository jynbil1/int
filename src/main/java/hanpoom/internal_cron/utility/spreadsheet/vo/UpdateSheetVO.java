package hanpoom.internal_cron.utility.spreadsheet.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UpdateSheetVO {
    private String spreadSheetId;
    private String updatedRange;
    private String updatedRows;
    private String updatedColumns;
    private String updatedCells;

    public UpdateSheetVO(String spreadSheetId, String updatedRange, int updatedRows, int updatedColumns,
            int updatedCells) {
        this.spreadSheetId = spreadSheetId;
        this.updatedRange = updatedRange;
        this.updatedRows = String.valueOf(updatedRows);
        this.updatedColumns = String.valueOf(updatedColumns);
        this.updatedCells = String.valueOf(updatedCells);

    }

}
