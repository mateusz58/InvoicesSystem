package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import pl.coderstrust.database.hibernate.HibernateInvoice;
import pl.coderstrust.database.hibernate.MongoModelMapperImpl;
import pl.coderstrust.database.mongo.MongoInvoice;
import pl.coderstrust.database.mongo.MongoModelMapper;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

@ExtendWith(MockitoExtension.class)
class MongoDatabaseTest {

    private MongoDatabase database;
    private MongoModelMapper modelMapper;

    @Mock
    private MongoTemplate invoiceRepository;

    @BeforeEach
    void setup() throws DatabaseOperationException {
        modelMapper = new MongoModelMapperImpl();
        database = new MongoDatabase(invoiceRepository, modelMapper);
    }

    @Test
    void constructorShouldThrowExceptionForNullInvoiceRepository() {
        assertThrows(IllegalArgumentException.class, () -> new MongoDatabase(null, modelMapper));
    }

    @Test
    void constructorShouldThrowExceptionForNullModelMapper() {
        assertThrows(IllegalArgumentException.class, () -> new MongoDatabase(invoiceRepository, null));
    }

    @Test
    void shouldSave() throws DatabaseOperationException {
        //given
        Invoice invoiceToBeSaved = InvoiceGenerator.generateRandomInvoice();
        MongoInvoice mongoInvoiceToBeSaved = modelMapper.mapToMongoInvoice(invoiceToBeSaved);
        doReturn(mongoInvoiceToBeSaved).when(invoiceRepository).save(mongoInvoiceToBeSaved);

        //when
        Invoice savedInvoice = database.save(invoiceToBeSaved);

        //then
        assertEquals(invoiceToBeSaved, savedInvoice);
        verify(invoiceRepository).save(mongoInvoiceToBeSaved);
    }

    @Test
    void saveMethodShouldThrowExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> database.save(null));
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionIsThrownWhenSavingInvoice() {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        MongoInvoice mongoInvoice = modelMapper.mapToMongoInvoice(invoice);
        doThrow(new NonTransientDataAccessException("") {}).when(invoiceRepository).save(mongoInvoice);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.save(invoice));
        verify(invoiceRepository).save(modelMapper.mapToMongoInvoice(invoice));
    }

    @Test
    void shouldDelete() throws DatabaseOperationException {
        //given
        when(invoiceRepository.existsById(10L)).thenReturn(true);

        //when
        database.delete(10L);

        //then
        verify(invoiceRepository).deleteById(10L);
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.delete(null));
    }

    @Test
    void deleteMethodShouldThrowExceptionForDeletingNotExistingInvoice() {
        //given
        when(invoiceRepository.existsById(10L)).thenReturn(false);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(10L));
        verify(invoiceRepository).existsById(10L);
        verify(invoiceRepository, never()).deleteById(10L);
    }

    @Test
    void deleteMethodShouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionOccurDuringDeletingInvoice() {
        //given
        when(invoiceRepository.existsById(10L)).thenReturn(true);
        doThrow(new NonTransientDataAccessException("") {}).when(invoiceRepository).deleteById(10L);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(10L));
        verify(invoiceRepository).deleteById(10L);
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNoSuchElementExceptionIsThrownWhenDeletingInvoice() {
        //given
        when(invoiceRepository.existsById(10L)).thenReturn(true);
        doThrow(new NoSuchElementException()).when(invoiceRepository).deleteById(10L);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(10L));
        verify(invoiceRepository).deleteById(10L);
    }

    @Test
    void shouldReturnInvoiceById() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        MongoInvoice mongoInvoice = modelMapper.mapToMongoInvoice(invoice);
        doReturn(Optional.of(mongoInvoice)).when(invoiceRepository).findById(mongoInvoice.getId());

        //when
        Optional<Invoice> gotInvoice = database.getById(invoice.getId());

        //then
        assertTrue(gotInvoice.isPresent());
        assertEquals(invoice, gotInvoice.get());
        verify(invoiceRepository).findById(mongoInvoice.getId());
    }

    @Test
    void shouldReturnEmptyOptionalWhenNonExistingInvoiceIsGotById() throws DatabaseOperationException {
        //given
        when(invoiceRepository.findById(10L)).thenReturn(Optional.empty());

        //when
        Optional<Invoice> gotInvoice = database.getById(10L);

        //then
        assertTrue(gotInvoice.isEmpty());
        verify(invoiceRepository).findById(10L);
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNoSuchElementExceptionIsThrownWhenGettingById() {
        //given
        doThrow(new NoSuchElementException()).when(invoiceRepository).findById(10L);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getById(10L));
        verify(invoiceRepository).findById(10L);
    }

    @Test
    void shouldReturnInvoiceByNumber() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        MongoInvoice mongoInvoice = modelMapper.mapToMongoInvoice(invoice);
        when(invoiceRepository.findOne(any(Example.class))).thenReturn(Optional.of(mongoInvoice));

        //when
        Optional<Invoice> gotInvoice = database.getByNumber(invoice.getNumber());

        //then
        assertTrue(gotInvoice.isPresent());
        assertEquals(invoice, gotInvoice.get());
        verify(invoiceRepository).findOne(any(Example.class));
    }

    @Test
    void shouldReturnEmptyOptionalWhenNonExistingInvoiceIsGotByNumber() throws DatabaseOperationException {
        //when
        Optional<Invoice> gotInvoice = database.getByNumber("123");

        //then
        assertTrue(gotInvoice.isEmpty());
        verify(invoiceRepository).findOne(any(Example.class));
    }

    @Test
    void getByNumberShouldThrowExceptionForNullNumber() {
        assertThrows(IllegalArgumentException.class, () -> database.getByNumber(null));
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionIsThrownWhenGettingByNumber() {
        //given
        doThrow(new NonTransientDataAccessException("") {}).when(invoiceRepository).findOne(any(Example.class));

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getByNumber("123"));
        verify(invoiceRepository).findOne(any(Example.class));
    }

    @Test
    void shouldReturnAllInvoices() throws DatabaseOperationException {
        //given
        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
        Collection<Invoice> invoiceList = List.of(invoice1, invoice2);
        Collection<HibernateInvoice> hibernateInvoiceList = modelMapper.mapToMongoInvoice(invoiceList);
        doReturn(hibernateInvoiceList).when(invoiceRepository).findAll();

        //when
        Collection<Invoice> gotList = database.getAll();

        //then
        assertEquals(gotList, invoiceList);
        verify(invoiceRepository).findAll();
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionIsThrownWhenGettingAll() {
        //given
        doThrow(new NonTransientDataAccessException("") {}).when(invoiceRepository).findAll();

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getAll());
        verify(invoiceRepository).findAll();
    }

    @Test
    void shouldDeleteAllInvoices() throws DatabaseOperationException {
        //given
        doNothing().when(invoiceRepository).deleteAll();

        //when
        database.deleteAll();

        //then
        verify(invoiceRepository).deleteAll();
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionIsThrownWhenDeletingAll() {
        //given
        doThrow(new NonTransientDataAccessException("") {}).when(invoiceRepository).deleteAll();

        //then
        assertThrows(DatabaseOperationException.class, () -> database.deleteAll());
        verify(invoiceRepository).deleteAll();
    }

    @Test
    void shouldReturnTrueForExistingInvoice() throws DatabaseOperationException {
        //given
        when(invoiceRepository.existsById(10L)).thenReturn(true);

        //then
        assertTrue(database.exists(10L));
        verify(invoiceRepository).existsById(10L);
    }

    @Test
    void shouldReturnFalseForNotExistingInvoice() throws DatabaseOperationException {
        //given
        when(invoiceRepository.existsById(10L)).thenReturn(false);

        //then
        assertFalse(database.exists(10L));
        verify(invoiceRepository).existsById(10L);
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.exists(null));
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionIsThrownWhenCheckingIfInvoiceExists() {
        //given
        doThrow(new NonTransientDataAccessException("") {}).when(invoiceRepository).existsById(10L);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.exists(10L));
        verify(invoiceRepository).existsById(10L);
    }

    @Test
    void shouldReturnNumberOfInvoices() throws DatabaseOperationException {
        //given
        when(invoiceRepository.count()).thenReturn(10L);

        //when
        long numberOfInvoices = database.count();

        //then
        assertEquals(10L, numberOfInvoices);
        verify(invoiceRepository).count();
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionIsThrownWhenCauntingNumberOfInvoices() {
        //given
        doThrow(new NonTransientDataAccessException("") {}).when(invoiceRepository).count();

        //then
        assertThrows(DatabaseOperationException.class, () -> database.count());
        verify(invoiceRepository).count();
    }
}
