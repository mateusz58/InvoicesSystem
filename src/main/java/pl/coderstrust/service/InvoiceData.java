package pl.coderstrust.service;

import com.itextpdf.zugferd.profiles.BasicProfileImp;
import com.itextpdf.zugferd.profiles.IBasicProfile;
import com.itextpdf.zugferd.validation.basic.DateFormatCode;
import com.itextpdf.zugferd.validation.basic.DocumentTypeCode;
import com.itextpdf.zugferd.validation.basic.TaxIDTypeCode;
import com.itextpdf.zugferd.validation.basic.TaxTypeCode;
import java.math.BigDecimal;
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

    public IBasicProfile createBasicProfileData(Invoice invoice) {
        BasicProfileImp profileImp = new BasicProfileImp(true);
        importInvoiceSellerBuyer(profileImp, invoice);
        importItems(profileImp, invoice);
        return profileImp;
    }

//    public IComfortProfile createComfortProfileData(Invoice invoice) {
//        ComfortProfileImp profileImp = new ComfortProfileImp(true);
//        importInvoiceSellerBuyer(profileImp, invoice);
//        //importComfortData(profileImp, invoice);
//        return profileImp;
//    }

    public void importInvoiceSellerBuyer(BasicProfileImp profileImp, Invoice invoice) {
        profileImp.setTest(true);
        profileImp.setId(String.format("I/%05d", invoice.getId()));
        profileImp.setName("INVOICE");
        profileImp.setTypeCode(DocumentTypeCode.COMMERCIAL_INVOICE);
        profileImp.setDate(Date.valueOf(invoice.getIssuedDate()), DateFormatCode.YYYYMMDD);

        Company seller = invoice.getSeller();

        profileImp.setSellerName(seller.getName());
        profileImp.setSellerLineOne(invoice.getSeller().getAddress());
        profileImp.addSellerTaxRegistration(TaxIDTypeCode.FISCAL_NUMBER, seller.getTaxId());

        Company customer = invoice.getBuyer();

        profileImp.setBuyerName(String.format("%s ", customer.getName()));
        profileImp.setBuyerLineOne(customer.getAddress());
        profileImp.setPaymentReference(String.format("%09d", invoice.getId()));

        profileImp.setInvoiceCurrencyCode("PLN");
    }

    public void importItems(BasicProfileImp profileImp, Invoice invoice) {
        Map<Vat,BigDecimal> taxes = new TreeMap<Vat, BigDecimal>();
        Vat tax;
        for (InvoiceEntry item : invoice.getEntries()) {
            tax = item.getVatRate();
            if (taxes.containsKey(tax)) {
                taxes.put(tax, taxes.get(tax).add(item.getPrice()));
            }
            else {
                taxes.put(tax, item.getPrice());
            }
            profileImp.addIncludedSupplyChainTradeLineItem(item.getQuantity().toString(), "C62", item.getDescription());
        }
        BigDecimal total, tA;
        int ltN = 0;
        int ttA = 0;
        int gtA = 0;
        for (Map.Entry<Vat, BigDecimal> t : taxes.entrySet()) {
//            tax = t.getKey();
//            total = t.getValue();
//            gtA += total;
//            tA = (100 * total) / (100 + tax);
//            ttA += (total - tA);
//            ltN += tA;
        profileImp.addApplicableTradeTax("110", "PLN", TaxTypeCode.VALUE_ADDED_TAX, "asdasd", "PLN", t.getKey().toString());
        }
    }

}
