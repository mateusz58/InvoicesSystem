package pl.coderstrust.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    Database database;

    @InjectMocks
    InvoiceService invoiceService;

    @Test
    void addMethodShouldThrowExceptionForNullAsInvoice() {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.add(null));
    }

    @Test
    void addMethodShouldThrowExceptionIfInvoiceAlreadyExist() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(database).exists(invoice.getId());
        assertThrows(ServiceOperationException.class, () -> invoiceService.add(invoice));
        verify(database).exists(invoice.getId());
        verify(database, never()).save(invoice);
    }

    @Test
    void shouldAddInvoice() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        Invoice addedInvoice = InvoiceGenerator.generateRandomInvoice();
        when(database.exists(invoiceToAdd.getId())).thenReturn(false);
        when(database.save(invoiceToAdd)).thenReturn(addedInvoice);
        Invoice result = invoiceService.add(invoiceToAdd);
        assertEquals(addedInvoice, result);
        verify(database).save(invoiceToAdd);
        verify(database).exists(invoiceToAdd.getId());
    }

    @Test
    void shouldAddInvoiceWithNullId() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoiceWithNullId();
        Invoice addedInvoice = InvoiceGenerator.generateRandomInvoiceWithNullId();
        when(database.save(invoiceToAdd)).thenReturn(addedInvoice);
        Invoice result = invoiceService.add(invoiceToAdd);
        assertEquals(addedInvoice, result);
        verify(database).save(invoiceToAdd);
    }

    @Test
    void addMethodShouldThrowExceptionWhenAnErrorOccurDuringAddingInvoiceToDatabase() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        when(database.save(invoice)).thenThrow(DatabaseOperationException.class);
        assertThrows(ServiceOperationException.class, () -> invoiceService.add(invoice));
    }

    @Test
    void updateMethodShouldThrowExceptionForNullAsInvoice() {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.update(null));
    }

    @Test
    void updateInvoiceMethodShouldThrowExceptionWhenInvoiceNotExist() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(database).exists(invoice.getId());
        assertThrows(ServiceOperationException.class, () -> invoiceService.update(invoice));
        verify(database).exists(invoice.getId());
        verify(database, never()).save(invoice);
    }

    @Test
    void updateMethodShouldThrowExceptionForNullInvoiceId() {
        Invoice invoiceWithNullId = InvoiceGenerator.generateRandomInvoiceWithNullId();
        assertThrows(ServiceOperationException.class, () -> invoiceService.update(invoiceWithNullId));
    }

    @Test
    void updateMethodShouldUpdateInvoice() throws ServiceOperationException, DatabaseOperationException {
        Invoice invoiceToUpdate = InvoiceGenerator.generateRandomInvoice();
        Invoice invoiceUpdated = InvoiceGenerator.generateRandomInvoice();
        when(database.exists(invoiceToUpdate.getId())).thenReturn(true);
        when(database.save(invoiceToUpdate)).thenReturn(invoiceUpdated);
        Invoice result = invoiceService.update(invoiceToUpdate);
        assertEquals(invoiceUpdated, result);
        verify(database).save(invoiceToUpdate);
        verify(database).exists(invoiceToUpdate.getId());
    }

    @Test
    void updateMethodShouldThrowExceptionWhenAnErrorOccurDuringUpdatingInvoiceInDatabase() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        when(database.exists(invoice.getId())).thenReturn(true);
        when(database.save(invoice)).thenThrow(DatabaseOperationException.class);
        assertThrows(ServiceOperationException.class, () -> invoiceService.update(invoice));
        verify(database).exists(invoice.getId());
        verify(database).save(invoice);
    }

    @Test
    void deleteByIdMethodShouldThrowExceptionForNullAsId() {
        assertThrows(IllegalArgumentException.class, () -> new InvoiceService(database).deleteById(null));
    }

    @Test
    void deleteByIdMethodShouldThrowExceptionWhenAnErrorOccurDuringDeletingInvoiceByIdFromDatabase() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        when(database.exists(invoice.getId())).thenReturn(true);
        doThrow(DatabaseOperationException.class).when(database).delete(invoice.getId());
        assertThrows(ServiceOperationException.class, () -> invoiceService.deleteById(invoice.getId()));
        verify(database).delete(invoice.getId());
    }

    @Test
    void deleteByIdMethodShouldThrowExceptionWhenInvoiceDoesNotExist() throws DatabaseOperationException, ServiceOperationException {
        doReturn(false).when(database).exists(1L);
        assertThrows(ServiceOperationException.class, () -> invoiceService.deleteById(1L));
        verify(database).exists(1L);
        verify(database, never()).delete(1L);
    }

    @Test
    void shouldDeleteInvoiceById() throws ServiceOperationException, DatabaseOperationException {
        doReturn(true).when(database).exists(1L);
        invoiceService.deleteById(1L);
        verify(database).exists(1L);
        verify(database).delete(1L);
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullAsId() {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.getById(null));
    }

    @Test
    void shouldGetInvoiceById() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        doReturn(Optional.of(invoice)).when(database).getById(invoice.getId());

        Optional<Invoice> result = invoiceService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(invoice, result.get());
        verify(database).getById(1L);
    }

    @Test
    void getByIdMethodShouldThrowExceptionWhenAnErrorOccurDuringGettingInvoiceByIdFromDatabase() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();

        when(database.getById(invoice.getId())).thenThrow(DatabaseOperationException.class);

        assertThrows(ServiceOperationException.class, () -> invoiceService.getById(invoice.getId()));
        verify(database).getById(invoice.getId());
    }

    @Test
    void getByNumberMethodShouldThrowExceptionForNullAsNumber() {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.getByNumber(null));
    }

    @Test
    void shouldGetInvoiceByNumber() throws DatabaseOperationException, ServiceOperationException {
        Optional<Invoice> expected = Optional.of(InvoiceGenerator.generateRandomInvoice());
        doReturn(expected).when(database).getByNumber("1");

        Optional<Invoice> actual = invoiceService.getByNumber("1");

        assertEquals(expected, actual);
        verify(database).getByNumber("1");
    }

    @Test
    void shouldReturnAllInvoices() throws ServiceOperationException, DatabaseOperationException {
        List<Invoice> expected = List.of(InvoiceGenerator.generateRandomInvoice(), InvoiceGenerator.generateRandomInvoice());
        doReturn(expected).when(database).getAll();

        Collection<Invoice> actual = invoiceService.getAll();

        assertEquals(expected, actual);
        verify(database).getAll();
    }

    @Test
    void getAllMethodShouldThrowExceptionWhenAnErrorOccurDuringGettingAllInvoicesFromDatabase() throws DatabaseOperationException {
        when(database.getAll()).thenThrow(DatabaseOperationException.class);
        InvoiceService invoiceService = new InvoiceService(database);

        assertThrows(ServiceOperationException.class, () -> invoiceService.getAll());
        verify(database).getAll();
    }

    @Test
    void shouldDeleteAllInvoices() throws ServiceOperationException, DatabaseOperationException {
        doNothing().when(database).deleteAll();

        invoiceService.deleteAll();
        verify(database).deleteAll();
    }

    @Test
    void deleteAllMethodShouldThrowExceptionWhenAnErrorOccurDuringDeletingAllInvoicesFromDatabase() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();

        doThrow(DatabaseOperationException.class).when(database).deleteAll();

        assertThrows(ServiceOperationException.class, () -> invoiceService.deleteAll());
        verify(database).deleteAll();
    }

    @Test
    void shouldCheckIfInvoiceExist() throws ServiceOperationException, DatabaseOperationException {
        invoiceService.exists(1L);

        verify(database).exists(1L);
    }

    @Test
    void shouldReturnTrueWhenInvoiceExistsInDatabase() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        when(database.exists(invoice.getId())).thenReturn(true);

        assertTrue(invoiceService.exists(invoice.getId()));
        verify(database).exists(invoice.getId());
    }

    @Test
    void shouldReturnFalseWhenInvoiceDoesNotExistsInDatabase() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        when(database.exists(invoice.getId())).thenReturn(false);

        assertFalse(invoiceService.exists(invoice.getId()));
        verify(database).exists(invoice.getId());
    }

    @Test
    void existsMethodShouldThrowExceptionForNullAsId() {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.exists(null));
    }

    @Test
    void existsMethodShouldThrowExceptionWhenAnErrorOccurDuringCheckingInvoiceExists() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        when(database.exists(invoice.getId())).thenThrow(DatabaseOperationException.class);

        assertThrows(ServiceOperationException.class, () -> invoiceService.exists(invoice.getId()));
        verify(database).exists(invoice.getId());
    }

    @Test
    void shouldReturnNumberOfInvoices() throws ServiceOperationException, DatabaseOperationException {
        doReturn(10L).when(database).count();
        long result = invoiceService.count();
        assertEquals(10L, result);
        verify(database).count();
    }

    @Test
    void countMethodShouldThrowExceptionWhenAnErrorOccurDuringGettingNumberOfInvoices() throws DatabaseOperationException {
        when(database.count()).thenThrow(DatabaseOperationException.class);
        assertThrows(ServiceOperationException.class, () -> invoiceService.count());
        verify(database).count();
    }
}
