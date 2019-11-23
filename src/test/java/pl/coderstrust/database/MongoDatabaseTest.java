package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import pl.coderstrust.database.mongo.MongoInvoice;
import pl.coderstrust.database.mongo.MongoModelMapper;
import pl.coderstrust.database.mongo.MongoModelMapperImpl;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

@ExtendWith(MockitoExtension.class)
class MongoDatabaseTest {

    private MongoDatabase database;
    private MongoModelMapper modelMapper;

    @Mock
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setup() {
        modelMapper = new MongoModelMapperImpl();
        database = new MongoDatabase(mongoTemplate, modelMapper);
    }

    @Test
    void constructorShouldThrowExceptionForNullMongoTemplate() {
        assertThrows(IllegalArgumentException.class, () -> new MongoDatabase(null, modelMapper));
    }

    @Test
    void constructorShouldThrowExceptionForNullModelMapper() {
        assertThrows(IllegalArgumentException.class, () -> new MongoDatabase(mongoTemplate, null));
    }

    @Test
    void shouldInsert() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        Query findId = Query.query(Criteria.where("id").is(invoice.getId()));
        doReturn(null).when(mongoTemplate).findOne(findId, MongoInvoice.class);
        Invoice invoiceToBeInserted = Invoice.builder().withInvoice(invoice).withId(1L).build();
        doReturn(modelMapper.mapToMongoInvoice(invoiceToBeInserted)).when(mongoTemplate).insert(modelMapper.mapToMongoInvoice(invoiceToBeInserted));
        //when
        Invoice insertedInvoice = database.save(invoice);
        //then
        assertEquals(invoiceToBeInserted, insertedInvoice);
        verify(mongoTemplate).findOne(findId, MongoInvoice.class);
        verify(mongoTemplate).insert(modelMapper.mapToMongoInvoice(invoiceToBeInserted));
    }

    @Test
    void shouldUpdate() throws DatabaseOperationException {
        //given
        Invoice invoiceInDatabase = InvoiceGenerator.generateRandomInvoice();
        MongoInvoice mongoInvoiceInDatabase = modelMapper.mapToMongoInvoice(invoiceInDatabase);
        Query findId = Query.query(Criteria.where("id").is(invoiceInDatabase.getId()));
        doReturn(mongoInvoiceInDatabase).when(mongoTemplate).findOne(findId, MongoInvoice.class);
        Invoice invoiceUpdate = InvoiceGenerator.getRandomInvoiceWithSpecificId(invoiceInDatabase.getId());
        MongoInvoice mongoInvoiceUpdate = modelMapper.mapToMongoInvoice(invoiceUpdate);
        doReturn(mongoInvoiceUpdate).when(mongoTemplate).save(mongoInvoiceUpdate);
        //when
        Invoice updatedInvoice = database.save(invoiceUpdate);
        //then
        assertEquals(invoiceUpdate, updatedInvoice);
        verify(mongoTemplate).findOne(findId, MongoInvoice.class);
        verify(mongoTemplate).save(mongoInvoiceUpdate);
    }

    @Test
    void saveMethodShouldThrowExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> database.save(null));
    }

    @Test
    void saveMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringSearchingForInvoice() {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        Query findId = Query.query(Criteria.where("id").is(invoice.getId()));
        doThrow(new MockitoException("") {
        }).when(mongoTemplate).findOne(findId, MongoInvoice.class);
        //then
        assertThrows(DatabaseOperationException.class, () -> database.save(invoice));
        verify(mongoTemplate).findOne(findId, MongoInvoice.class);
    }

    @Test
    void saveMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringInsertingInvoice() {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        MongoInvoice addedInvoice = modelMapper.mapToMongoInvoice(Invoice.builder().withInvoice(invoice).withId(1L).build());
        MongoInvoice mongoInvoice = modelMapper.mapToMongoInvoice(invoice);
        doThrow(new MockitoException("") {
        }).when(mongoTemplate).insert(mongoInvoice);
        //then
        assertThrows(DatabaseOperationException.class, () -> database.save(invoice));
        verify(mongoTemplate).insert(addedInvoice);
    }

    @Test
    void saveMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringUpdatingInvoice() {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        MongoInvoice mongoInvoice = modelMapper.mapToMongoInvoice(invoice);
        Query findId = Query.query(Criteria.where("id").is(invoice.getId()));
        doReturn(mongoInvoice).when(mongoTemplate).findOne(findId, MongoInvoice.class);
        doThrow(new MockitoException("") {
        }).when(mongoTemplate).save(mongoInvoice);
        //then
        assertThrows(DatabaseOperationException.class, () -> database.save(invoice));
        verify(mongoTemplate).findOne(findId, MongoInvoice.class);
        verify(mongoTemplate).save(mongoInvoice);
    }

    @Test
    void shouldDelete() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        MongoInvoice mongoInvoice = modelMapper.mapToMongoInvoice(invoice);
        Long id = invoice.getId();
        Query findId = Query.query(Criteria.where("id").is(id));
        when(mongoTemplate.findAndRemove(findId, MongoInvoice.class)).thenReturn(mongoInvoice);
        //when
        database.delete(id);
        //then
        verify(mongoTemplate).findAndRemove(findId, MongoInvoice.class);
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.delete(null));
    }

    @Test
    void deleteMethodShouldThrowExceptionDuringDeletingNotExistingInvoice() {
        //given
        Query findId = Query.query(Criteria.where("id").is(10L));
        when(mongoTemplate.findAndRemove(findId, MongoInvoice.class)).thenReturn(null);
        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(10L));
        verify(mongoTemplate).findAndRemove(findId, MongoInvoice.class);
    }

    @Test
    void deleteMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringDeletingInvoice() {
        //given
        Query findId = Query.query(Criteria.where("id").is(10L));
        doThrow(new MockitoException("") {
        }).when(mongoTemplate).findAndRemove(findId, MongoInvoice.class);
        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(10L));
        verify(mongoTemplate).findAndRemove(findId, MongoInvoice.class);
    }

    @Test
    void shouldReturnInvoiceById() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        MongoInvoice mongoInvoice = modelMapper.mapToMongoInvoice(invoice);
        Query findId = Query.query(Criteria.where("id").is(invoice.getId()));
        doReturn(mongoInvoice).when(mongoTemplate).findOne(findId, MongoInvoice.class);
        //when
        Optional<Invoice> gotInvoice = database.getById(invoice.getId());
        //then
        assertTrue(gotInvoice.isPresent());
        assertEquals(invoice, gotInvoice.get());
        verify(mongoTemplate).findOne(findId, MongoInvoice.class);
    }

    @Test
    void shouldReturnEmptyOptionalWhileGettingNonExistingInvoiceById() throws DatabaseOperationException {
        //given
        Query findId = Query.query(Criteria.where("id").is(10L));
        when(mongoTemplate.findOne(findId, MongoInvoice.class)).thenReturn(null);
        //when
        Optional<Invoice> gotInvoice = database.getById(10L);
        //then
        assertTrue(gotInvoice.isEmpty());
        verify(mongoTemplate).findOne(findId, MongoInvoice.class);
    }

    @Test
    void getByIdMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringGettingInvoiceById() {
        //given
        Query findId = Query.query(Criteria.where("id").is(10L));
        doThrow(new MockitoException("") {
        }).when(mongoTemplate).findOne(findId, MongoInvoice.class);
        //then
        assertThrows(DatabaseOperationException.class, () -> database.getById(10L));
        verify(mongoTemplate).findOne(findId, MongoInvoice.class);
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));
    }

    @Test
    void shouldReturnInvoiceByNumber() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        Query findByNumber = Query.query(Criteria.where("number").is(invoice.getNumber()));
        MongoInvoice mongoInvoice = modelMapper.mapToMongoInvoice(invoice);
        when(mongoTemplate.findOne(findByNumber, MongoInvoice.class)).thenReturn(mongoInvoice);
        //when
        Optional<Invoice> gotInvoice = database.getByNumber(invoice.getNumber());
        //then
        assertTrue(gotInvoice.isPresent());
        assertEquals(invoice, gotInvoice.get());
        verify(mongoTemplate).findOne(findByNumber, MongoInvoice.class);
    }

    @Test
    void shouldReturnEmptyOptionalWhileGettingNonExistingInvoiceByNumber() throws DatabaseOperationException {
        //given
        String number = "123";
        Query findByNumber = Query.query(Criteria.where("number").is(number));
        when(mongoTemplate.findOne(findByNumber, MongoInvoice.class)).thenReturn(null);
        //when
        Optional<Invoice> gotInvoice = database.getByNumber(number);
        //then
        assertTrue(gotInvoice.isEmpty());
        verify(mongoTemplate).findOne(findByNumber, MongoInvoice.class);
    }

    @Test
    void getByNumberShouldThrowExceptionForNullNumber() {
        assertThrows(IllegalArgumentException.class, () -> database.getByNumber(null));
    }

    @Test
    void getByNumberMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringGettingInvoiceByNumber() {
        //given
        Query findByNumber = Query.query(Criteria.where("number").is("123"));
        doThrow(new MockitoException("") {
        }).when(mongoTemplate).findOne(findByNumber, MongoInvoice.class);
        //then
        assertThrows(DatabaseOperationException.class, () -> database.getByNumber("123"));
        verify(mongoTemplate).findOne(findByNumber, MongoInvoice.class);
    }

    @Test
    void shouldReturnAllInvoices() throws DatabaseOperationException {
        //given
        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
        Collection<Invoice> invoiceList = List.of(invoice1, invoice2);
        Collection<MongoInvoice> mongoInvoiceList = modelMapper.mapToMongoInvoices(invoiceList);
        doReturn(mongoInvoiceList).when(mongoTemplate).findAll(MongoInvoice.class);
        //when
        Collection<Invoice> gotList = database.getAll();
        //then
        assertEquals(gotList, invoiceList);
        verify(mongoTemplate).findAll(MongoInvoice.class);
    }

    @Test
    void getAllMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringGettingAllInvoices() {
        //given
        doThrow(new MockitoException("") {
        }).when(mongoTemplate).findAll(MongoInvoice.class);
        //then
        assertThrows(DatabaseOperationException.class, () -> database.getAll());
        verify(mongoTemplate).findAll(MongoInvoice.class);
    }

    @Test
    void shouldDeleteAllInvoices() throws DatabaseOperationException {
        //given
        doNothing().when(mongoTemplate).dropCollection(MongoInvoice.class);
        //when
        database.deleteAll();
        //then
        verify(mongoTemplate).dropCollection(MongoInvoice.class);
    }

    @Test
    void deleteAllMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringDeletingAllInvoices() {
        //given
        doThrow(new MockitoException("") {
        }).when(mongoTemplate).dropCollection(MongoInvoice.class);
        //then
        assertThrows(DatabaseOperationException.class, () -> database.deleteAll());
        verify(mongoTemplate).dropCollection(MongoInvoice.class);
    }

    @Test
    void shouldReturnTrueForExistingInvoice() throws DatabaseOperationException {
        //given
        Query findId = Query.query(Criteria.where("id").is(10L));
        when(mongoTemplate.exists(findId, MongoInvoice.class)).thenReturn(true);
        //then
        assertTrue(database.exists(10L));
        verify(mongoTemplate).exists(findId, MongoInvoice.class);
    }

    @Test
    void shouldReturnFalseForNotExistingInvoice() throws DatabaseOperationException {
        //given
        Query findId = Query.query(Criteria.where("id").is(10L));
        when(mongoTemplate.exists(findId, MongoInvoice.class)).thenReturn(false);
        //then
        assertFalse(database.exists(10L));
        verify(mongoTemplate).exists(findId, MongoInvoice.class);
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.exists(null));
    }

    @Test
    void existsMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringCheckingIfInvoiceExists() {
        //given
        Query findId = Query.query(Criteria.where("id").is(10L));
        doThrow(new MockitoException("") {
        }).when(mongoTemplate).exists(findId, MongoInvoice.class);
        //then
        assertThrows(DatabaseOperationException.class, () -> database.exists(10L));
        verify(mongoTemplate).exists(findId, MongoInvoice.class);
    }

    @Test
    void shouldReturnNumberOfInvoices() throws DatabaseOperationException {
        //given
        when(mongoTemplate.count(new Query(), MongoInvoice.class)).thenReturn(10L);
        //when
        long numberOfInvoices = database.count();
        //then
        assertEquals(10L, numberOfInvoices);
        verify(mongoTemplate).count(new Query(), MongoInvoice.class);
    }

    @Test
    void countMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringGettingNumberOfInvoices() {
        //given
        doThrow(new MockitoException("") {
        }).when(mongoTemplate).count(new Query(), MongoInvoice.class);
        //then
        assertThrows(DatabaseOperationException.class, () -> database.count());
        verify(mongoTemplate).count(new Query(), MongoInvoice.class);
    }
}
