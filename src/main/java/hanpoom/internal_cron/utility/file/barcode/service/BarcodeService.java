package hanpoom.internal_cron.utility.file.barcode.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.springframework.stereotype.Service;

@Service
public class BarcodeService {

    public BarcodeService() {
    }

    public byte[] generateCode128(String barcodableValue, boolean displayReadableText) {
        Code128Bean bean = new Code128Bean();
        final int dpi = 250;
        // style
        bean.setModuleWidth(0.21);
        bean.setBarHeight(7);
        bean.doQuietZone(false);
        bean.setQuietZone(2);

        // Blank areas on both sides
        bean.setFontName("Helvetica");
        bean.setFontSize(3);
        if (displayReadableText) {
            bean.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
        } else {
            bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        }

        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(out, "image/png", dpi,
                    BufferedImage.TYPE_BYTE_BINARY, true, 0);

            bean.generateBarcode(canvas, barcodableValue);
            canvas.finish();
            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
