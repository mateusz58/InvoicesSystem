package pl.coderstrust.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import pl.coderstrust.configuration.InFileDatabaseProperties;
import pl.coderstrust.helpers.FileHelper;
import pl.coderstrust.model.Invoice;

public class InFileDatabase implements Database {

    private String filePath;
    private ObjectMapper mapper;
    private FileHelper fileHelper;
    private AtomicLong nextId;

    @Autowired
    public InFileDatabase(InFileDatabaseProperties inFileDatabaseProperties, ObjectMapper mapper, FileHelper fileHelper) throws IOException {
        this.filePath = inFileDatabaseProperties.getFilePath();
        this.mapper = mapper;
        this.fileHelper = fileHelper;
        initFile();
    }

    private void initFile() throws IOException {
        if (!fileHelper.exists(filePath)) {
            fileHelper.createFile(filePath);
        }
        nextId = new AtomicLong(getLastInvoiceId());
    }

    private Invoice deserializeJsonToInvoice(String json)  {
        try {
            return mapper.readValue(json, Invoice.class);
        } catch (IOException e) {
            return null;
        }
    }

    private long getLastInvoiceId() throws IOException {
        String lastLine=fileHelper.readLastLine(filePath);
        if (lastLine == null) {
            return 0;
        }
        Invoice invoice = deserializeJsonToInvoice(lastLine);
        if (invoice == null) {
            return 0;
        }
        return invoice.getId();
    }

    private Invoice insertInvoice(Invoice invoice) throws IOException {
        Long id = nextId.incrementAndGet();
        Invoice insertedInvoice = Invoice.builder()
            .withInvoice(invoice)
            .withId(id)
            .build();
        fileHelper.writeLine(filePath, mapper.writeValueAsString(insertedInvoice));
        return insertedInvoice;
    }

    private Invoice updateInvoice(Invoice invoice) throws IOException, DatabaseOperationException {
        Invoice updatedInvoice = Invoice.builder()
            .withInvoice(invoice)
            .build();
        fileHelper.replaceLine(filePath, getPositionInDatabase(invoice.getId()), mapper.writeValueAsString(updatedInvoice));
        return updatedInvoice;
    }

    private List<Invoice> getInvoices() throws IOException {
        return fileHelper.readLines(filePath).stream()
            .map(invoice->deserializeJsonToInvoice(invoice))
            .filter(Objects::nonNull).
            collect(Collectors.toList());
    }

    private int getPositionInDatabase(Long id) throws IOException, DatabaseOperationException {
        List<Invoice>invoices = getInvoices();
        Optional<Invoice> invoice = invoices.stream()
            .filter(s -> s.getId().equals(id))
            .findFirst();
        if (invoice.isEmpty()) {
            throw new DatabaseOperationException(String.format("No invoice with id: %s", id));
        }
        return invoices.indexOf(invoice.get()) + 1;
    }

    @Override
    public synchronized Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            if (invoice.getId() == null || !exists(invoice.getId())) {
                return insertInvoice(invoice);
            }
            return updateInvoice(invoice);
        } catch (IOException e) {
            throw new DatabaseOperationException("An error occurred while saving invoice to database");
        }
    }

    @Override
    public synchronized void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        try {
            fileHelper.removeLine(filePath, getPositionInDatabase(id));
        } catch (IOException e) {
            throw new DatabaseOperationException(String.format("An error occurred while deleting invoice with id: %d from database", id));
        }
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        try {
            return getInvoices().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
        } catch (IOException e) {
            throw new DatabaseOperationException(String.format("An error occurred while getting invoice with id: %d from database", id));
        }
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            throw new IllegalArgumentException("Invoice number cannot be null.");
        }
        try {
            return getInvoices().stream()
                .filter(s -> s.getNumber().equals(number))
                .findFirst();
        } catch (IOException e) {
            throw new DatabaseOperationException(String.format("An error occurred while getting invoice with number: %s from database", number));
        }

    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            return getInvoices();
        } catch (IOException e) {
            throw new DatabaseOperationException("An error occurred while getting all invoices from database");
        }
    }

    @Override
    public synchronized void deleteAll() throws DatabaseOperationException {
        try {
            fileHelper.clear(filePath);
        } catch (IOException e) {
            throw new DatabaseOperationException("An error occurred while deleting all invoices from database");
        }
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        try {
            return getInvoices()
                .stream()
                .anyMatch(invoice -> invoice.getId().equals(id));
        } catch (IOException e) {
            throw new DatabaseOperationException("An error occurred while checking if invoice exists in database");
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return getInvoices().size();
        } catch (IOException e) {
            throw new DatabaseOperationException("An error occurred during getting number of invoices.");
        }
    }
}
