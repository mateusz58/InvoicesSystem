package pl.coderstrust.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InvoicePdfServiceTest {

    InvoicePdfService invoicePdfService;

    private static final String PDFPATH = "src/test/resources/service/Invoice.pdf";

    @BeforeEach
    void setUp() {
        invoicePdfService=new InvoicePdfService();
    }
    @Test
    void shouldCreatePdf()
    {
        InvoicePdfService.createPDF(PDFPATH);
    }
}