package pl.coderstrust.database;

import org.springframework.dao.NonTransientDataAccessException;
import pl.coderstrust.database.hibernate.HibernateInvoice;
import pl.coderstrust.database.hibernate.InvoiceRepository;
import pl.coderstrust.model.Invoice;

import java.util.Collection;
import java.util.Optional;

public class HibernateDatabase implements Database {
    private final InvoiceRepository invoiceRepository;
    private final HibernateModelMapper modelMapper;

    public HibernateDatabase(InvoiceRepository invoiceRepository, HibernateModelMapper modelMapper) {
        this.invoiceRepository = invoiceRepository;
        this.modelMapper = modelMapper;
    }

    //stwórz new HibernateInvoice
    //trzeba zrobić translację Invoice na HibernateInvoice i z powrotem

//    ModelMapper modelMapper = new ModelMapper();
//    OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);

    @Override
    public Invoice save(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        HibernateInvoice savedInvoice = invoiceRepository.save(modelMapper.mapInvoice(invoice));
        return modelMapper.mapInvoice(savedInvoice);
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        if (!invoiceRepository.existsById(id)) {
            throw new DatabaseOperationException(String.format("There is no invoice with id: %s", id));
        }
        invoiceRepository.deleteById(id);
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        if (!invoiceRepository.existsById(id)) {
            throw new DatabaseOperationException(String.format("There is no invoice with id: %s", id));
        }
        return Optional.ofNullable(invoiceRepository.getOne(id));
    }

    @Override
    public Optional<Invoice> getByNumber(String number) {
        if (number == null) {
            throw new IllegalArgumentException("Invoice number cannot be null");
        }
        return invoiceRepository.findAll()
                .stream()
                .filter(invoice -> invoice.getNumber().equals(number))
                .findFirst();
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        List<Invoice> invoiceList;
        try {
            return invoiceRepository.findAll();
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during deleting all invoices.", e);
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
    public boolean exists(Long id) {
        return invoiceRepository.existsById(id);
    }

    @Override
    public long count() {
        return invoiceRepository.count();
    }
}
