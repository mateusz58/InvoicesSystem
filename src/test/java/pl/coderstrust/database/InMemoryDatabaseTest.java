package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

class InMemoryDatabaseTest {

    private Map<Long, Invoice> storage;
    private InMemoryDatabase database;

    @BeforeEach
    void setup() {
        storage = new HashMap<>();
        database = new InMemoryDatabase(storage);
    }

    @Test
    void constructorClassShouldThrowExceptionForNullStorage() {
        assertThrows(IllegalArgumentException.class, () -> new InMemoryDatabase(null));
    }

    @Test
    void shouldAddInvoice() {

        Invoice addedInvoice = database.save(InvoiceGenerator.generateRandomInvoice());

        assertNotNull(addedInvoice.getId());
        assertEquals(1, (long) addedInvoice.getId());
        assertEquals(storage.get(addedInvoice.getId()), addedInvoice);

    }

    @Test
    void shouldUpdate() {
        Invoice invoiceInDatabase = InvoiceGenerator.generateRandomInvoice();
        Invoice invoiceToUpdate = invoiceInDatabase;
        storage.put(invoiceInDatabase.getId(), invoiceInDatabase);

        Invoice updatedInvoice = database.save(invoiceToUpdate);

        assertEquals(storage.get(invoiceInDatabase.getId()), updatedInvoice);
    }

    @Test
    void shouldDelete() throws DatabaseOperationException {

        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
        storage.put(invoice1.getId(), invoice1);
        storage.put(invoice2.getId(), invoice2);

        Map<Long, Invoice> expected = Map.of(invoice2.getId(), invoice2);

        database.delete(invoice1.getId());

        assertEquals(expected, storage);

    }

    @Test
    void deleteShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.delete(null));
    }

    @Test
    void deleteShouldThrowExceptionDuringDeletingNotExistingInvoice() {
        assertThrows(DatabaseOperationException.class, () -> database.delete(10L));
    }


    @Test
    void shouldGetById() {
        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
        storage.put(invoice1.getId(), invoice1);
        storage.put(invoice2.getId(), invoice2);

        Optional<Invoice> optionalInvoice = database.getById(invoice1.getId());

        assertTrue(optionalInvoice.isPresent());
        assertEquals(invoice1, optionalInvoice.get());
    }

    @Test
    void shouldGetByNumber() {

        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
        storage.put(invoice1.getId(), invoice1);
        storage.put(invoice2.getId(), invoice2);

        Optional<Invoice> optionalInvoice = database.getByNumber(invoice1.getNumber());

        assertTrue(optionalInvoice.isPresent());
        assertEquals(invoice1, optionalInvoice.get());
    }

    @Test
    void shouldReturnEmptyOptionalWhileGetNonExistingInvoiceById() {
        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
        storage.put(invoice1.getId(), invoice1);

        Optional<Invoice> optionalInvoice = database.getById(invoice2.getId());

        assertTrue(optionalInvoice.isEmpty());
    }

    @Test
    void shouldReturnEmptyOptionalWhileGetNonExistingInvoiceByNumber() {
        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
        storage.put(invoice1.getId(), invoice1);

        Optional<Invoice> optionalInvoice = database.getByNumber(invoice2.getNumber());

        assertTrue(optionalInvoice.isEmpty());
    }

    @Test
    void getByIdShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));
    }

    @Test
    void getByNumberShouldThrowExceptionForNullNumber() {
        assertThrows(IllegalArgumentException.class, () -> database.getByNumber(null));
    }

    @Test
    void shouldGetAll() {

        for (int i = 0; i < 10; i++) {
            Invoice invoice = InvoiceGenerator.generateRandomInvoice();
            storage.put(invoice.getId(), invoice);
        }

        assertEquals(storage.values(), database.getAll());

    }

    @Test
    void shouldDeleteAll() {

        for (int i = 0; i < 10; i++) {
            Invoice invoice = InvoiceGenerator.generateRandomInvoice();
            storage.put(invoice.getId(), invoice);
        }

        database.deleteAll();
        assertEquals(new HashMap<>(), storage);
    }

    @Test
    void shouldCheckIfExists() {

        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        storage.put(invoice.getId(), invoice);
        assertTrue(database.exists(invoice.getId()));
    }

    @Test
    void shouldCheckIfNotExists() {
        for (int i = 0; i < 10; i++) {
            Invoice invoice = InvoiceGenerator.generateRandomInvoice();
            storage.put(invoice.getId(), invoice);
        }

        assertFalse(database.exists(666L));
    }

    @Test
    void ShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.exists(null));
    }

    @Test
    void shouldCountInvoices() {

        for (int i = 0; i < 10; i++) {
            Invoice invoice = InvoiceGenerator.generateRandomInvoice();
            storage.put(invoice.getId(), invoice);
        }

        assertEquals(10, database.count());
    }
}
