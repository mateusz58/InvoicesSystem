package pl.coderstrust.database;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.coderstrust.model.Invoice;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "in-memory")
public class InMemoryDatabase implements Database {

    private static Logger log = LoggerFactory.getLogger(InMemoryDatabase.class);
    private Map<Long, Invoice> database;
    private AtomicLong nextId = new AtomicLong(0);

    public InMemoryDatabase(Map<Long, Invoice> database) {
        if (database == null) {
            log.error("Database is empty.");
            throw new IllegalArgumentException("Database is empty.");
        }
        this.database = database;
    }

    @Override
    public synchronized Invoice save(Invoice invoice) {
        if (invoice == null) {
            log.error("Attempt to save null invoice to database.");
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        if (invoice.getId() == null || !database.containsKey(invoice.getId())) {
            log.debug("Invoice has been successfully added to database.");
            return insertInvoice(invoice);
        }
        log.debug("Invoice has been successfully updated.");
        return updateInvoice(invoice);
    }

    private Invoice insertInvoice(Invoice invoice) {
        Long id = nextId.incrementAndGet();
        Invoice insertedInvoice = Invoice.builder()
            .id(id)
            .number(invoice.getNumber())
            .dueDate(invoice.getDueDate())
            .issuedDate(invoice.getIssuedDate())
            .buyer(invoice.getBuyer())
            .seller(invoice.getSeller())
            .entries(invoice.getEntries())
            .build();
        database.put(id, insertedInvoice);
        return insertedInvoice;
    }

    private Invoice updateInvoice(Invoice invoice) {
        Invoice updatedInvoice = Invoice.builder()
            .id(invoice.getId())
            .number(invoice.getNumber())
            .dueDate(invoice.getDueDate())
            .issuedDate(invoice.getIssuedDate())
            .buyer(invoice.getBuyer())
            .seller(invoice.getSeller())
            .entries(invoice.getEntries())
            .build();
        database.put(invoice.getId(), updatedInvoice);
        return updatedInvoice;
    }

    @Override
    public synchronized void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to delete invoice by null id.");
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        if (!database.containsKey(id)) {
            log.error("Attempt to delete not existing invoice.");
            throw new DatabaseOperationException(String.format("There is no invoice with id: %s in database.", id));
        }
        database.remove(id);
        log.debug("Invoice has been successfully deleted.");
    }

    @Override
    public Optional<Invoice> getById(Long id) {
        if (id == null) {
            log.error("Attempt to get invoice by null id.");
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public Optional<Invoice> getByNumber(String number) {
        if (number == null) {
            log.error("Attempt to get invoice by null number.");
            throw new IllegalArgumentException("Invoice number cannot be null.");
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
        log.debug("All invoices have been successfully deleted.");
        database.clear();
    }

    @Override
    public boolean exists(Long id) {
        if (id == null) {
            log.error("Attempt to check if invoice exists by null id.");
            throw new IllegalArgumentException("Id cannot be null.");
        }
        return database.containsKey(id);
    }

    @Override
    public long count() {
        return database.size();
    }
}
