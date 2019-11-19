package pl.coderstrust.database;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import pl.coderstrust.database.hibernate.HibernateInvoice;
import pl.coderstrust.database.hibernate.HibernateModelMapper;
import pl.coderstrust.model.Invoice;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "hibernate")
public class HibernateDatabase implements Database {
    private final InvoiceRepository invoiceRepository;
    private final HibernateModelMapper modelMapper;

    private static Logger log = LoggerFactory.getLogger(HibernateDatabase.class);

    public HibernateDatabase(InvoiceRepository invoiceRepository, HibernateModelMapper modelMapper) {
        if (invoiceRepository == null) {
            log.error("Database is empty.");
            throw new IllegalArgumentException("Database is empty.");
        }
        if (modelMapper == null) {
            log.error("Attempt to initialize database with null mapper.");
            throw new IllegalArgumentException("Mapper cannot be null.");
        }
        this.invoiceRepository = invoiceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            log.error("Attempt to add null invoice to database.");
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            HibernateInvoice savedInvoice = invoiceRepository.save(modelMapper.mapToHibernateInvoice(invoice));
            log.debug("Invoice has been successfully added to database.");
            return modelMapper.mapToInvoice(savedInvoice);
        } catch (NonTransientDataAccessException e) {
            log.error("An error occurred during saving invoice", e);
            throw new DatabaseOperationException("An error occurred during saving invoice.", e);
        }
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to delete invoice by null id.");
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        if (!invoiceRepository.existsById(id)) {
            log.error("Attempt to delete not existing invoice.");
            throw new DatabaseOperationException(String.format("There is no invoice with id: %s", id));
        }
        try {
            invoiceRepository.deleteById(id);
            log.debug("Invoice with id {} has been successfully deleted.", id);
        } catch (NonTransientDataAccessException | NoSuchElementException e) {
            log.error("An error occurred during deleting invoice.",e);
            throw new DatabaseOperationException("An error occurred during deleting invoice.", e);
        }
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to get invoice by null id.");
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            Optional<HibernateInvoice> invoice = invoiceRepository.findById(id);
            if (invoice.isPresent()) {
                log.debug("Found invoice with id {}.", id);
                return Optional.of(modelMapper.mapToInvoice(invoice.get()));
            }
            log.debug("Invoice with id {} is not found.", id);
            return Optional.empty();
        } catch (NoSuchElementException e) {
            log.error("An error occurred during getting invoice by id.",e);
            throw new DatabaseOperationException("An error occurred during getting invoice by id.", e);
        }
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            log.error("Attempt to get invoice by null number.");
            throw new IllegalArgumentException("Number cannot be null.");
        }
        try {
            Example<HibernateInvoice> example = Example.of(modelMapper.mapToHibernateInvoice(new Invoice.Builder().withNumber(number).build()));
            Optional<HibernateInvoice> invoice = invoiceRepository.findOne(example);
            if (invoice.isPresent()) {
                log.debug("Found invoice with number {}.", number);
                return Optional.of(modelMapper.mapToInvoice(invoice.get()));
            }
            log.debug("Invoice with number {} is not found.", number);
            return Optional.empty();
        } catch (NonTransientDataAccessException e) {
            log.error("An error occurred during getting invoice by number.",e);
            throw new DatabaseOperationException("An error occurred during getting invoice by number.", e);
        }
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            log.debug("Successfully downloaded all invoices.");
            return modelMapper.mapToInvoices(invoiceRepository.findAll());
        } catch (NonTransientDataAccessException e) {
            log.error("An error occurred during getting all invoices.");
            throw new DatabaseOperationException("An error occurred during getting all invoices.", e);
        }
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {
        try {
            invoiceRepository.deleteAll();
            log.debug("All invoices have been successfully deleted.");
        } catch (NonTransientDataAccessException e) {
            log.error("An error occurred during deleting all invoices.",e);
            throw new DatabaseOperationException("An error occurred during deleting all invoices.", e);
        }
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to check invoice by null id.");
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return invoiceRepository.existsById(id);
        } catch (NonTransientDataAccessException e) {
            log.error("An error occurred during checking if invoice exist.",e);
            throw new DatabaseOperationException("An error occurred during checking if invoice exists.", e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return invoiceRepository.count();
        } catch (NonTransientDataAccessException e) {
            log.error("An error occurred during counting invoices.",e);
            throw new DatabaseOperationException("An error occurred during getting number of invoices.", e);
        }
    }
}
