package pl.coderstrust.database;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.dao.NonTransientDataAccessException;
import pl.coderstrust.database.hibernate.HibernateInvoice;
import org.springframework.data.domain.Example;
import pl.coderstrust.database.hibernate.HibernateModelMapper;
import pl.coderstrust.database.hibernate.InvoiceRepository;
import pl.coderstrust.model.Invoice;

public class HibernateDatabase implements Database {
    private final InvoiceRepository invoiceRepository;
    private final HibernateModelMapper modelMapper;

    public HibernateDatabase(InvoiceRepository invoiceRepository, HibernateModelMapper modelMapper) {
        if (invoiceRepository == null) {
            throw new IllegalArgumentException("Database is empty.");
        }
        if (modelMapper == null) {
            throw new IllegalArgumentException("Mapper cannot be null.");
        }
        this.invoiceRepository = invoiceRepository;
        this.modelMapper = modelMapper;
    }

    //stwórz new HibernateInvoice
    //trzeba zrobić translację Invoice na HibernateInvoice i z powrotem

//    ModelMapper modelMapper = new ModelMapper();
//    OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            HibernateInvoice savedInvoice = invoiceRepository.save(modelMapper.mapToHibernateInvoice(invoice));
            return modelMapper.mapToInvoice(savedInvoice);
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during saving invoice.", e);
        }
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        if (!invoiceRepository.existsById(id)) {
            throw new DatabaseOperationException(String.format("There is no invoice with id: %s", id));
        }
        try {
            invoiceRepository.deleteById(id);
        } catch (NonTransientDataAccessException | NoSuchElementException e) {
            throw new DatabaseOperationException("An error occurred during deleting invoice.", e);
        }
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            Optional<HibernateInvoice> invoice = invoiceRepository.findById(id);
            if (invoice.isPresent()) {
                return Optional.of(modelMapper.mapToInvoice(invoice.get()));
            }
            return Optional.empty();
        } catch (NoSuchElementException e) {
            throw new DatabaseOperationException("An error occurred during getting invoice by id.", e);
        }
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null.");
        }
        try {
            Example<HibernateInvoice> example = Example.of(modelMapper.mapToHibernateInvoice(new Invoice.Builder().withNumber(number).build()));
            Optional<HibernateInvoice> invoice = invoiceRepository.findOne(example);
            if (invoice.isPresent()) {
                return Optional.of(modelMapper.mapToInvoice(invoice.get()));
            }
            return Optional.empty();
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during getting invoice by number.", e);
        }
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            return modelMapper.mapToInvoices(invoiceRepository.findAll());
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during getting all invoices.", e);
        }
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {
        try {
            invoiceRepository.deleteAll();
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during deleting all invoices.", e);
        }
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return invoiceRepository.existsById(id);
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during checking if invoice exists.", e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return invoiceRepository.count();
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during getting number of invoices.", e);
        }
    }
}
