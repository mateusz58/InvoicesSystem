package pl.coderstrust.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pl.coderstrust.configuration.InFileDatabaseProperties;
import pl.coderstrust.helpers.FileHelper;
import pl.coderstrust.model.Invoice;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "in-file")
public class InFileDatabase implements Database {

    private String filePath;
    private ObjectMapper mapper;
    private FileHelper fileHelper;
    private AtomicLong nextId;

    private static Logger log = LoggerFactory.getLogger(InFileDatabase.class);

    @Autowired
    public InFileDatabase(InFileDatabaseProperties inFileDatabaseProperties, ObjectMapper mapper, FileHelper fileHelper) throws IOException {
        this.filePath = inFileDatabaseProperties.getFilePath();
        this.mapper = mapper;
        this.fileHelper = fileHelper;
        initFile();
        log.info("Database has been initialized");
    }

    @Override
    public synchronized Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            log.error("Attempt to add null invoice to database");
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            if (invoice.getId() == null || !exists(invoice.getId())) {
                log.error("Attempt to add existing invoice to database");
                return insertInvoice(invoice);
            }
            log.info("Invoice has been successfully added to database");
            return updateInvoice(invoice);
        } catch (IOException e) {
            log.error("An error occurred during adding invoice",e);
            throw new DatabaseOperationException("An error occurred while saving invoice to database");
        }
    }

    @Override
    public synchronized void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to get invoice by null id");
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        try {
            fileHelper.removeLine(filePath, getPositionInDatabase(id));
            log.info("Invoice has been successfully deleted");
        } catch (IOException e) {
            log.error("An error occurred during deleting invoice",e);
            throw new DatabaseOperationException(String.format("An error occurred while deleting invoice with id: %d from database", id));
        }
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to get invoice by null id");
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        try {
            return getInvoices().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
        } catch (IOException e) {
            log.error("An error occurred during getting invoice by id",e);
            throw new DatabaseOperationException(String.format("An error occurred while getting invoice with id: %d from database", id));
        }
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            log.error("Attempt to get invoice by null number");
            throw new IllegalArgumentException("Invoice number cannot be null.");
        }
        try {
            return getInvoices().stream()
                .filter(s -> s.getNumber().equals(number))
                .findFirst();
        } catch (IOException e) {
            log.error("An error occurred during getting invoice by number",e);
            throw new DatabaseOperationException(String.format("An error occurred while getting invoice with number: %s from database", number));
        }

    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            return getInvoices();
        } catch (IOException e) {
            log.error("An error occurred during getting all invoices",e);
            throw new DatabaseOperationException("An error occurred while getting all invoices from database");
        }
    }

    @Override
    public synchronized void deleteAll() throws DatabaseOperationException {
        try {
            fileHelper.clear(filePath);
            log.info("All invoices have been successfully deleted");
        } catch (IOException e) {
            log.error("An error occurred during deleting all invoices");
            throw new DatabaseOperationException("An error occurred while deleting all invoices from database");
        }
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to check if null invoice exists");
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        try {
            return getInvoices()
                .stream()
                .anyMatch(invoice -> invoice.getId().equals(id));
        } catch (IOException e) {
            log.error("An error occurred during checking if invoice exist",e);
            throw new DatabaseOperationException("An error occurred while checking if invoice exists in database");
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return getInvoices().size();
        } catch (IOException e) {
            log.error("An error occurred during counting invoices",e);
            throw new DatabaseOperationException("An error occurred during getting number of invoices.");
        }
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
        String lastLine = fileHelper.readLastLine(filePath);
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
            .map(invoice -> deserializeJsonToInvoice(invoice))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private int getPositionInDatabase(Long id) throws IOException, DatabaseOperationException {
        List<Invoice> invoices = getInvoices();
        Optional<Invoice> invoice = invoices.stream()
            .filter(s -> s.getId().equals(id))
            .findFirst();
        if (invoice.isEmpty()) {
            throw new DatabaseOperationException(String.format("No invoice with id: %s", id));
        }
        return invoices.indexOf(invoice.get()) + 1;
    }
}
