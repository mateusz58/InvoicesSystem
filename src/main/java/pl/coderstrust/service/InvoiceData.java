package pl.coderstrust.service;

import com.itextpdf.zugferd.profiles.BasicProfileImp;
import com.itextpdf.zugferd.profiles.IBasicProfile;
import com.itextpdf.zugferd.validation.basic.DateFormatCode;
import com.itextpdf.zugferd.validation.basic.DocumentTypeCode;
import com.itextpdf.zugferd.validation.basic.TaxIDTypeCode;
import com.itextpdf.zugferd.validation.basic.TaxTypeCode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.Map;
import java.util.TreeMap;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;


public class InvoiceData {

    public InvoiceData() {
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

    public IBasicProfile createBasicProfileData(Invoice invoice) {
        BasicProfileImp profileImp = new BasicProfileImp(true);
        importInvoiceSellerBuyer(profileImp, invoice);
        importItemsTotalPrizeValues(profileImp, invoice);
        return profileImp;
    }

    public void importInvoiceSellerBuyer(BasicProfileImp profileImp, Invoice invoice) {
        profileImp.setId(String.format("I/%s", invoice.getNumber()));
        profileImp.setName("INVOICE");
        profileImp.setTypeCode(DocumentTypeCode.COMMERCIAL_INVOICE);
        profileImp.setDate(Date.valueOf(invoice.getIssuedDate()), DateFormatCode.YYYYMMDD);
        profileImp.setPaymentReference(String.format("%09d", invoice.getId()));

        Company seller = invoice.getSeller();

        profileImp.setSellerName(seller.getName());
        profileImp.setSellerLineOne(invoice.getSeller().getAddress());
        profileImp.addSellerTaxRegistration(TaxIDTypeCode.FISCAL_NUMBER, seller.getTaxId());

        Company customer = invoice.getBuyer();

        profileImp.setBuyerName(customer.getName());
        profileImp.setBuyerLineOne(customer.getAddress());
        profileImp.addBuyerTaxRegistration(TaxIDTypeCode.FISCAL_NUMBER, customer.getTaxId());

        profileImp.setInvoiceCurrencyCode("PLN");
    }

//    public void importInvoicePaymentMeans(BasicProfileImp profileImp, Invoice invoice){
//
//        profileImp.addPaymentMeans("", "", invoice.getSeller().getAccountNumber(), "", "", "", "", "");
//        profileImp.addPaymentMeans("", "", invoice.getBuyer().getAccountNumber(), "", "", "", "", "");
//    }

    public void importItemsTotalPrizeValues(BasicProfileImp profileImp, Invoice invoice) {
        Map<Vat, Double[]> taxes = new TreeMap<>();
        Vat tax;
        for (InvoiceEntry item : invoice.getEntries()) {
            tax = item.getVatRate();
            if (taxes.containsKey(tax)) {
                taxes.put(tax, add(taxes.get(tax),new Double[]{item.getGrossValue().doubleValue(),item.getNetValue().doubleValue()}));
            } else {
                taxes.put(tax, new Double[]{item.getGrossValue().doubleValue(),item.getNetValue().doubleValue()});
            }
            profileImp.addIncludedSupplyChainTradeLineItem(item.getQuantity().toString(), "C62", item.getDescription());
        }

        double grandTotal=0;
        double baseTotal=0;
        double taxTotal=0;
        for (Map.Entry<Vat, Double[]> t : taxes.entrySet()) {
            tax = t.getKey();
            taxTotal=taxTotal+(t.getValue()[0] -t.getValue()[1]);
            baseTotal=baseTotal+t.getValue()[1].doubleValue();
            grandTotal=grandTotal+t.getValue()[0].doubleValue();
            profileImp.addApplicableTradeTax( String.valueOf(BigDecimal.valueOf(t.getValue()[0]-t.getValue()[1]).setScale(2,RoundingMode.HALF_EVEN)), "PLN", TaxTypeCode.VALUE_ADDED_TAX, String.valueOf(BigDecimal.valueOf(t.getValue()[1]).setScale(2,RoundingMode.HALF_EVEN)), "PLN", String.format("%s%%",String.valueOf(tax.getValue()*100)));
        }
        profileImp.setMonetarySummation(String.valueOf(0), "PLN",
            String.valueOf(0), "PLN",
            String.valueOf(0), "PLN",
            String.valueOf(BigDecimal.valueOf(baseTotal).setScale(2, RoundingMode.HALF_EVEN)), "PLN", /// suma wszystkiego netto
            String.valueOf(BigDecimal.valueOf(taxTotal).setScale(2,RoundingMode.HALF_EVEN)), "PLN",  ///suma calego podatku
            String.valueOf(BigDecimal.valueOf(grandTotal).setScale(2,RoundingMode.HALF_EVEN)), "PLN"); ///suma wszystkiego

    }
}
