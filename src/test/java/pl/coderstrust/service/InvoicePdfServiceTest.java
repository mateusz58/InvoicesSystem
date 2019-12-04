package pl.coderstrust.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

class InvoicePdfServiceTest {

    private InvoicePdfService invoicePdfService;

    @BeforeEach
    void setUp() {
        invoicePdfService = new InvoicePdfService();
    }

    @Test
    void shouldThrowExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> invoicePdfService.createPdf(null));
    }

    @Test
    void shouldCreatePdfFileInMemory() throws ServiceOperationException {
        //Given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();

        //When
        byte[] result = invoicePdfService.createPdf(invoice);

        //Then
        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}
