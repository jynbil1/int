package hanpoom.internal_cron.arrival.basic.vo;

import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PDFFontVO {
    private BaseFont baseFont;

    private Font headerFont;
    private Font productFont;
    private Font contentFont;
    private Font commentFont;
    private Font dateFont;
    private Font expDateFont;

    public PDFFontVO() {
    }

    public PDFFontVO(float paperSize, String fontPath) {
        try {
            baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            headerFont = new Font(baseFont, 14 / paperSize);
            productFont = new Font(baseFont, 28 / paperSize);
            contentFont = new Font(baseFont, 16 / paperSize);
            commentFont = new Font(baseFont, 12 / paperSize);
            dateFont = new Font(baseFont, 23 / paperSize);
            expDateFont = new Font(baseFont, 28 / paperSize, Font.BOLD);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
