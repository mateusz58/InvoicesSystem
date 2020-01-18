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
            .id(IdGenerator.getRandomId())
            .number(WordGenerator.generateRandomWord())
            .buyer(company)
            .seller(company)
            .dueDate(dateGenerated.plusDays(10L))
            .issuedDate(dateGenerated)
            .entries(generateEntries(5))
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
            .id(IdGenerator.getRandomId())
            .number(WordGenerator.generateRandomWord())
            .buyer(CompanyGenerator.generateRandomCompany())
            .seller(CompanyGenerator.generateRandomCompany())
            .dueDate(dateGenerated)
            .issuedDate(dateGenerated.plusDays(10L))
            .entries(generateEntries(5))
            .build();
    }

    public static Invoice getRandomInvoiceWithSpecificId(Long id) {
        return generateInvoice(id);
    }

    private static Invoice generateInvoice(Long id) {
        LocalDate dateGenerated = LocalDateGenerator.generateRandomLocalDate();
        return Invoice.builder()
            .id(id)
            .number(WordGenerator.generateRandomWord())
            .buyer(CompanyGenerator.generateRandomCompany())
            .seller(CompanyGenerator.generateRandomCompany())
            .dueDate(dateGenerated.plusDays(10L))
            .issuedDate(dateGenerated)
            .entries(generateEntries(5))
            .build();
    }

    public static Invoice generateRandomInvoice() {
        return generateInvoice(IdGenerator.getRandomId());
    }

    public static Invoice generateRandomInvoiceWithNullId() {
        return generateInvoice(null);
    }
}
