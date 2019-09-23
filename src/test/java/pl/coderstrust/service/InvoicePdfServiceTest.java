package pl.coderstrust.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.coderstrust.generators.InvoiceGenerator;

class InvoicePdfServiceTest {

    InvoicePdfService invoicePdfService;

    private static final String PDFPATH = "src/test/resources/service/Invoice.pdf";

    @BeforeEach
    void setUp() {
        invoicePdfService=new InvoicePdfService();
    }
    @Test
    void shouldCreatePdf() throws Exception {
        InvoicePdfService.createPdf(InvoiceGenerator.generateRandomInvoice(), PDFPATH);
    }
}
