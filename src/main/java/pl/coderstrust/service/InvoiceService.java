package pl.coderstrust.service;

import java.util.Collection;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.model.Invoice;

@Service
public class InvoiceService {

    private Database database;

    private static Logger log= LoggerFactory.getLogger(InvoiceService.class);

    @Autowired
    public InvoiceService(Database database) {
        this.database = database;
    }

    public Invoice add(Invoice invoice) throws ServiceOperationException {
        if (invoice == null) {
            log.error("Attempt to add null invoice to database.");
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            if (invoice.getId() != null && database.exists(invoice.getId())) {
                log.error("Attempt to add existing invoice to database.");
                throw new ServiceOperationException("Invoice already exist in database.");
            }
            log.info("Invoice has been successfully added to database.");
            return database.save(invoice);
        } catch (DatabaseOperationException e) {
            String message = "An error occurred during adding invoice.";
            log.error(message, e);
            throw new ServiceOperationException(message, e);
        }
    }

    public Invoice update(Invoice invoice) throws ServiceOperationException {
        if (invoice == null) {
            log.error("Attempt to update null invoice.");
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            if (invoice.getId() == null || !database.exists(invoice.getId())) {
                log.error("Attempt to update not existing invoice.");
                throw new ServiceOperationException("Invoice does not exist in database.");
            }
            log.debug("Invoice has been successfully updated.");
            return database.save(invoice);
        } catch (DatabaseOperationException e) {
            String message = "An error occurred during updating invoice.";
            log.error(message, e);
            throw new ServiceOperationException(message, e);
        }
    }

    public void deleteById(Long id) throws ServiceOperationException {
        if (id == null) {
            log.error("Attempt to delete invoice by null id.");
            throw new IllegalArgumentException("Id cannot be null");
        }
        try {
            if (!database.exists(id)) {
                log.error("Attempt to delete not existing invoice.");
                throw new ServiceOperationException("Invoice does not exist in database.");
            }
            database.delete(id);
            log.debug("Invoice with id {} has been deleted.", id);
        } catch (DatabaseOperationException e) {
            String message = "An error occurred during deleting invoice by id.";
            log.error(message, e);
            throw new ServiceOperationException(message, e);
        }
    }

    public Optional<Invoice> getById(Long id) throws ServiceOperationException {
        if (id == null) {
            log.error("Attempt to get invoice by null id.");
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            log.debug("Found invoice with id {} in database.", id);
            return database.getById(id);
        } catch (DatabaseOperationException e) {
            String message = "An error occurred during getting invoice by id.";
            log.error("An error occurred during getting invoice by id.", e);
            throw new ServiceOperationException(message, e);
        }
    }

    public Optional<Invoice> getByNumber(String number) throws ServiceOperationException {
        if (number == null) {
            log.error("Attempt to get invoice by null number.");
            throw new IllegalArgumentException("Number cannot be null.");
        }
        try {
            log.debug("Found invoice with number {} in database.", number);
            return database.getByNumber(number);
        } catch (DatabaseOperationException e) {
            String message = "An error occurred during getting invoice by number.";
            log.error(message, e);
            throw new ServiceOperationException(message, e);
        }
    }

    public Collection<Invoice> getAll() throws ServiceOperationException {
        try {
            return database.getAll();
        } catch (DatabaseOperationException e) {
            String message = "An error occurred during getting all invoices.";
            log.error(message, e);
            throw new ServiceOperationException(message, e);
        }
    }

    public void deleteAll() throws ServiceOperationException {
        try {
            database.deleteAll();
            log.debug("All invoices have been successfully deleted.");
        } catch (DatabaseOperationException e) {
            String message = "An error occurred during deleting all invoices.";
            log.error(message, e);
            throw new ServiceOperationException(message, e);
        }
    }

    public boolean exists(Long id) throws ServiceOperationException, DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to check if invoice exists by null id.");
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return database.exists(id);
        } catch (DatabaseOperationException e) {
            String message = "An error occurred during checking if invoice exists.";
            log.error(message, e);
            throw new ServiceOperationException(message, e);
        }
    }

    public long count() throws ServiceOperationException {
        try {
            return database.count();
        } catch (DatabaseOperationException e) {
            String message = "An error occurred during counting invoices.";
            log.error(message, e);
            throw new ServiceOperationException(message, e);
        }
    }
}
