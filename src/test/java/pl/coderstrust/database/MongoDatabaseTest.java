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
    void setup() throws DatabaseOperationException {
        modelMapper = new MongoModelMapperImpl();
        database = new MongoDatabase(mongoTemplate, modelMapper);
    }

    @Test
    void constructorShouldThrowExceptionForNullInvoiceRepository() {
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
        doReturn(null).when(mongoTemplate).findOne(Query.query(Criteria.where("id").is(invoice.getId())), MongoInvoice.class);
        Invoice invoiceToBeInserted = Invoice.builder().withInvoice(invoice).withId(1L).build();
        doReturn(modelMapper.mapToMongoInvoice(invoiceToBeInserted)).when(mongoTemplate).insert(modelMapper.mapToMongoInvoice(invoiceToBeInserted));

        //when
        Invoice insertedInvoice = database.save(invoice);

        //then
        assertEquals(invoiceToBeInserted, insertedInvoice);
        verify(mongoTemplate).insert(modelMapper.mapToMongoInvoice(invoiceToBeInserted));
    }

    @Test
    void shouldUpdate() throws DatabaseOperationException {
        //given
        Invoice invoiceToBeSaved = InvoiceGenerator.generateRandomInvoice();
        MongoInvoice mongoInvoiceToBeSaved = modelMapper.mapToMongoInvoice(invoiceToBeSaved);
        doReturn(mongoInvoiceToBeSaved).when(mongoTemplate).save(mongoInvoiceToBeSaved);

        //when
        Invoice savedInvoice = database.save(invoiceToBeSaved);

        //then
        assertEquals(invoiceToBeSaved, savedInvoice);
        verify(mongoTemplate).save(mongoInvoiceToBeSaved);
    }

    @Test
    void saveMethodShouldThrowExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> database.save(null));
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenExceptionIsThrownWhenSavingInvoice() {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        MongoInvoice mongoInvoice = modelMapper.mapToMongoInvoice(invoice);
        doThrow(new MockitoException("") {
        }).when(mongoTemplate).save(mongoInvoice);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.save(invoice));
    }

    @Test
    void shouldDelete() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        MongoInvoice mongoInvoice = modelMapper.mapToMongoInvoice(invoice);
        Long id = invoice.getId();
        when(mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), MongoInvoice.class)).thenReturn(mongoInvoice);

        //when
        database.delete(id);

        //then
        assertFalse(mongoTemplate.exists(Query.query(Criteria.where("id").is(id)), MongoInvoice.class));
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.delete(null));
    }

    @Test
    void deleteMethodShouldThrowExceptionForDeletingNotExistingInvoice() {
        //given
        when(mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(10L)), MongoInvoice.class)).thenReturn(null);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(10L));
    }

    @Test
    void deleteMethodShouldThrowDatabaseOperationExceptionWhenExceptionOccurDuringDeletingInvoice() {
        //given
        doThrow(new MockitoException("") {
        }).when(mongoTemplate).findAndRemove(Query.query(Criteria.where("id").is(10L)), MongoInvoice.class);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(10L));
    }

    @Test
    void shouldReturnInvoiceById() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        MongoInvoice mongoInvoice = modelMapper.mapToMongoInvoice(invoice);
        doReturn(mongoInvoice).when(mongoTemplate).findOne(Query.query(Criteria.where("id").is(invoice.getId())), MongoInvoice.class);

        //when
        Optional<Invoice> gotInvoice = database.getById(invoice.getId());

        //then
        assertTrue(gotInvoice.isPresent());
        assertEquals(invoice, gotInvoice.get());
        verify(mongoTemplate).findOne(Query.query(Criteria.where("id").is(invoice.getId())), MongoInvoice.class);
    }

    @Test
    void shouldReturnEmptyOptionalWhenNonExistingInvoiceIsGotById() throws DatabaseOperationException {
        //given
        when(mongoTemplate.findOne(Query.query(Criteria.where("id").is(10L)), MongoInvoice.class)).thenReturn(null);

        //when
        Optional<Invoice> gotInvoice = database.getById(10L);

        //then
        assertTrue(gotInvoice.isEmpty());
        verify(mongoTemplate).findOne(Query.query(Criteria.where("id").is(10L)), MongoInvoice.class);
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenExceptionIsThrownWhenGettingById() {
        //given
        doThrow(new MockitoException("") {
        }).when(mongoTemplate).findOne(Query.query(Criteria.where("id").is(10L)), MongoInvoice.class);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getById(10L));
        verify(mongoTemplate).findOne(Query.query(Criteria.where("id").is(10L)), MongoInvoice.class);
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));
    }

    @Test
    void shouldReturnInvoiceByNumber() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        MongoInvoice mongoInvoice = modelMapper.mapToMongoInvoice(invoice);
        String number = invoice.getNumber();
        when(mongoTemplate.findOne(Query.query(Criteria.where("number").is(number)), MongoInvoice.class)).thenReturn(mongoInvoice);

        //when
        Optional<Invoice> gotInvoice = database.getByNumber(invoice.getNumber());

        //then
        assertTrue(gotInvoice.isPresent());
        assertEquals(invoice, gotInvoice.get());
        verify(mongoTemplate).findOne(Query.query(Criteria.where("number").is(number)), MongoInvoice.class);
    }

    @Test
    void shouldReturnEmptyOptionalWhenNonExistingInvoiceIsGotByNumber() throws DatabaseOperationException {
        //given
        String number = "123";
        when(mongoTemplate.findOne(Query.query(Criteria.where("number").is(number)), MongoInvoice.class)).thenReturn(null);

        //when
        Optional<Invoice> gotInvoice = database.getByNumber(number);

        //then
        assertTrue(gotInvoice.isEmpty());
        verify(mongoTemplate).findOne(Query.query(Criteria.where("number").is(number)), MongoInvoice.class);
    }

    @Test
    void getByNumberShouldThrowExceptionForNullNumber() {
        assertThrows(IllegalArgumentException.class, () -> database.getByNumber(null));
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenExceptionIsThrownWhenGettingByNumber() {
        //given
        doThrow(new MockitoException("") {
        }).when(mongoTemplate).findOne(Query.query(Criteria.where("number").is("123")), MongoInvoice.class);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getByNumber("123"));
        verify(mongoTemplate).findOne(Query.query(Criteria.where("number").is("123")), MongoInvoice.class);
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
    void shouldThrowDatabaseOperationExceptionWhenExceptionIsThrownWhenGettingAll() {
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
    void shouldThrowDatabaseOperationExceptionWhenExceptionIsThrownWhenDeletingAll() {
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
        when(mongoTemplate.exists(Query.query(Criteria.where("id").is(10L)), MongoInvoice.class)).thenReturn(true);

        //then
        assertTrue(database.exists(10L));
        verify(mongoTemplate).exists(Query.query(Criteria.where("id").is(10L)), MongoInvoice.class);
    }

    @Test
    void shouldReturnFalseForNotExistingInvoice() throws DatabaseOperationException {
        //given
        when(mongoTemplate.exists(Query.query(Criteria.where("id").is(10L)), MongoInvoice.class)).thenReturn(false);

        //then
        assertFalse(database.exists(10L));
        verify(mongoTemplate).exists(Query.query(Criteria.where("id").is(10L)), MongoInvoice.class);
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.exists(null));
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenExceptionIsThrownWhenCheckingIfInvoiceExists() {
        //given
        doThrow(new MockitoException("") {
        }).when(mongoTemplate).exists(Query.query(Criteria.where("id").is(10L)), MongoInvoice.class);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.exists(10L));
        verify(mongoTemplate).exists(Query.query(Criteria.where("id").is(10L)), MongoInvoice.class);
    }

    @Test
    void shouldReturnNumberOfInvoices() throws DatabaseOperationException {
        //given
        when(mongoTemplate.count(Query.query(Criteria.where("id").regex("/[0-9]+/")), MongoInvoice.class)).thenReturn(10L);

        //when
        long numberOfInvoices = database.count();

        //then
        assertEquals(10L, numberOfInvoices);
        verify(mongoTemplate).count(Query.query(Criteria.where("id").regex("/[0-9]+/")), MongoInvoice.class);
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionIsThrownWhenCauntingNumberOfInvoices() {
        //given
        doThrow(new MockitoException("") {
        }).when(mongoTemplate).count(Query.query(Criteria.where("id").regex("/[0-9]+/")), MongoInvoice.class);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.count());
        verify(mongoTemplate).count(Query.query(Criteria.where("id").regex("/[0-9]+/")), MongoInvoice.class);
    }
}
