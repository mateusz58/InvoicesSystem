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
    }

    @Override
    public synchronized Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            log.error("Attempt to add null invoice to database.");
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            if (invoice.getId() == null || !exists(invoice.getId())) {
                log.debug("Invoice has been successfully added to database.");
                return insertInvoice(invoice);
            }
            log.debug("Invoice has been successfully updated.");
            return updateInvoice(invoice);
        } catch (IOException e) {
            String message = "An error occurred during saving invoice.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public synchronized void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to delete invoice by null id.");
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        try {
            fileHelper.removeLine(filePath, getPositionInDatabase(id));
            log.debug("Invoice with id {} has been successfully deleted.", id);
        } catch (IOException e) {
            String message = "An error occurred during deleting invoice.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to get invoice by null id.");
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        try {
            return getInvoices().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
        } catch (IOException e) {
            String message = "An error occurred during getting invoice by id.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            log.error("Attempt to get invoice by null number.");
            throw new IllegalArgumentException("Invoice number cannot be null.");
        }
        try {
            return getInvoices().stream()
                .filter(s -> s.getNumber().equals(number))
                .findFirst();
        } catch (IOException e) {
            String message = "An error occurred during getting invoice by number.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }

    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            return getInvoices();
        } catch (IOException e) {
            String message = "An error occurred during getting all invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public synchronized void deleteAll() throws DatabaseOperationException {
        try {
            fileHelper.clear(filePath);
            log.debug("All invoices have been successfully deleted.");
        } catch (IOException e) {
            String message = "An error occurred during deleting all invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to check if invoice exists by null id.");
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        try {
            return getInvoices()
                .stream()
                .anyMatch(invoice -> invoice.getId().equals(id));
        } catch (IOException e) {
            String message = "An error occurred during checking if invoice exists.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return getInvoices().size();
        } catch (IOException e) {
            String message = "An error occurred during counting invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
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
            .id(id)
            .number(invoice.getNumber())
            .dueDate(invoice.getDueDate())
            .issuedDate(invoice.getIssuedDate())
            .buyer(invoice.getBuyer())
            .seller(invoice.getSeller())
            .entries(invoice.getEntries())
            .build();
        fileHelper.writeLine(filePath, mapper.writeValueAsString(insertedInvoice));
        return insertedInvoice;
    }

    private Invoice updateInvoice(Invoice invoice) throws IOException, DatabaseOperationException {
        Invoice updatedInvoice = Invoice.builder()
            .id(invoice.getId())
            .number(invoice.getNumber())
            .dueDate(invoice.getDueDate())
            .issuedDate(invoice.getIssuedDate())
            .buyer(invoice.getBuyer())
            .seller(invoice.getSeller())
            .entries(invoice.getEntries())
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
            throw new DatabaseOperationException(String.format("There is no invoice with id: %s", id));
        }
        return invoices.indexOf(invoice.get()) + 1;
    }
}
