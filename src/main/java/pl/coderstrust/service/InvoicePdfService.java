package pl.coderstrust.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
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
import com.itextpdf.zugferd.profiles.BasicProfileImp;
import com.itextpdf.zugferd.profiles.IBasicProfile;
import com.itextpdf.zugferd.validation.basic.DateFormatCode;
import com.itextpdf.zugferd.validation.basic.DocumentTypeCode;
import com.itextpdf.zugferd.validation.basic.TaxIDTypeCode;
import com.itextpdf.zugferd.validation.basic.TaxTypeCode;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.stereotype.Service;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

@Service
public class InvoicePdfService {

    public  byte[] createPdf(Invoice invoice) throws ServiceOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            PdfDocument pdfDocument = new PdfDocument(new PdfWriter(byteStream));
            IBasicProfile invoiceProfile = createBasicProfileData(invoice);
            pdfDocument.setDefaultPageSize(PageSize.A4);
            try (Document document = new Document(pdfDocument)) {
                document.add(getHeaderInfo(invoiceProfile).add(new Text(getInvoicesIssueDueDates(invoice))));
                document.add(getAddressTable(invoiceProfile));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("\n"));
                document.add(getLineItemTable(invoice));
                document.add(getTotalsTable(invoiceProfile));
                document.add(getPaymentInfo(invoice));
                document.close();
                return byteStream.toByteArray();
            }
        } catch (IOException e) {
            throw new ServiceOperationException("An error occured during generating pdf", e);
        }
    }

    private static IBasicProfile createBasicProfileData(Invoice invoice) {
        BasicProfileImp profileImp = new BasicProfileImp(true);
        importSellerBuyerFromInvoice(profileImp, invoice);
        importItemsTotalPrizeValuesFromInvoice(profileImp, invoice);
        return profileImp;
    }

    private static Paragraph getHeaderInfo(IBasicProfile invoiceProfile) throws IOException {
        return new Paragraph()
            .setTextAlignment(TextAlignment.RIGHT)
            .setMultipliedLeading(1)
            .add(new Text(String.format("%s %s\n", invoiceProfile.getName(), invoiceProfile.getId())))
            .setFont(bold());
    }

    private static String getInvoicesIssueDueDates(Invoice invoice) {
        String issuedDate = invoice.getIssuedDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String dueDate = invoice.getIssuedDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        return String.format("Issued date: %s\n Due date: %s\n", issuedDate, dueDate);
    }

    private static Table getAddressTable(IBasicProfile basic) throws IOException {
        Table table = new Table(new UnitValue[] {
            new UnitValue(UnitValue.PERCENT, 50),
            new UnitValue(UnitValue.PERCENT, 50)})
            .setWidth(UnitValue.createPercentValue(100));
        table.addCell(getPartyAddress("From:",
            basic.getSellerName(),
            basic.getSellerLineOne()));
        table.addCell(getPartyAddress("To:",
            basic.getBuyerName(),
            basic.getBuyerLineOne()));
        table.addCell(getPartyTax(basic.getSellerTaxRegistrationID(),
            basic.getSellerTaxRegistrationSchemeID()));
        table.addCell(getPartyTax(basic.getBuyerTaxRegistrationID(),
            basic.getBuyerTaxRegistrationSchemeID()));
        return table;
    }

    private static Table getLineItemTable(Invoice invoice) throws IOException {
        Table table = new Table(new UnitValue[] {
            new UnitValue(UnitValue.PERCENT, 43.75f),
            new UnitValue(UnitValue.PERCENT, 12.5f),
            new UnitValue(UnitValue.PERCENT, 6.25f),
            new UnitValue(UnitValue.PERCENT, 12.5f),
            new UnitValue(UnitValue.PERCENT, 12.5f),
            new UnitValue(UnitValue.PERCENT, 12.5f)})
            .setWidth(UnitValue.createPercentValue(100))
            .setMarginTop(10).setMarginBottom(10);
        table.addHeaderCell(createCell("Item:", bold()));
        table.addHeaderCell(createCell("Price:", bold()));
        table.addHeaderCell(createCell("Qty:", bold()));
        table.addHeaderCell(createCell("Subtotal:", bold()));
        table.addHeaderCell(createCell("VAT:", bold()));
        table.addHeaderCell(createCell("Total:", bold()));
        InvoiceEntry product;
        for (InvoiceEntry item : invoice.getEntries()) {
            product = item;
            table.addCell(createCell(product.getDescription()));
            table.addCell(createCell(
                product.getPrice().setScale(2).toString())
                .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(createCell(String.valueOf(item.getQuantity()))
                .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(createCell(
                item.getNetValue().setScale(2).toString())
                .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(createCell(
                (product.getVatRate().getValue() * 100) + "%")
                .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(createCell(
                item.getGrossValue().setScale(2).toString()))
                .setTextAlignment(TextAlignment.RIGHT);
        }
        return table;
    }

    private static Table getTotalsTable(IBasicProfile basic) throws IOException {
        Table table = new Table(new UnitValue[] {
            new UnitValue(UnitValue.PERCENT, 8.33f),
            new UnitValue(UnitValue.PERCENT, 8.33f),
            new UnitValue(UnitValue.PERCENT, 25f),
            new UnitValue(UnitValue.PERCENT, 25f),
            new UnitValue(UnitValue.PERCENT, 25f),
            new UnitValue(UnitValue.PERCENT, 8.34f)})
            .setWidth(UnitValue.createPercentValue(100));
        table.addCell(createCell("TAX:", bold()));
        table.addCell(createCell("%", bold())
            .setTextAlignment(TextAlignment.RIGHT));
        table.addCell(createCell("Base amount:", bold()));
        table.addCell(createCell("Tax amount:", bold()));
        table.addCell(createCell("Total:", bold()));
        table.addCell(createCell("Curr.:", bold()));
        int n = basic.getTaxTypeCode().length;
        double total = 0;
        for (int i = 0; i < n; i++) {
            table.addCell(createCell(basic.getTaxTypeCode()[i])
                .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(createCell(basic.getTaxApplicablePercent()[i])
                .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(createCell(basic.getTaxBasisAmount()[i])
                .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(createCell(basic.getTaxCalculatedAmount()[i])
                .setTextAlignment(TextAlignment.RIGHT));
            total = Double.parseDouble(basic.getTaxBasisAmount()[i]) + Double.parseDouble(basic.getTaxCalculatedAmount()[i]);
            table.addCell(createCell(
                String.valueOf(BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_EVEN)))
                .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(createCell(basic.getTaxCalculatedAmountCurrencyID()[i]));
        }
        table.addCell(new Cell(1, 2).setBorder(Border.NO_BORDER));
        table.addCell(createCell(basic.getTaxBasisTotalAmount(), bold())
            .setTextAlignment(TextAlignment.RIGHT));
        table.addCell(createCell(basic.getTaxTotalAmount(), bold())
            .setTextAlignment(TextAlignment.RIGHT));
        table.addCell(createCell(basic.getGrandTotalAmount(), bold())
            .setTextAlignment(TextAlignment.RIGHT));
        table.addCell(createCell(basic.getGrandTotalAmountCurrencyID(), bold()));
        return table;
    }

    private static Paragraph getPaymentInfo(Invoice invoice) {
        Paragraph p = new Paragraph("Bank account numbers :");
        p.add("\n").add(String.format("Seller account number %s: ", invoice.getSeller().getAccountNumber()));
        p.add("\n").add(String.format("Buyer account number %s: ", invoice.getBuyer().getAccountNumber()));
        return p;
    }

    private static void importSellerBuyerFromInvoice(BasicProfileImp profileImp, Invoice invoice) {
        profileImp.setId(String.format("I/%s", invoice.getNumber()));
        profileImp.setName("INVOICE");
        profileImp.setTypeCode(DocumentTypeCode.COMMERCIAL_INVOICE);
        profileImp.setDate(Date.valueOf(invoice.getIssuedDate()), DateFormatCode.YYYYMMDD);
        profileImp.setPaymentReference(String.format("%09d", invoice.getId()));
        Company seller = invoice.getSeller();
        profileImp.setSellerName(seller.getName());
        profileImp.setSellerLineOne(seller.getAddress());
        profileImp.addSellerTaxRegistration(TaxIDTypeCode.FISCAL_NUMBER, seller.getTaxId());
        Company customer = invoice.getBuyer();
        profileImp.setBuyerName(customer.getName());
        profileImp.setBuyerLineOne(customer.getAddress());
        profileImp.addBuyerTaxRegistration(TaxIDTypeCode.FISCAL_NUMBER, customer.getTaxId());
        profileImp.setInvoiceCurrencyCode("PLN");
    }

    private static void importItemsTotalPrizeValuesFromInvoice(BasicProfileImp profileImp, Invoice invoice) {
        Map<Vat, Double[]> taxes = new TreeMap<>();
        Vat tax;
        for (InvoiceEntry item : invoice.getEntries()) {
            tax = item.getVatRate();
            if (taxes.containsKey(tax)) {
                taxes.put(tax, add(taxes.get(tax), new Double[] {item.getGrossValue().doubleValue(), item.getNetValue().doubleValue()}));
            } else {
                taxes.put(tax, new Double[] {item.getGrossValue().doubleValue(), item.getNetValue().doubleValue()});
            }
            profileImp.addIncludedSupplyChainTradeLineItem(item.getQuantity().toString(), "C62", item.getDescription());
        }
        double grandTotal = 0;
        double baseTotal = 0;
        double taxTotal = 0;
        for (Map.Entry<Vat, Double[]> t : taxes.entrySet()) {
            tax = t.getKey();
            taxTotal = taxTotal + (t.getValue()[0] - t.getValue()[1]);
            baseTotal = baseTotal + t.getValue()[1].doubleValue();
            grandTotal = grandTotal + t.getValue()[0].doubleValue();
            profileImp.addApplicableTradeTax(String.valueOf(BigDecimal.valueOf(t.getValue()[0] - t.getValue()[1]).setScale(2, RoundingMode.HALF_EVEN)), "PLN", TaxTypeCode.VALUE_ADDED_TAX,
                String.valueOf(BigDecimal.valueOf(t.getValue()[1]).setScale(2, RoundingMode.HALF_EVEN)), "PLN", String.format("%s%%", String.valueOf(tax.getValue() * 100)));
        }
        profileImp.setMonetarySummation(String.valueOf(0), "PLN",
            String.valueOf(0), "PLN",
            String.valueOf(0), "PLN",
            String.valueOf(BigDecimal.valueOf(baseTotal).setScale(2, RoundingMode.HALF_EVEN)), "PLN",
            String.valueOf(BigDecimal.valueOf(taxTotal).setScale(2, RoundingMode.HALF_EVEN)), "PLN",
            String.valueOf(BigDecimal.valueOf(grandTotal).setScale(2, RoundingMode.HALF_EVEN)), "PLN");
    }

    private static PdfFont bold() throws IOException {
        return PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
    }

    private static Cell getPartyAddress(String who, String name, String address) throws IOException {
        Paragraph p = new Paragraph()
            .setMultipliedLeading(1.0f)
            .add(new Text(who).setFont(bold())).add("\n")
            .add(name).add("\n")
            .add(address).add("\n");
        Cell cell = new Cell()
            .setBorder(Border.NO_BORDER)
            .add(p);
        return cell;
    }

    private static Cell getPartyTax(String[] taxId, String[] taxSchema) throws IOException {
        Paragraph p = new Paragraph()
            .setFontSize(10).setMultipliedLeading(1.0f)
            .add(new Text("Tax ID(s):").setFont(bold()));
        if (taxId.length == 0) {
            p.add("\nNot applicable");
        } else {
            int n = taxId.length;
            for (int i = 0; i < n; i++) {
                p.add("\n")
                    .add(String.format("%s: %s", taxSchema[i], taxId[i]));
            }
        }
        return new Cell().setBorder(Border.NO_BORDER).add(p);
    }

    private static Cell createCell(String text, PdfFont font) {
        return new Cell().setPadding(0.8f)
            .add(new Paragraph(text)
                .setFont(font).setMultipliedLeading(1));
    }

    private static Cell createCell(String text) {
        return new Cell().setPadding(0.8f)
            .add(new Paragraph(text)
                .setMultipliedLeading(1));
    }

    private static Double[] add(Double[] first, Double[] second) {
        int length = first.length < second.length ? first.length
            : second.length;
        Double[] result = new Double[length];
        for (int i = 0; i < length; i++) {
            result[i] = first[i] + second[i];
        }
        return result;
    }
}
