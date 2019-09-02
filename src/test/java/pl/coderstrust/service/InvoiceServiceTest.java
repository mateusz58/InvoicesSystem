package pl.coderstrust.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {
    @Mock
    Database database;

    @InjectMocks
    InvoiceService invoiceService;

    @Test
    public void addMethodShouldThrowExceptionForIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new InvoiceService(database).add(null));
    }

    @Test
    public void addMethodShouldThrowExceptionIfInvoiceAlreadyExist() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(database).exists(invoice.getId());

        assertThrows(ServiceOperationException.class, () -> invoiceService.add(invoice));
    }

    @Test
    public void shouldAddInvoice() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        invoiceService.add(invoice);
        verify(database).save(invoice);
    }

    @Test
    public void updateMethodShouldThrowExceptionForIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new InvoiceService(database).update(null));
    }

    @Test
    public void updateMethodShouldThrowExceptionWhenInvoiceWasNotFound() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(database).exists(invoice.getId());
        assertThrows(ServiceOperationException.class, () -> invoiceService.update(invoice));
    }

    @Test
    public void updateMethodShouldUpdateInvoice() throws ServiceOperationException, DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(database).exists(invoice.getId());
        invoiceService.update(invoice);
        verify(database).save(invoice);
    }

    @Test
    public void deleteByIdMethodShouldThrowExceptionForIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new InvoiceService(database).deleteById(null));
    }

    @Test
    public void deleteByIdMethodShouldThrowExceptionWhenInvoiceWasNotFound() throws DatabaseOperationException, ServiceOperationException {
        doReturn(false).when(database).exists(1L);
        assertThrows(ServiceOperationException.class, () -> new InvoiceService(database).deleteById(1L));
    }

    @Test
    public void shouldDeleteInvoiceById() throws ServiceOperationException, DatabaseOperationException {
        doReturn(true).when(database).exists(1L);
        invoiceService.deleteById(1L);
        verify(database).delete(1L);
    }

    @Test
    public void getByIdMethodShouldThrowExceptionForIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new InvoiceService(database).getById(null));
    }

    @Test
    public void getByIdMethodShouldThrowExceptionWhenInvoiceWasNotFound() throws DatabaseOperationException {
        doReturn(false).when(database).exists(1L);
        assertThrows(ServiceOperationException.class, () -> new InvoiceService(database).getById(1L));
    }

    @Test
    public void shouldGetInvoiceById() throws DatabaseOperationException, ServiceOperationException {
        doReturn(true).when(database).exists(1L);
        invoiceService.getById(1L);
        verify(database).getById(1L);
    }

    @Test
    public void getByNumberMethodShouldThrowExceptionForIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new InvoiceService(database).getByNumber(null));
    }

    @Test
    public void shouldGetInvoiceByNumber() throws DatabaseOperationException, ServiceOperationException {
        Optional<Invoice> expected = Optional.of(InvoiceGenerator.generateRandomInvoice());
        doReturn(expected).when(database).getByNumber("1");
        Optional<Invoice> actual = invoiceService.getByNumber("1");

        assertEquals(expected, actual);
        verify(database).getByNumber("1");
    }

    @Test
    public void shouldGetAllInvoices() throws ServiceOperationException, DatabaseOperationException {
        List<Invoice> expected = List.of(InvoiceGenerator.generateRandomInvoice(), InvoiceGenerator.generateRandomInvoice());
        when(database.getAll()).thenReturn(expected);
        Collection<Invoice> actual = invoiceService.getAll();

        assertEquals(expected, actual);
        verify(database).getAll();
    }

    @Test
    public void shouldDeleteAllInvoices() throws ServiceOperationException, DatabaseOperationException {
        invoiceService.deleteAll();
        verify(database).deleteAll();
    }

    @Test
    public void shouldCheckIfInvoiceExist() throws ServiceOperationException, DatabaseOperationException {
        invoiceService.exists(1L);
        verify(database).exists(1L);
    }

    @Test
    public void shouldCountInvoices() throws ServiceOperationException, DatabaseOperationException {
        invoiceService.count();
        verify(database).count();
    }
}
