
package pl.coderstrust.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.zugferd.exceptions.DataIncompleteException;
import com.itextpdf.zugferd.exceptions.InvalidCodeException;
import com.itextpdf.zugferd.profiles.IBasicProfile;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;

/**
 * Reads invoice data from a test database and creates ZUGFeRD invoices
 * (Basic profile).
 * @author Bruno Lowagie
 */
public class InvoicePdfService {


    /** The path to the color profile. */
    public static final String ICC = "resources/color/sRGB_CS_profile.icm";
//
//    /** The path to a regular font. */
//    public static final String REGULAR = "resources/fonts/OpenSans-Regular.ttf";
//
//    /** The path to a bold font. */
//    public static final String BOLD = "resources/fonts/OpenSans-Bold.ttf";

//    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
//        Font.BOLD);

    /** A <code>String</code> with a newline character. */
    public static final String NEWLINE = "\n";


    public static void createPdf(Invoice invoice,String filePath) throws ParserConfigurationException, SAXException, TransformerException, IOException, ParseException, DataIncompleteException, InvalidCodeException {

        String dest = String.format(filePath, invoice);

        PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);

        // Create the XML
        InvoiceData invoiceData=new InvoiceData();
        IBasicProfile basic = invoiceData.createBasicProfileData(invoice);
//        InvoiceDOM dom = new InvoiceDOM(basic);

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest));
         pdfDocument.setDefaultPageSize(PageSize.A4);

        // Create the document
        Document document = new Document(pdfDocument);
//        document.setFont(PdfFontFactory.createFont(REGULAR, true))
//            .setFontSize(12);
//        PdfFont bold = PdfFontFactory.createFont(BOLD, true);

        // Add the header
        document.add(
            new Paragraph()
                .	setTextAlignment(TextAlignment.RIGHT)
                .setMultipliedLeading(1)
                .add(new Text(String.format("%s %s\n", basic.getName(), basic.getId())))
                    .setFont(bold).setFontSize(14));
        // Add the seller and buyer address
        document.add(getAddressTable(basic, bold));

        document.add(new Paragraph(NEWLINE));
        document.add(new Paragraph(NEWLINE));
        document.add(getLineItemTable(invoice, bold));
       document.add(getTotalsTable(
            basic.getTaxBasisTotalAmount(), basic.getTaxTotalAmount(), basic.getGrandTotalAmount(), basic.getGrandTotalAmountCurrencyID(),
            basic.getTaxTypeCode(), basic.getTaxApplicablePercent(),
            basic.getTaxBasisAmount(), basic.getTaxCalculatedAmount(), basic.getTaxCalculatedAmountCurrencyID(), bold));
        // Add the payment info
        document.close();
    }

    private static String convertDate(Date d, String newFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(newFormat);
        return sdf.format(d);
    }

    private static Table getAddressTable(IBasicProfile basic, PdfFont bold) {
        Table table = new Table(new UnitValue[]{
            new UnitValue(UnitValue.PERCENT, 50),
            new UnitValue(UnitValue.PERCENT, 50)})
            .setWidth(UnitValue.createPercentValue(100));
        table.addCell(getPartyAddress("From:",
            basic.getSellerName(),
            basic.getSellerLineOne(),
            basic.getSellerLineTwo(),
            basic.getSellerCountryID(),
            basic.getSellerPostcode(),
            basic.getSellerCityName(),
            bold));
        table.addCell(getPartyAddress("To:",
            basic.getBuyerName(),
            basic.getBuyerLineOne(),
            basic.getBuyerLineTwo(),
            basic.getBuyerCountryID(),
            basic.getBuyerPostcode(),
            basic.getBuyerCityName(),
            bold));
        table.addCell(getPartyTax(basic.getSellerTaxRegistrationID(),
            basic.getSellerTaxRegistrationSchemeID(), bold));
        table.addCell(getPartyTax(basic.getBuyerTaxRegistrationID(),
            basic.getBuyerTaxRegistrationSchemeID(), bold));
        return table;
    }

    private static Cell getPartyAddress(String who, String name, String line1, String line2, String countryID, String postcode, String city, PdfFont bold) {
        Paragraph p = new Paragraph()
            .setMultipliedLeading(1.0f)
            .add(new Text(who).setFont(bold)).add(NEWLINE)
            .add(name).add(NEWLINE)
            .add(line1).add(NEWLINE)
//            .add(line2).add(NEWLINE)
            .add(String.format("%s-%s %s", countryID, postcode, city));
        Cell cell = new Cell()
            .setBorder(Border.NO_BORDER)
            .add(p);
        return cell;
    }

    private static Cell getPartyTax(String[] taxId, String[] taxSchema, PdfFont bold) {
        Paragraph p = new Paragraph()
            .setFontSize(10).setMultipliedLeading(1.0f)
            .add(new Text("Tax ID(s):").setFont(bold));
        if (taxId.length == 0) {
            p.add("\nNot applicable");
        }
        else {
            int n = taxId.length;
            for (int i = 0; i < n; i++) {
                p.add(NEWLINE)
                    .add(String.format("%s: %s", taxSchema[i], taxId[i]));
            }
        }
        return new Cell().setBorder(Border.NO_BORDER).add(p);
    }

    private static Table getLineItemTable(Invoice invoice, PdfFont bold) {
        Table table = new Table(new UnitValue[]{
            new UnitValue(UnitValue.PERCENT, 43.75f),
            new UnitValue(UnitValue.PERCENT, 12.5f),
            new UnitValue(UnitValue.PERCENT, 6.25f),
            new UnitValue(UnitValue.PERCENT, 12.5f),
            new UnitValue(UnitValue.PERCENT, 12.5f),
            new UnitValue(UnitValue.PERCENT, 12.5f)})
            .setWidth(UnitValue.createPercentValue(100))
            .setMarginTop(10).setMarginBottom(10);
        table.addHeaderCell(createCell("Item:", bold));
        table.addHeaderCell(createCell("Price:", bold));
        table.addHeaderCell(createCell("Qty:", bold));
        table.addHeaderCell(createCell("Subtotal:", bold));
        table.addHeaderCell(createCell("VAT:", bold));
        table.addHeaderCell(createCell("Total:", bold));
        InvoiceEntry product;
        for (InvoiceEntry item : invoice.getEntries()) {
            product = item;
            table.addCell(createCell(product.getDescription()));
            table.addCell(createCell(
                product.getPrice().toString())
                .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(createCell(String.valueOf(item.getQuantity()))
                .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(createCell(
               item.getPrice().toString())
                .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(createCell(
               product.getVatRate().toString())
                .setTextAlignment(TextAlignment.RIGHT));

            table.addCell(createCell(
                item.getPrice().toString()))
                .setTextAlignment(TextAlignment.RIGHT);
//            table.addCell(createCell(
//                    item.getPrice() + ((item.getPrice() * product.getVatRate()) / 100)))
//                .setTextAlignment(TextAlignment.RIGHT);
        }
        return table;
    }

    private static Cell createCell(String text) {
        return new Cell().setPadding(0.8f)
            .add(new Paragraph(text)
                .setMultipliedLeading(1));
    }

    private static Cell createCell(String text, PdfFont font) {
        return new Cell().setPadding(0.8f)
            .add(new Paragraph(text)
                .setFont(font).setMultipliedLeading(1));
    }

    private static Table getTotalsTable(String tBase, String tTax, String tTotal, String tCurrency,
                                String[] type, String[] percentage, String base[], String tax[], String currency[],
                                PdfFont bold) {
        Table table = new Table(new UnitValue[]{
            new UnitValue(UnitValue.PERCENT, 8.33f),
            new UnitValue(UnitValue.PERCENT, 8.33f),
            new UnitValue(UnitValue.PERCENT, 25f),
            new UnitValue(UnitValue.PERCENT, 25f),
            new UnitValue(UnitValue.PERCENT, 25f),
            new UnitValue(UnitValue.PERCENT, 8.34f)})
            .setWidth(UnitValue.createPercentValue(100));
        table.addCell(createCell("TAX:", bold));
        table.addCell(createCell("%", bold)
            .setTextAlignment(TextAlignment.RIGHT));
        table.addCell(createCell("Base amount:", bold));
        table.addCell(createCell("Tax amount:", bold));
        table.addCell(createCell("Total:", bold));
        table.addCell(createCell("Curr.:", bold));
        int n = type.length;
        for (int i = 0; i < n; i++) {
            table.addCell(createCell(type[i])
                .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(createCell(percentage[i])
                .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(createCell(base[i])
                .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(createCell(tax[i])
                .setTextAlignment(TextAlignment.RIGHT));
//            double total = Double.parseDouble(base[i]) + Double.parseDouble(tax[i]);
            table.addCell(createCell(
               String.valueOf(100))
                .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(createCell(currency[i]));
        }
        table.addCell(new Cell(1, 2).setBorder(Border.NO_BORDER));
        table.addCell(createCell(tBase, bold)
            .setTextAlignment(TextAlignment.RIGHT));
        table.addCell(createCell(tTax, bold)
            .setTextAlignment(TextAlignment.RIGHT));
        table.addCell(createCell(tTotal, bold)
            .setTextAlignment(TextAlignment.RIGHT));
        table.addCell(createCell(tCurrency, bold));
        return table;
    }

}