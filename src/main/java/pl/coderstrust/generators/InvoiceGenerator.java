package pl.coderstrust.generators;

import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;

import java.util.ArrayList;
import java.util.List;

public class InvoiceGenerator {

    public static Invoice generateRandomInvoice() {
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(InvoiceEntryGenerator.getRandomEntry());
        }
        return Invoice.builder()
                .withId(IdGenerator.getId())
                .withNumber(Generator.generateRandomWord())
                .withBuyer(CompanyGenerator.generateRandomCompany())
                .withSeller(CompanyGenerator.generateRandomCompany())
                .withDueDate(Generator.generateRandomLocalDate())
                .withIssuedDate(Generator.generateRandomLocalDate())
                .withEntries(entries)
                .build();
    }

    public static Invoice generateRandomInvoicewithNullId() {
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(InvoiceEntryGenerator.getRandomEntry());
        }
        return Invoice.builder()
                .withNumber(Generator.generateRandomWord())
                .withBuyer(CompanyGenerator.generateRandomCompany())
                .withSeller(CompanyGenerator.generateRandomCompany())
                .withDueDate(Generator.generateRandomLocalDate())
                .withIssuedDate(Generator.generateRandomLocalDate())
                .withEntries(entries)
                .build();
    }
}
