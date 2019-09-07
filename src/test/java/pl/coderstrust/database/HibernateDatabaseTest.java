package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.NonTransientDataAccessException;
import pl.coderstrust.database.hibernate.HibernateInvoice;
import pl.coderstrust.database.hibernate.InvoiceRepository;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

@ExtendWith(MockitoExtension.class)
class HibernateDatabaseTest {

    private HibernateDatabase database;
    private HibernateModelMapper modelMapper;

    @Mock
    private InvoiceRepository invoiceRepository;

    @BeforeEach
    void setup() {
        modelMapper = new HibernateModelMapperImpl();
        database = new HibernateDatabase(invoiceRepository, modelMapper);
    }

    @Test
    void constructorClassShouldThrowExceptionForNullInvoiceRepository() {
        assertThrows(IllegalArgumentException.class, () -> new HibernateDatabase(null, modelMapper));
    }

    @Test
    void constructorClassShouldThrowExceptionForNullModelMapper() {
        assertThrows(IllegalArgumentException.class, () -> new HibernateDatabase(invoiceRepository, null));
    }

    @Test
    void shouldSave() throws DatabaseOperationException {
        //given
        Invoice invoiceToBeSaved = InvoiceGenerator.generateRandomInvoice();
        HibernateInvoice hibernateInvoiceToBeSaved = modelMapper.mapToHibernateInvoice(invoiceToBeSaved);
        doReturn(hibernateInvoiceToBeSaved).when(invoiceRepository).save(hibernateInvoiceToBeSaved);

        //when
        Invoice savedInvoice = database.save(invoiceToBeSaved);

        //then
        assertEquals(invoiceToBeSaved, savedInvoice);
        verify(invoiceRepository).save(hibernateInvoiceToBeSaved);
    }

    @Test
    void saveMethodShouldThrowExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> database.save(null));
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionIsThrownWhenSavingInvoice() {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        HibernateInvoice hibernateInvoice = modelMapper.mapToHibernateInvoice(invoice);
        doThrow(new NonTransientDataAccessException("") {
        }).when(invoiceRepository).save(hibernateInvoice);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.save(invoice));
        verify(invoiceRepository).save(modelMapper.mapToHibernateInvoice(invoice));
    }

    @Test
    void shouldDelete() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        HibernateInvoice hibernateInvoice = modelMapper.mapToHibernateInvoice(invoice);
        when(invoiceRepository.existsById(hibernateInvoice.getId())).thenReturn(true);

        //when
        database.delete(invoice.getId());

        //then
        verify(invoiceRepository).deleteById(invoice.getId());
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
    void shouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionIsThrownWhenDeletingInvoice() {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        HibernateInvoice hibernateInvoice = modelMapper.mapToHibernateInvoice(invoice);
        when(invoiceRepository.existsById(hibernateInvoice.getId())).thenReturn(true);
        doThrow(new NonTransientDataAccessException("") {
        }).when(invoiceRepository).deleteById(hibernateInvoice.getId());

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(invoice.getId()));
        verify(invoiceRepository).deleteById(invoice.getId());
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNoSuchElementExceptionIsThrownWhenDeletingInvoice() {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        HibernateInvoice hibernateInvoice = modelMapper.mapToHibernateInvoice(invoice);
        when(invoiceRepository.existsById(hibernateInvoice.getId())).thenReturn(true);
        doThrow(new NoSuchElementException()).when(invoiceRepository).deleteById(hibernateInvoice.getId());

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(invoice.getId()));
        verify(invoiceRepository).deleteById(modelMapper.mapToHibernateInvoice(invoice).getId());
    }

    @Test
    void shouldGetInvoiceById() throws DatabaseOperationException {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        HibernateInvoice hibernateInvoice = modelMapper.mapToHibernateInvoice(invoice);
        when(invoiceRepository.existsById(hibernateInvoice.getId())).thenReturn(true);
        doReturn(hibernateInvoice).when(invoiceRepository).getOne(hibernateInvoice.getId());

        //when
        Optional<Invoice> gotInvoice = database.getById(invoice.getId());

        //then
        assertTrue(gotInvoice.isPresent());
        assertEquals(invoice, gotInvoice.get());
        verify(invoiceRepository).getOne(hibernateInvoice.getId());
    }

    @Test
    void shouldReturnEmptyOptionalWhenNonExistingInvoiceIsGotById() throws DatabaseOperationException {
        //given
        when(invoiceRepository.existsById(10L)).thenReturn(true);

        //when
        Optional<Invoice> gotInvoice = database.getById(10L);

        //then
        assertTrue(gotInvoice.isEmpty());
        verify(invoiceRepository).getOne(10L);
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNotExistingInvoice() {
        //given
        when(invoiceRepository.existsById(10L)).thenReturn(false);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getById(10L));
        verify(invoiceRepository).existsById(10L);
        verify(invoiceRepository, never()).getOne(10L);
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionIsThrownWhenGettingById() {
        //given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        HibernateInvoice hibernateInvoice = modelMapper.mapToHibernateInvoice(invoice);
        when(invoiceRepository.existsById(hibernateInvoice.getId())).thenReturn(true);
        doThrow(new NoSuchElementException()).when(invoiceRepository).getOne(hibernateInvoice.getId());

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getById(invoice.getId()));
        verify(invoiceRepository).getOne(invoice.getId());
    }

    @Test
    void shouldReturnInvoiceByNumber() throws DatabaseOperationException {
        //given
        List<HibernateInvoice> list = new ArrayList<>();
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        HibernateInvoice hibernateInvoice = modelMapper.mapToHibernateInvoice(invoice);
        list.add(hibernateInvoice);
        doReturn(list).when(invoiceRepository).findAll();

        //when
        Optional<Invoice> gotInvoice = database.getByNumber(invoice.getNumber());

        //then
        assertTrue(gotInvoice.isPresent());
        assertEquals(invoice, gotInvoice.get());
        verify(invoiceRepository).findAll();
    }

    @Test
    void shouldReturnEmptyOptionalWhenNonExistingInvoiceIsGotByNumber() throws DatabaseOperationException {
        //given
        List<HibernateInvoice> list = new ArrayList<>();
//        Invoice invoice = Invoice.builder().withNumber("123").build();
//        HibernateInvoice hibernateInvoice = modelMapper.mapToHibernateInvoice(invoice);
//        list.add(hibernateInvoice);
        doReturn(list).when(invoiceRepository).findAll();

        //when
        Optional<Invoice> gotInvoice = database.getByNumber("123");

        //then
        assertTrue(gotInvoice.isEmpty());
        verify(invoiceRepository).findAll();
    }
//

    @Test
    void getByNumberShouldThrowExceptionForNullNumber() {
        assertThrows(IllegalArgumentException.class, () -> database.getByNumber(null));
    }

    //
//    @Test
//    void shouldReturnAllInvoices() {
//        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
//        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
//        storage.put(invoice1.getId(), InvoiceGenerator.generateRandomInvoice());
//        storage.put(invoice2.getId(), InvoiceGenerator.generateRandomInvoice());
//
//        assertEquals(storage.values(), database.getAll());
//    }
//
//    @Test
//    void shouldDeleteAll() {
//        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
//        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
//        storage.put(invoice1.getId(), InvoiceGenerator.generateRandomInvoice());
//        storage.put(invoice2.getId(), InvoiceGenerator.generateRandomInvoice());
//
//        database.deleteAll();
//
//        assertEquals(new HashMap<>(), storage);
//    }
//
//    @Test
//    void shouldReturnTrueForExistingInvoice() {
//        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
//        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
//        storage.put(invoice1.getId(), InvoiceGenerator.generateRandomInvoice());
//        storage.put(invoice2.getId(), InvoiceGenerator.generateRandomInvoice());
//
//        assertTrue(database.exists(invoice1.getId()));
//    }
//
//    @Test
//    void shouldReturnFalseForNonExistingInvoice() {
//        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
//        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
//        storage.put(invoice1.getId(), InvoiceGenerator.generateRandomInvoice());
//        storage.put(invoice2.getId(), InvoiceGenerator.generateRandomInvoice());
//
//        assertFalse(database.exists(666L));
//    }
//
    @Test
    void existsMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.exists(null));
    }
//
//    @Test
//    void shouldReturnNumberOfInvoices() {
//        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
//        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
//        storage.put(invoice1.getId(), InvoiceGenerator.generateRandomInvoice());
//        storage.put(invoice2.getId(), InvoiceGenerator.generateRandomInvoice());
//
//        assertEquals(2, database.count());
//    }
}
