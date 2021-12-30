package hanpoom.internal_cron.arrival.basic.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import hanpoom.internal_cron.arrival.basic.mapper.PDFGeneratorMapper;
import hanpoom.internal_cron.arrival.basic.statics.PDFLabel;
import hanpoom.internal_cron.arrival.basic.vo.ArrivalProductVO;
import hanpoom.internal_cron.arrival.basic.vo.PDFFontVO;
import hanpoom.internal_cron.utilities.file.barcode.service.BarcodeService;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PDFGeneratorService {
    private final static String FONT_STYLE = "src/main/resources/fonts/eland_choice_m.ttf";
    private PDFGeneratorMapper pDFGeneratorMapper;
    private BarcodeService barcodeService;

    public PDFGeneratorService(PDFGeneratorMapper pDFGeneratorMapper, BarcodeService barcodeService) {
        this.pDFGeneratorMapper = pDFGeneratorMapper;
        this.barcodeService = barcodeService;
    }

    public ArrivalProductVO getArrivalProductLabelDatum(String arrivalSeq) {
        ArrivalProductVO arrivalProductDetails = null;
        try {
            arrivalProductDetails = pDFGeneratorMapper.getInProductLabelDatum(arrivalSeq);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrivalProductDetails;
    }

    private ArrayList<ArrivalProductVO> getArrivalProductLabelData(ArrayList<String> arrivalSeqs) {
        ArrayList<ArrivalProductVO> arrivalProductDetails = null;
        try {
            arrivalProductDetails = pDFGeneratorMapper.getInProductLabelData(arrivalSeqs);
            System.out.println(arrivalProductDetails.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrivalProductDetails;
    }

    public byte[] validatePDFArrivalLabel(String arrivalSeq, String requester) {

        ArrivalProductVO arrivalProductVo = this.getArrivalProductLabelDatum(arrivalSeq);
        Document document;
        PDFFontVO fontVo;
        Rectangle barcodeImageRect;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String fontPath = new File(FONT_STYLE).getAbsolutePath();

        document = new Document(PDFLabel.ARRIVAL_PAPER, 0, 0, 0, 0);
        fontVo = new PDFFontVO(PDFLabel.ARRIVAL_FONT, fontPath);
        barcodeImageRect = PDFLabel.ARRIVAL_BARCODE_IMG;

        try {
            PdfWriter.getInstance(document, baos);

            document.open();

            PdfPTable table = new PdfPTable(3);
            table.setWidths(new float[] { 1, 1, 8 });

            PdfPCell productNameHeader = new PdfPCell(new Phrase("제\n\n품\n\n명", fontVo.getHeaderFont()));
            String productName = arrivalProductVo.getProduct_name();
            System.out.println(productName.length());

            float varProductNameSize;
            switch (productName.length() / 10) {
                case 1:
                    varProductNameSize = 23f;
                    break;
                case 2:
                    varProductNameSize = 22f;
                    break;
                case 3:
                    varProductNameSize = 21f;
                    break;
                case 4:
                    varProductNameSize = 20f;
                    break;
                case 5:
                    varProductNameSize = 19f;
                    break;
                case 6:
                    varProductNameSize = 18f;
                    break;
                case 7:
                    varProductNameSize = 17f;
                    break;
                case 8:
                    varProductNameSize = 16f;
                    break;
                case 9:
                    varProductNameSize = 15f;
                    break;
                default:
                    varProductNameSize = 14f;
                    break;
            }
            fontVo.setProductFont(new Font(fontVo.getBaseFont(), varProductNameSize));

            PdfPCell product = new PdfPCell(new Phrase(productName, fontVo.getProductFont()));
            PdfPCell qtyHeader = new PdfPCell(new Phrase("수량", fontVo.getContentFont()));
            PdfPCell qtyInHeader = new PdfPCell(new Phrase("입고", fontVo.getContentFont()));
            PdfPCell qtyInUnit = new PdfPCell(new Phrase("단위", fontVo.getContentFont()));
            PdfPCell qtyIn = new PdfPCell(
                    new Phrase(String.format("%,d", arrivalProductVo.getWh_arrival_qty()), fontVo.getContentFont()));

            PdfPCell barcode = null;
            PdfPCell barcodeHumanText = null;
            System.out.println(arrivalProductVo.getWh_arrival_barcode());
            if (arrivalProductVo.getWh_arrival_barcode() != null && arrivalProductVo.getWh_arrival_barcode() != "") {

                String barcodeValue = arrivalProductVo.getWh_arrival_barcode();
                Image barcodeImage = Image.getInstance(barcodeService.generateCode128(barcodeValue, false));
                barcodeImage.scaleAbsolute(barcodeImageRect);
                barcode = new PdfPCell(barcodeImage);

                String[] barcodeValueArr = barcodeValue.split("");
                String newBarcodeText = "";
                for (String tmpText : barcodeValueArr) {
                    newBarcodeText += (tmpText + "   ");
                }
                newBarcodeText = newBarcodeText.strip();

                barcodeHumanText = new PdfPCell(new Phrase(newBarcodeText, fontVo.getContentFont()));

            } else {
                barcode = new PdfPCell(new Phrase("", fontVo.getContentFont()));
                barcodeHumanText = new PdfPCell(new Phrase("N / A", fontVo.getContentFont()));
            }

            PdfPCell expDateHeader = new PdfPCell(new Phrase("유통기한", fontVo.getContentFont()));
            PdfPCell expDate = new PdfPCell(
                    new Phrase(arrivalProductVo.getWh_arrival_expdate(), fontVo.getExpDateFont()));

            PdfPCell pickLocationHeader = new PdfPCell(new Phrase("운영위치", fontVo.getContentFont()));
            PdfPCell pickLocation = new PdfPCell(
                    new Phrase(arrivalProductVo.getWh_pick_location(), fontVo.getDateFont()));

            PdfPCell inDateHeader = new PdfPCell(new Phrase("입하일자", fontVo.getContentFont()));
            PdfPCell inDate = new PdfPCell(new Phrase(arrivalProductVo.getWh_arrival_date(), fontVo.getDateFont()));

            PdfPCell orderDateHeader = new PdfPCell(new Phrase("발주일자", fontVo.getContentFont()));
            PdfPCell orderDate = new PdfPCell(new Phrase(arrivalProductVo.getOrder_date(), fontVo.getDateFont()));

            PdfPCell emptyCell = new PdfPCell(new Phrase("", fontVo.getContentFont()));

            for (PdfPCell cell : Arrays.asList(productNameHeader, product, qtyHeader, qtyInHeader, qtyInUnit, qtyIn,
                    barcode, barcodeHumanText, expDateHeader, expDate, pickLocationHeader, pickLocation, inDateHeader,
                    inDate, orderDateHeader, orderDate, emptyCell)) {
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            }

            // 그리고 테이블에 위에서 생성시킨 셀을 넣는다.
            product.setColspan(2);

            qtyHeader.setRowspan(2);
            // qtyIn.setColspan();
            // emptyCell.setColspan(2);

            pickLocationHeader.setColspan(2);
            orderDateHeader.setColspan(2);
            expDateHeader.setColspan(2);
            inDateHeader.setColspan(2);

            barcode.setRowspan(2);
            barcode.setColspan(3);
            barcodeHumanText.setColspan(3);

            table.addCell(productNameHeader);
            table.addCell(product);
            table.completeRow();

            table.addCell(qtyHeader);
            table.addCell(qtyInHeader);
            table.addCell(qtyIn);
            table.completeRow();

            table.addCell(qtyInUnit);
            table.addCell(emptyCell);
            table.completeRow();

            table.addCell(pickLocationHeader);
            table.addCell(pickLocation);
            table.completeRow();

            table.addCell(orderDateHeader);
            table.addCell(orderDate);
            table.completeRow();

            table.addCell(inDateHeader);
            table.addCell(inDate);
            table.completeRow();

            table.addCell(expDateHeader);
            table.addCell(expDate);
            table.completeRow();

            table.addCell(barcode);
            table.completeRow();

            table.addCell(barcodeHumanText);
            table.completeRow();

            // 출력시간을 작게나마 보여준다.
            PdfPCell bottomCell = new PdfPCell(new Phrase(
                    new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA).format(new Date()) + " by " + requester,
                    fontVo.getCommentFont()));
            bottomCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            bottomCell.setColspan(5);
            table.addCell(bottomCell);
            table.completeRow();

            table.setWidthPercentage(100);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            document.add(table); // 웹접근 객체에 table를 저장한다.
            // return document;
        } catch (Exception e) {
            e.printStackTrace();
            // return null;
        } finally {
            document.close(); // 저장이 끝났으면 document객체를 닫는다.
        }
        return baos.toByteArray();
    }

    public byte[] validatePDFDHLLabel(String arrivalSeq, String requester) {

        ArrivalProductVO arrivalProductVo = this.getArrivalProductLabelDatum(arrivalSeq);
        Document document;
        PDFFontVO fontVo;
        Rectangle barcodeImageRect;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String fontPath = new File(FONT_STYLE).getAbsolutePath();

        document = new Document(PDFLabel.DHL_PAPER, 0, 0, 0, 0);
        fontVo = new PDFFontVO(PDFLabel.DHL_FONT, fontPath);
        barcodeImageRect = PDFLabel.DHL_BARCODE_IMG;

        try {
            PdfWriter.getInstance(document, baos);

            document.open();

            PdfPTable table = new PdfPTable(5);
            table.setWidths(new float[] { 1, 1, 2, 1, 3 });

            PdfPCell productNameHeader = new PdfPCell(new Phrase("제\n\n품\n\n명", fontVo.getHeaderFont()));
            PdfPCell productName = new PdfPCell(
                    new Phrase(arrivalProductVo.getProduct_name(), fontVo.getProductFont()));

            PdfPCell qtyHeader = new PdfPCell(new Phrase("수\n\n량", fontVo.getContentFont()));
            PdfPCell qtyInHeader = new PdfPCell(new Phrase("입고", fontVo.getContentFont()));
            PdfPCell qtyInUnit = new PdfPCell(new Phrase("단위", fontVo.getContentFont()));
            PdfPCell qtyIn = new PdfPCell(
                    new Phrase(String.valueOf(arrivalProductVo.getWh_arrival_qty()), fontVo.getContentFont()));

            PdfPCell barcodeHeader = new PdfPCell(new Phrase("바\n코\n드", fontVo.getContentFont()));
            PdfPCell barcode = null;
            if (arrivalProductVo.getWh_arrival_barcode() != null) {
                Image barcodeImage = Image
                        .getInstance(barcodeService.generateCode128(arrivalProductVo.getWh_arrival_barcode(), false));
                barcodeImage.scaleAbsolute(barcodeImageRect);
                barcode = new PdfPCell(barcodeImage);

            } else {
                barcode = new PdfPCell(new Phrase("N/A", fontVo.getContentFont()));
            }

            PdfPCell expDateHeader = new PdfPCell(new Phrase("유\n통\n기\n한", fontVo.getContentFont()));
            PdfPCell expDate = new PdfPCell(
                    new Phrase(arrivalProductVo.getWh_arrival_expdate(), fontVo.getExpDateFont()));

            PdfPCell pickLocationHeader = new PdfPCell(new Phrase("운\n영\n위\n치", fontVo.getContentFont()));
            PdfPCell pickLocation = new PdfPCell(
                    new Phrase(arrivalProductVo.getWh_pick_location(), fontVo.getDateFont()));

            PdfPCell inDateHeader = new PdfPCell(new Phrase("입\n하\n일\n자", fontVo.getContentFont()));
            PdfPCell inDate = new PdfPCell(new Phrase(arrivalProductVo.getWh_arrival_date(), fontVo.getDateFont()));

            PdfPCell orderDateHeader = new PdfPCell(new Phrase("발\n주\n일\n자", fontVo.getContentFont()));
            PdfPCell orderDate = new PdfPCell(new Phrase(arrivalProductVo.getOrder_date(), fontVo.getDateFont()));

            PdfPCell emptyCell = new PdfPCell(new Phrase("", fontVo.getContentFont()));

            for (PdfPCell cell : Arrays.asList(productNameHeader, productName, qtyHeader, qtyInHeader, qtyInUnit, qtyIn,
                    barcodeHeader, barcode, expDateHeader, expDate, pickLocationHeader, pickLocation, inDateHeader,
                    inDate, orderDateHeader, orderDate, emptyCell)) {
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            }

            // 그리고 테이블에 위에서 생성시킨 셀을 넣는다.
            productName.setColspan(4);

            qtyHeader.setRowspan(2);
            // qtyIn.setColspan();
            // emptyCell.setColspan(2);

            barcodeHeader.setRowspan(2);
            barcode.setRowspan(2);

            expDate.setColspan(2);
            expDate.setColspan(2);

            inDate.setColspan(2);

            table.addCell(productNameHeader);
            table.addCell(productName);
            table.completeRow();

            table.addCell(qtyHeader);
            table.addCell(qtyInHeader);
            table.addCell(qtyIn);
            table.addCell(barcodeHeader);
            table.addCell(barcode);
            table.completeRow();

            table.addCell(qtyInUnit);
            table.addCell(emptyCell);
            table.completeRow();

            table.addCell(expDateHeader);
            table.addCell(expDate);
            table.addCell(pickLocationHeader);
            table.addCell(pickLocation);
            table.completeRow();

            table.addCell(inDateHeader);
            table.addCell(inDate);
            table.addCell(orderDateHeader);
            table.addCell(orderDate);
            table.completeRow();

            // 출력시간을 작게나마 보여준다.
            PdfPCell bottomCell = new PdfPCell(
                    new Phrase(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()) + " by " + requester,
                            fontVo.getCommentFont()));
            bottomCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            bottomCell.setColspan(5);
            table.addCell(bottomCell);
            table.completeRow();

            table.setWidthPercentage(100);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            document.add(table); // 웹접근 객체에 table를 저장한다.
            // return document;
        } catch (Exception e) {
            e.printStackTrace();
            // return null;
        } finally {
            document.close(); // 저장이 끝났으면 document객체를 닫는다.
        }
        return baos.toByteArray();
    }

    public String validateMultiplePDFArrivalLabel(HashMap<String, Integer> arrivalSeqQty, String requester) {
        // 입하 데이터 가져오기
        ArrayList<ArrivalProductVO> arrvivedProductLists = getArrivalProductLabelData(
                new ArrayList<String>(arrivalSeqQty.keySet()));

        Document document;
        PDFFontVO fontVo;
        Rectangle barcodeImageRect;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String fontPath = new File(FONT_STYLE).getAbsolutePath();

        document = new Document(PDFLabel.ARRIVAL_PAPER, 0, 0, 0, 0);
        fontVo = new PDFFontVO(PDFLabel.ARRIVAL_FONT, fontPath);
        barcodeImageRect = PDFLabel.ARRIVAL_BARCODE_IMG;

        try {
            PdfWriter.getInstance(document, baos);

            document.open();
            for (ArrivalProductVO arrivalProductVo : arrvivedProductLists) {
                System.out.println(arrivalSeqQty.get(arrivalProductVo.getWh_arrival_seq()));
                System.out.println(arrivalProductVo.getWh_arrival_seq());
                
                for (int i = arrivalSeqQty.get(arrivalProductVo.getWh_arrival_seq()); i > 0 ; --i) {

                    PdfPTable table = new PdfPTable(3);
                    table.setWidths(new float[] { 1, 1, 8 });

                    PdfPCell productNameHeader = new PdfPCell(new Phrase("제\n\n품\n\n명", fontVo.getHeaderFont()));
                    String productName = arrivalProductVo.getProduct_name();
                    System.out.println(productName.length());

                    float varProductNameSize;
                    switch (productName.length() / 10) {
                        case 1:
                            varProductNameSize = 23f;
                            break;
                        case 2:
                            varProductNameSize = 22f;
                            break;
                        case 3:
                            varProductNameSize = 21f;
                            break;
                        case 4:
                            varProductNameSize = 20f;
                            break;
                        case 5:
                            varProductNameSize = 19f;
                            break;
                        case 6:
                            varProductNameSize = 18f;
                            break;
                        case 7:
                            varProductNameSize = 17f;
                            break;
                        case 8:
                            varProductNameSize = 16f;
                            break;
                        case 9:
                            varProductNameSize = 15f;
                            break;
                        default:
                            varProductNameSize = 14f;
                            break;
                    }
                    fontVo.setProductFont(new Font(fontVo.getBaseFont(), varProductNameSize));

                    PdfPCell product = new PdfPCell(new Phrase(productName, fontVo.getProductFont()));
                    PdfPCell qtyHeader = new PdfPCell(new Phrase("수량", fontVo.getContentFont()));
                    PdfPCell qtyInHeader = new PdfPCell(new Phrase("입고", fontVo.getContentFont()));
                    PdfPCell qtyInUnit = new PdfPCell(new Phrase("단위", fontVo.getContentFont()));
                    PdfPCell qtyIn = new PdfPCell(new Phrase(String.format("%,d", arrivalProductVo.getWh_arrival_qty()),
                            fontVo.getContentFont()));

                    PdfPCell barcode = null;
                    PdfPCell barcodeHumanText = null;
                    System.out.println(arrivalProductVo.getWh_arrival_barcode());
                    if (arrivalProductVo.getWh_arrival_barcode() != null
                            && arrivalProductVo.getWh_arrival_barcode() != "") {

                        String barcodeValue = arrivalProductVo.getWh_arrival_barcode();
                        Image barcodeImage = Image.getInstance(barcodeService.generateCode128(barcodeValue, false));
                        barcodeImage.scaleAbsolute(barcodeImageRect);
                        barcode = new PdfPCell(barcodeImage);

                        String[] barcodeValueArr = barcodeValue.split("");
                        String newBarcodeText = "";
                        for (String tmpText : barcodeValueArr) {
                            newBarcodeText += (tmpText + "   ");
                        }
                        newBarcodeText = newBarcodeText.strip();

                        barcodeHumanText = new PdfPCell(new Phrase(newBarcodeText, fontVo.getContentFont()));

                    } else {
                        barcode = new PdfPCell(new Phrase("", fontVo.getContentFont()));
                        barcodeHumanText = new PdfPCell(new Phrase("N / A", fontVo.getContentFont()));
                    }

                    PdfPCell expDateHeader = new PdfPCell(new Phrase("유통기한", fontVo.getContentFont()));
                    PdfPCell expDate = new PdfPCell(
                            new Phrase(arrivalProductVo.getWh_arrival_expdate(), fontVo.getExpDateFont()));

                    PdfPCell pickLocationHeader = new PdfPCell(new Phrase("운영위치", fontVo.getContentFont()));
                    PdfPCell pickLocation = new PdfPCell(
                            new Phrase(arrivalProductVo.getWh_pick_location(), fontVo.getDateFont()));

                    PdfPCell inDateHeader = new PdfPCell(new Phrase("입하일자", fontVo.getContentFont()));
                    PdfPCell inDate = new PdfPCell(
                            new Phrase(arrivalProductVo.getWh_arrival_date(), fontVo.getDateFont()));

                    PdfPCell orderDateHeader = new PdfPCell(new Phrase("발주일자", fontVo.getContentFont()));
                    PdfPCell orderDate = new PdfPCell(
                            new Phrase(arrivalProductVo.getOrder_date(), fontVo.getDateFont()));

                    PdfPCell emptyCell = new PdfPCell(new Phrase("", fontVo.getContentFont()));

                    for (PdfPCell cell : Arrays.asList(productNameHeader, product, qtyHeader, qtyInHeader, qtyInUnit,
                            qtyIn, barcode, barcodeHumanText, expDateHeader, expDate, pickLocationHeader, pickLocation,
                            inDateHeader, inDate, orderDateHeader, orderDate, emptyCell)) {
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }

                    // 그리고 테이블에 위에서 생성시킨 셀을 넣는다.
                    product.setColspan(2);

                    qtyHeader.setRowspan(2);
                    // qtyIn.setColspan();
                    // emptyCell.setColspan(2);

                    pickLocationHeader.setColspan(2);
                    orderDateHeader.setColspan(2);
                    expDateHeader.setColspan(2);
                    inDateHeader.setColspan(2);

                    barcode.setRowspan(2);
                    barcode.setColspan(3);
                    barcodeHumanText.setColspan(3);

                    table.addCell(productNameHeader);
                    table.addCell(product);
                    table.completeRow();

                    table.addCell(qtyHeader);
                    table.addCell(qtyInHeader);
                    table.addCell(qtyIn);
                    table.completeRow();

                    table.addCell(qtyInUnit);
                    table.addCell(emptyCell);
                    table.completeRow();

                    table.addCell(pickLocationHeader);
                    table.addCell(pickLocation);
                    table.completeRow();

                    table.addCell(orderDateHeader);
                    table.addCell(orderDate);
                    table.completeRow();

                    table.addCell(inDateHeader);
                    table.addCell(inDate);
                    table.completeRow();

                    table.addCell(expDateHeader);
                    table.addCell(expDate);
                    table.completeRow();

                    table.addCell(barcode);
                    table.completeRow();

                    table.addCell(barcodeHumanText);
                    table.completeRow();

                    // 출력시간을 작게나마 보여준다.
                    PdfPCell bottomCell = new PdfPCell(
                            new Phrase(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA).format(new Date())
                                    + " by " + requester, fontVo.getCommentFont()));
                    bottomCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    bottomCell.setColspan(5);
                    table.addCell(bottomCell);
                    table.completeRow();

                    table.setWidthPercentage(100);
                    table.setHorizontalAlignment(Element.ALIGN_CENTER);
                    document.add(table); // 웹접근 객체에 table를 저장한다.
                    // return document;
                    if (arrvivedProductLists.size() - 1 != arrvivedProductLists.lastIndexOf(arrivalProductVo)) {
                        document.newPage();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return null;
        } finally {
            document.close(); // 저장이 끝났으면 document객체를 닫는다.
        }

        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}