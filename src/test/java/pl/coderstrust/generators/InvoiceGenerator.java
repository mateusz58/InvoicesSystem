package pl.coderstrust.generators;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;

public class InvoiceGenerator {

    public static Invoice generateInvoiceWithTheSameBuyerAndSeller() {
        LocalDate dateGenerated = LocalDateGenerator.generateRandomLocalDate();
        Company company = CompanyGenerator.generateRandomCompanyWithSpecificId(5L);
        return Invoice.builder()
            .withId(IdGenerator.getRandomId())
            .withNumber(WordGenerator.generateRandomWord())
            .withBuyer(company)
            .withSeller(company)
            .withDueDate(dateGenerated.plusDays(10L))
            .withIssuedDate(dateGenerated)
            .withEntries(generateEntries(5))
            .build();
    }

    private static List<InvoiceEntry> generateEntries(long count) {
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entries.add(InvoiceEntryGenerator.getRandomEntry());
        }
        return entries;
    }

    public static Invoice generateRandomInvoiceWithGreaterIssuedDate() {
        LocalDate dateGenerated = LocalDateGenerator.generateRandomLocalDate();
        return Invoice.builder()
            .withId(IdGenerator.getRandomId())
            .withNumber(WordGenerator.generateRandomWord())
            .withBuyer(CompanyGenerator.generateRandomCompany())
            .withSeller(CompanyGenerator.generateRandomCompany())
            .withDueDate(dateGenerated)
            .withIssuedDate(dateGenerated.plusDays(10L))
            .withEntries(generateEntries(5))
            .build();
    }

    public static Invoice getRandomInvoiceWithSpecificId(Long id) {
        return generateInvoice(id);
    }

    private static Invoice generateInvoice(Long id) {
        LocalDate dateGenerated = LocalDateGenerator.generateRandomLocalDate();
        return Invoice.builder()
            .withId(id)
            .withNumber(WordGenerator.generateRandomWord())
            .withBuyer(CompanyGenerator.generateRandomCompany())
            .withSeller(CompanyGenerator.generateRandomCompany())
            .withDueDate(dateGenerated.plusDays(10L))
            .withIssuedDate(dateGenerated)
            .withEntries(generateEntries(5))
            .build();
    }

    public static Invoice generateRandomInvoice() {
        return generateInvoice(IdGenerator.getRandomId());
    }

    public static Invoice generateRandomInvoiceWithNullId() {
        return generateInvoice(null);
    }
}
