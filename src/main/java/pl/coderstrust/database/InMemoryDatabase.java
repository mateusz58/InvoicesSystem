package pl.coderstrust.database;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import pl.coderstrust.model.Invoice;

public class InMemoryDatabase implements Database {

    private Map<Long, Invoice> database;
    private AtomicLong nextId = new AtomicLong(0);

    public InMemoryDatabase(Map<Long, Invoice> database) {
        if (database == null) {
            throw new IllegalArgumentException("Database is empty.");
        }
        this.database = database;
    }

    @Override
    public synchronized Invoice save(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        if (invoice.getId() == null || !database.containsKey(invoice.getId())) {
            return insertInvoice(invoice);
        }
        return updateInvoice(invoice);
    }

    private Invoice insertInvoice(Invoice invoice) {
        Long id = nextId.incrementAndGet();
        Invoice insertedInvoice = Invoice.builder()
            .withInvoice(invoice)
            .withId(id)
            .build();
        database.put(id, insertedInvoice);
        return insertedInvoice;
    }

    private Invoice updateInvoice(Invoice invoice) {
        Invoice updatedInvoice = Invoice.builder()
            .withInvoice(invoice)
            .build();
        database.put(invoice.getId(), updatedInvoice);
        return updatedInvoice;
    }

    @Override
    public synchronized void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        if (!database.containsKey(id)) {
            throw new DatabaseOperationException(String.format("No invoice with id: %s", id));
        }
        database.remove(id);
    }

    @Override
    public Optional<Invoice> getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public Optional<Invoice> getByNumber(String number) {
        if (number == null) {
            throw new IllegalArgumentException("Invoice number cannot be null");
        }
        return database.values()
            .stream()
            .filter(invoice -> invoice.getNumber().equals(number))
            .findFirst();
    }

    @Override
    public Collection<Invoice> getAll() {
        return database.values();
    }

    @Override
    public synchronized void deleteAll() {
        database.clear();
    }

    @Override
    public boolean exists(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        return database.containsKey(id);
    }

    @Override
    public long count() {
        return database.size();
    }
}
