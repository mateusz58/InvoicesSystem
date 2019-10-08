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
            log.error("Attempt to add null invoice to database");
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        try {
            if (invoice.getId() != null && database.exists(invoice.getId())) {
                log.error("Attempt to add existing invoice to database");
                throw new ServiceOperationException("Invoice already exist in database");
            }
            log.info("Invoice has been successfully added to database");
            return database.save(invoice);
        } catch (DatabaseOperationException e) {
            log.error("An error occurred during adding invoice", e);
            throw new ServiceOperationException("An error occurred during adding invoice", e);
        }
    }

    public Invoice update(Invoice invoice) throws ServiceOperationException {
        if (invoice == null) {
            log.error("Attempt to update null invoice to database");
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        try {
            if (invoice.getId() == null || !database.exists(invoice.getId())) {
                log.error("Attempt to update not existing invoice to database");
                throw new ServiceOperationException("Invoice does not exist in database");
            }
            log.info("Invoice has been successfully updated to database");
            return database.save(invoice);
        } catch (DatabaseOperationException e) {
            log.error("An error occured during updating Invoice to Database", e);
            throw new ServiceOperationException("An error occurred during updating invoice", e);
        }
    }

    public void deleteById(Long id) throws ServiceOperationException {
        if (id == null) {
            log.error("Attempt to delete null invoice");
            throw new IllegalArgumentException("Id cannot be null");
        }
        try {
            if (!database.exists(id)) {
                log.error("Attempt to delete not existing invoice to database");
                throw new ServiceOperationException("Invoice does not exist in database");
            }
            database.delete(id);
            log.info("Invoice has been successfully deleted");
        } catch (DatabaseOperationException e) {
            log.error("An error occurred during deleting invoice", e);
            throw new ServiceOperationException("An error occurred during deleting invoice", e);
        }
    }

    public Optional<Invoice> getById(Long id) throws ServiceOperationException {
        if (id == null) {
            log.error("Attempt to get invoice by null id");
            throw new IllegalArgumentException("Id cannot be null");
        }
        try {
            if (!database.exists(id)) {
                log.error("Attempt to get not existing invoice by Id");
                throw new ServiceOperationException("Invoice does not exist in database");
            }
            return database.getById(id);
        } catch (DatabaseOperationException e) {
            log.error("An error occurred during getting invoice by id", e);
            throw new ServiceOperationException("An error occurred during getting invoice by id", e);
        }
    }

    public Optional<Invoice> getByNumber(String number) throws ServiceOperationException {
        if (number == null) {
            log.error("Attempt to get invoice by null number");
            throw new IllegalArgumentException("Number cannot be null");
        }
        try {
            return database.getByNumber(number);
        } catch (DatabaseOperationException e) {
            log.error("An error occurred during getting invoice by number", e);
            throw new ServiceOperationException("An error occurred during getting invoice by number", e);
        }
    }

    public Collection<Invoice> getAll() throws ServiceOperationException {
        try {
            return database.getAll();
        } catch (DatabaseOperationException e) {
            log.error("An error occurred during getting all invoices");
            throw new ServiceOperationException("An error occurred during getting all invoices", e);
        }
    }

    public void deleteAll() throws ServiceOperationException {
        try {
            database.deleteAll();
            log.info("All invoices have been successfully deleted");
        } catch (DatabaseOperationException e) {
            log.error("An error occurred during deleting all invoices", e);
            throw new ServiceOperationException("An error occurred during deleting all invoices", e);
        }
    }

    public boolean exists(Long id) throws ServiceOperationException, DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to check if null invoice exists");
            throw new IllegalArgumentException("Id cannot be null");
        }
        try {
            return database.exists(id);
        } catch (DatabaseOperationException e) {
            log.error("An error occurred during checking if invoice exist", e);
            throw new ServiceOperationException("An error occurred during checking if invoice exist", e);
        }
    }

    public long count() throws ServiceOperationException {
        try {
            return database.count();
        } catch (DatabaseOperationException e) {
            log.error("An error occurred during counting invoices", e);
            throw new ServiceOperationException("An error occurred during counting invoices", e);
        }
    }
}
