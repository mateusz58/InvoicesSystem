package pl.coderstrust.service;

import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.model.Invoice;

import java.util.Collection;
import java.util.Optional;

public class InvoiceService {

    private Database database;

    public InvoiceService(Database database) {
        this.database = database;
    }

    public Invoice add(Invoice invoice) throws ServiceOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        try {
            if (invoice.getId() != null && database.exists(invoice.getId())) {
                throw new ServiceOperationException("Invoice already exist in database");
            }
            return database.save(invoice);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during adding invoice", e);
        }
    }

    public Invoice update(Invoice invoice) throws ServiceOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        try {
            if (invoice.getId() == null || !database.exists(invoice.getId())) {
                throw new ServiceOperationException("Invoice does not exist in database");
            }
            return database.save(invoice);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during updating invoice", e);
        }
    }

    public void deleteById(Long id) throws ServiceOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        try {
            if (!database.exists(id)) {
                throw new ServiceOperationException("Invoice does not exist in database");
            }
            database.delete(id);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during deleting invoice", e);
        }
    }

    public Optional<Invoice> getById(Long id) throws ServiceOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        try {
            if (!database.exists(id)) {
                throw new ServiceOperationException("Invoice does not exist in database");
            }
            return database.getById(id);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during getting invoice by id", e);
        }
    }

    public Optional<Invoice> getByNumber(String number) throws ServiceOperationException {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null");
        }
        try {
            return database.getByNumber(number);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during getting invoice by number", e);
        }
    }

    public Collection<Invoice> getAll() throws ServiceOperationException {
        try {
            return database.getAll();
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during getting all invoices", e);
        }
    }

    public void deleteAll() throws ServiceOperationException {
        try {
            database.deleteAll();
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during deleting all invoices", e);
        }
    }

    public boolean exists(Long id) throws ServiceOperationException, DatabaseOperationException {
        if(id == null){
            throw new IllegalArgumentException("Id cannot be null");
        }
        try {
            return database.exists(id);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during checking if invoice exist", e);
        }
    }

    public long count() throws ServiceOperationException {
        try {
            return database.count();
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during counting invoices", e);
        }
    }
}
