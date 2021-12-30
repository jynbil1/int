package hanpoom.internal_cron.arrival.basic.statics;

import com.itextpdf.text.Rectangle;

public abstract class PDFLabel {
    // userUnit = in * 72
    // 1 in = 2.54 cm
    public final static String DHL = "dhl";
    public final static Rectangle DHL_PAPER = new Rectangle(576, 288);
    public final static float DHL_FONT = 1.0f;
    public final static Rectangle DHL_BARCODE_IMG = new Rectangle(212.4f, 46.8f);

    public final static float ARRIVAL_FONT = 1.7f;
    public final static Rectangle ARRIVAL_PAPER = new Rectangle(283.46f, 226.77f);
    public final static String ARRIVAL = "arrival";
    public final static Rectangle ARRIVAL_BARCODE_IMG = new Rectangle(150f, 30);


}
