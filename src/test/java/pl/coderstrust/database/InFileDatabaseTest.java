package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.coderstrust.configuration.ApplicationConfiguration;
import pl.coderstrust.configuration.InFileDatabaseProperties;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.helpers.FileHelper;
import pl.coderstrust.model.Invoice;

@ExtendWith(MockitoExtension.class)
class InFileDatabaseTest {

    private static final String DATABASE_FILE = "src/test/resources/database/database.json";
    private static ObjectMapper objectMapper;

    @Mock
    private FileHelper fileHelper;
    private InFileDatabase inFileDatabase;

    @BeforeEach
    void setup() throws IOException {
        objectMapper = new ApplicationConfiguration().getObjectMapper();
        InFileDatabaseProperties inFileDatabasePropertiesTest = new InFileDatabaseProperties();
        inFileDatabasePropertiesTest.setFilePath(DATABASE_FILE);
        doReturn(false).when(fileHelper).exists(DATABASE_FILE);
        inFileDatabase = new InFileDatabase(inFileDatabasePropertiesTest, objectMapper, fileHelper);
    }

    @Test
    void shouldAddInvoice() throws DatabaseOperationException, IOException {
        //Given
        Invoice invoiceToAdd = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        doNothing().when(fileHelper).writeLine(DATABASE_FILE, objectMapper.writeValueAsString(invoiceToAdd));
        doReturn(List.of(objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice()), objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice()), objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice()))).when(fileHelper).readLines(DATABASE_FILE);
        //When
        Invoice addedInvoice = inFileDatabase.save(invoiceToAdd);
        //Then
        assertEquals(invoiceToAdd, addedInvoice);
        verify(fileHelper).readLines(DATABASE_FILE);
        verify(fileHelper).writeLine(DATABASE_FILE, objectMapper.writeValueAsString(invoiceToAdd));
    }

    @Test
    void shouldUpdateInvoice() throws IOException, DatabaseOperationException {
        //Given
        Invoice invoiceToUpdate = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        doNothing().when(fileHelper).replaceLine(DATABASE_FILE, 1, objectMapper.writeValueAsString(invoiceToUpdate));
        doReturn(Collections.singletonList(objectMapper.writeValueAsString(invoiceToUpdate))).when(fileHelper).readLines(DATABASE_FILE);
        //When
        Invoice updatedInvoice = inFileDatabase.save(invoiceToUpdate);
        //Then
        verify(fileHelper).replaceLine(DATABASE_FILE, 1, objectMapper.writeValueAsString(updatedInvoice));
        verify(fileHelper, times(2)).readLines(DATABASE_FILE);
        assertEquals(invoiceToUpdate, updatedInvoice);
    }

    @Test
    void shouldReturnAllInvoices() throws IOException, DatabaseOperationException {
        //Given
        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
        List<Invoice> expected = Arrays.asList(invoice1, invoice2);
        doReturn(List.of(objectMapper.writeValueAsString(invoice1), objectMapper.writeValueAsString(invoice2))).when(fileHelper).readLines(DATABASE_FILE);
        //When
        Collection<Invoice> result = inFileDatabase.getAll();
        //Then
        assertEquals(expected, result);
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnInvoiceById() throws DatabaseOperationException, IOException {
        //Given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(invoice), objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice()))).when(fileHelper).readLines(DATABASE_FILE);
        //When
        Optional<Invoice> optionalInvoice = inFileDatabase.getById(invoice.getId());
        //Then
        assertTrue(optionalInvoice.isPresent());
        assertEquals(invoice, optionalInvoice.get());
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnInvoiceByNumber() throws DatabaseOperationException, IOException {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice()), objectMapper.writeValueAsString(invoiceToGet))).when(fileHelper).readLines(DATABASE_FILE);
        //When
        Optional<Invoice> optionalInvoice = inFileDatabase.getByNumber(invoiceToGet.getNumber());
        //Then
        assertTrue(optionalInvoice.isPresent());
        assertEquals(invoiceToGet, optionalInvoice.get());
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldDeleteAllInvoices() throws DatabaseOperationException, IOException {
        //Given
        doNothing().when(fileHelper).clear(DATABASE_FILE);
        //When
        inFileDatabase.deleteAll();
        //Then
        verify(fileHelper).clear(DATABASE_FILE);
    }

    @Test
    void shouldReturnNumberOfInvoices() throws IOException, DatabaseOperationException {
        //Given
        doReturn(List.of(objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice()), objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice()))).when(fileHelper).readLines(DATABASE_FILE);
        //Then
        assertEquals(2, inFileDatabase.count());
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnFalseForNonExistingInvoice() throws IOException, DatabaseOperationException {
        //Given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(invoice))).when(fileHelper).readLines(DATABASE_FILE);
        //When
        boolean result = inFileDatabase.exists(invoice.getId() + 1L);
        //Then
        assertFalse(result);
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnTrueForExistingInvoice() throws IOException, DatabaseOperationException {
        //Given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(invoice))).when(fileHelper).readLines(DATABASE_FILE);
        //When
        boolean result = inFileDatabase.exists(invoice.getId());
        //Then
        assertTrue(result);
        verify(fileHelper, times(1)).readLines(DATABASE_FILE);
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException, IOException {
        //Given
        Invoice invoiceToDelete = InvoiceGenerator.generateRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(invoiceToDelete))).when(fileHelper).readLines(DATABASE_FILE);
        doNothing().when(fileHelper).removeLine(DATABASE_FILE, 1);
        //When
        inFileDatabase.delete(invoiceToDelete.getId());
        //Then
        verify(fileHelper, times(1)).readLines(DATABASE_FILE);
        verify(fileHelper).removeLine(DATABASE_FILE, 1);
    }

    @Test
    void saveMethodShouldThrowExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.save(null));
    }

    @Test
    void deleteMethodShouldThrowExceptionDuringDeletingNotExistingInvoice() throws IOException {
        //Given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(invoice))).when(fileHelper).readLines(DATABASE_FILE);
        //Then
        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.delete(invoice.getId() + 1L));
        verify(fileHelper).readLines(DATABASE_FILE);
        verify(fileHelper, never()).removeLine(anyString(), anyInt());
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.delete(null));
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.getById(null));
    }

    @Test
    void getByNumberMethodShouldThrowExceptionForNullNumber() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.getByNumber(null));
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.exists(null));
    }

    @Test
    void saveMethodShouldThrowExceptionWhenFileHelpersWriteLineMethodThrowsException() throws IOException {
        //Given
        Invoice invoiceToAdd = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        doReturn(new ArrayList<>()).when(fileHelper).readLines(DATABASE_FILE);
        doThrow(IOException.class).when(fileHelper).writeLine(DATABASE_FILE, objectMapper.writeValueAsString(invoiceToAdd));
        //Then
        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.save(invoiceToAdd));
        verify(fileHelper).readLines(DATABASE_FILE);
        verify(fileHelper).writeLine(DATABASE_FILE, objectMapper.writeValueAsString(invoiceToAdd));
    }

    @Test
    void saveMethodShouldThrowExceptionWhenFileHelpersReplaceLineMethodThrowsException() throws IOException {
        //Given
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(invoiceToAdd))).when(fileHelper).readLines(DATABASE_FILE);
        doThrow(IOException.class).when(fileHelper).replaceLine(DATABASE_FILE, 1, objectMapper.writeValueAsString(invoiceToAdd));
        //Then
        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.save(invoiceToAdd));
        verify(fileHelper, times(2)).readLines(DATABASE_FILE);
        verify(fileHelper).replaceLine(DATABASE_FILE, 1, objectMapper.writeValueAsString(invoiceToAdd));
    }

    @Test
    void deleteMethodShouldThrowExceptionWhenFileHelperThrowsException() throws IOException {
        //Given
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(invoiceToAdd))).when(fileHelper).readLines(DATABASE_FILE);
        doThrow(IOException.class).when(fileHelper).removeLine(DATABASE_FILE, 1);
        //Then
        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.delete(invoiceToAdd.getId()));
        verify(fileHelper, times(1)).readLines(DATABASE_FILE);
        verify(fileHelper).removeLine(DATABASE_FILE, 1);
    }

    @Test
    void shouldReturnEmptyOptionalWhenGettingNonExistingInvoiceById() throws IOException, DatabaseOperationException {
        //Given
        doReturn(new ArrayList<>()).when(fileHelper).readLines(DATABASE_FILE);
        //When
        Optional<Invoice> optionalInvoice = inFileDatabase.getById(InvoiceGenerator.generateRandomInvoice().getId());
        //Then
        assertTrue(optionalInvoice.isEmpty());
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void getByIdMethodShouldThrowExceptionWhenFileHelperThrowsException() throws IOException {
        //Given
        doThrow(IOException.class).when(fileHelper).readLines(DATABASE_FILE);
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        //Then
        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.getById(invoice.getId()));
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnEmptyOptionalWhenGettingNonExistingInvoiceByNumber() throws DatabaseOperationException, IOException {
        //Given
        doReturn(new ArrayList<>()).when(fileHelper).readLines(DATABASE_FILE);
        //When
        Optional<Invoice> optionalInvoice = inFileDatabase.getByNumber(InvoiceGenerator.generateRandomInvoice().getNumber());
        //Then
        assertTrue(optionalInvoice.isEmpty());
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void getByNumberMethodShouldThrowExceptionWhenFileHelperThrowsException() throws IOException {
        //Given
        doThrow(IOException.class).when(fileHelper).readLines(DATABASE_FILE);
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        //Then
        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.getByNumber(invoice.getNumber()));
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void getAllMethodShouldReturnEmptyListWhenDatabaseIsEmpty() throws IOException, DatabaseOperationException {
        //Given
        doReturn(Collections.emptyList()).when(fileHelper).readLines(DATABASE_FILE);
        //When
        Collection<Invoice> result = inFileDatabase.getAll();
        //Then
        assertEquals(Collections.emptyList(), result);
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void getAllMethodShouldThrowExceptionWhenFileHelperThrowsException() throws IOException {
        //Given
        doThrow(IOException.class).when(fileHelper).readLines(DATABASE_FILE);
        //Then
        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.getAll());
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void deleteAllMethodShouldThrowExceptionWhenFileHelperThrowsException() throws IOException {
        //Given
        doThrow(IOException.class).when(fileHelper).clear(DATABASE_FILE);
        //Then
        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.deleteAll());
        verify(fileHelper).clear(DATABASE_FILE);
    }

    @Test
    void existsMethodShouldThrowExceptionWhenFileHelperThrowsException() throws IOException {
        //Given
        Invoice invoiceInDatabase = InvoiceGenerator.generateRandomInvoice();
        doThrow(IOException.class).when(fileHelper).readLines(DATABASE_FILE);
        //Then
        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.exists(invoiceInDatabase.getId()));
        verify(fileHelper).exists(DATABASE_FILE);
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void countMethodShouldThrowExceptionWhenFileHelperThrowsException() throws IOException {
        //Given
        doThrow(IOException.class).when(fileHelper).readLines(DATABASE_FILE);
        //Then
        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.count());
        verify(fileHelper).exists(DATABASE_FILE);
        verify(fileHelper).readLines(DATABASE_FILE);
    }
}
