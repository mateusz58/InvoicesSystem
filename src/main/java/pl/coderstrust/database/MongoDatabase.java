package pl.coderstrust.database;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import pl.coderstrust.database.mongo.MongoInvoice;
import pl.coderstrust.database.mongo.MongoModelMapper;
import pl.coderstrust.model.Invoice;

public class MongoDatabase implements Database {
    private final MongoTemplate mongoTemplate;
    private final MongoModelMapper modelMapper;
    private AtomicLong nextId = new AtomicLong(0);

    private AtomicLong init() {
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "idField"));
        query.limit(1);
        nextId = getAndIncrement((mongoTemplate.findOne(query, MongoInvoice.class)).getId());
        return null;
    }

    public MongoDatabase(MongoTemplate mongoTemplate, MongoModelMapper modelMapper) throws DatabaseOperationException {
        if (mongoTemplate == null) {
            throw new IllegalArgumentException("Database is empty.");
        }
        if (modelMapper == null) {
            throw new IllegalArgumentException("Mapper cannot be null.");
        }
        this.mongoTemplate = mongoTemplate;
        this.modelMapper = modelMapper;
    }

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            MongoInvoice savedInvoice = mongoTemplate.save(modelMapper.mapToMongoInvoice(invoice));
            //create private Invoice insertInvoice(Invoice invoice) and private Invoice updateInvoice(Invoice invoice, String mongoId) method
            return modelMapper.mapToInvoice(savedInvoice);
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during saving invoice.", e);
        }
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        try {
            MongoInvoice invoice = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), MongoInvoice.class);
            if (invoice == null) {
                throw new DatabaseOperationException(String.format("There is no invoice with id: %s", id));
            }
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during deleting invoice.", e);
        }
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            MongoInvoice invoice = getInvoiceById(id);
            if (invoice != null) {
                return Optional.of(modelMapper.mapToInvoice(invoice));
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during getting invoice by id.", e);
        }
    }

    private MongoInvoice getInvoiceById(Long id) {
        Optional<MongoInvoice> invoice = Optional.ofNullable(mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), MongoInvoice.class));
        return invoice.get();
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null.");
        }
        try {
            MongoInvoice invoice = mongoTemplate.findOne(Query.query(Criteria.where("number").is(number)), MongoInvoice.class);
                return Optional.ofNullable(modelMapper.mapToInvoice(invoice));
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during getting invoice by number.", e);
        }
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            return modelMapper.mapToInvoices(mongoTemplate.findAll(MongoInvoice.class));
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during getting all invoices.", e);
        }
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {
        try {
            mongoTemplate.dropCollection(MongoInvoice.class);
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during deleting all invoices.", e);
        }
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return mongoTemplate.exists(Query.query(Criteria.where("id").is(id)), MongoInvoice.class);
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during checking if invoice exists.", e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return mongoTemplate.count(new Query(), MongoInvoice.class);
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during getting number of invoices.", e);
        }
    }
}
