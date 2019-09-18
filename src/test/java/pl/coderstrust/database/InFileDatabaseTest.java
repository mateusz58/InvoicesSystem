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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private InFileDatabase inFileDatabase;

    @BeforeEach
    void setup() throws IOException {
        new ApplicationConfiguration().getObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        InFileDatabaseProperties inFileDatabasePropertiesTest = new InFileDatabaseProperties();
        inFileDatabasePropertiesTest.setFilePath(DATABASE_FILE);
        doReturn(false).when(fileHelper).exists(DATABASE_FILE);
        inFileDatabase = new InFileDatabase(inFileDatabasePropertiesTest, objectMapper, fileHelper);
    }

    @Test
    void shouldAddInvoiceToFile() throws DatabaseOperationException, IOException {
        //Given
        Invoice invoiceToAdd = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        doNothing().when(fileHelper).writeLine(DATABASE_FILE, objectMapper.writeValueAsString(invoiceToAdd));
        doReturn(new ArrayList<>()).when(fileHelper).readLines(DATABASE_FILE);
        //When
        Invoice expectedInvoice = inFileDatabase.save(invoiceToAdd);
        //Then
        assertEquals(expectedInvoice, invoiceToAdd);
        verify(fileHelper).readLines(DATABASE_FILE);
        verify(fileHelper).writeLine(DATABASE_FILE, objectMapper.writeValueAsString(invoiceToAdd));
    }

    @Test
    void shouldUpdateInvoiceToFile() throws IOException, DatabaseOperationException {
        //Given
        Invoice invoiceToUpdate = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        //When
        doNothing().when(fileHelper).replaceLine(DATABASE_FILE, 1, objectMapper.writeValueAsString(invoiceToUpdate));
        doReturn(Collections.singletonList(objectMapper.writeValueAsString(invoiceToUpdate))).when(fileHelper).readLines(DATABASE_FILE);
        Invoice updatedInvoice = inFileDatabase.save(invoiceToUpdate);
        //Then
        verify(fileHelper).replaceLine(DATABASE_FILE, 1, objectMapper.writeValueAsString(updatedInvoice));
        verify(fileHelper, times(3)).readLines(DATABASE_FILE);
        assertEquals(invoiceToUpdate, updatedInvoice);
    }

    @Test
    void shouldReturnAllInvoices() throws IOException, DatabaseOperationException {
        //Given
        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        Invoice invoice2 = InvoiceGenerator.getRandomInvoiceWithSpecificId(2L);
        List<Invoice> expected = Arrays.asList(invoice1, invoice2);
        //When
        doReturn(List.of(objectMapper.writeValueAsString(invoice1), objectMapper.writeValueAsString(invoice2))).when(fileHelper).readLines(DATABASE_FILE);
        //Then
        assertEquals(expected, inFileDatabase.getAll());
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnInvoiceById() throws DatabaseOperationException, IOException {
        //When
        doReturn(List.of(objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice()), objectMapper.writeValueAsString(InvoiceGenerator.getRandomInvoiceWithSpecificId(1L)))).when(fileHelper).readLines(DATABASE_FILE);
        Optional<Invoice> optionalInvoice = inFileDatabase.getById(1L);
        //Then
        assertTrue(optionalInvoice.isPresent());
        assertEquals(1L, optionalInvoice.get().getId());
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnInvoiceByNumber() throws DatabaseOperationException, IOException {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        //When
        doReturn(List.of(objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice()), objectMapper.writeValueAsString(invoiceToGet))).when(fileHelper).readLines(DATABASE_FILE);
        Optional<Invoice> optionalInvoice = inFileDatabase.getByNumber(invoiceToGet.getNumber());
        //Then
        assertTrue(optionalInvoice.isPresent());
        assertEquals(invoiceToGet, optionalInvoice.get());
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldDeleteAllInvoices() throws DatabaseOperationException, IOException {
        //When
        doNothing().when(fileHelper).clear(DATABASE_FILE);
        inFileDatabase.deleteAll();
        //Then
        verify(fileHelper).clear(DATABASE_FILE);
    }

    @Test
    void shouldReturnNumberOfInvoices() throws IOException, DatabaseOperationException {
        //When
        doReturn(List.of(objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice()), objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice()))).when(fileHelper).readLines(DATABASE_FILE);
        //Then
        assertEquals(2, inFileDatabase.count());
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnFalseForNonExistingInvoice() throws IOException, DatabaseOperationException {
        //Given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        //When
        doReturn(List.of(objectMapper.writeValueAsString(invoice))).when(fileHelper).readLines(DATABASE_FILE);
        //Then
        assertFalse(inFileDatabase.exists(invoice.getId() + 1L));
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnTrueForExistingInvoice() throws IOException, DatabaseOperationException {
        //Given
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        //When
        doReturn(List.of(objectMapper.writeValueAsString(invoice))).when(fileHelper).readLines(DATABASE_FILE);
        inFileDatabase.save(invoice);
        boolean result = inFileDatabase.exists(invoice.getId());
        //Then
        assertTrue(result);
        verify(fileHelper, times(4)).readLines(DATABASE_FILE);
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException, IOException {
        //Given
        Invoice invoiceToDelete = InvoiceGenerator.generateRandomInvoice();
        //When
        doReturn(List.of(objectMapper.writeValueAsString(invoiceToDelete))).when(fileHelper).readLines(DATABASE_FILE);
        doNothing().when(fileHelper).removeLine(DATABASE_FILE, 1);
        inFileDatabase.delete(invoiceToDelete.getId());
        //Then
        verify(fileHelper, times(2)).readLines(DATABASE_FILE);
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
        //When
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
        Invoice expected = Invoice.builder().withId(2L).withInvoice(invoiceToAdd).build();
        //When
        doReturn(List.of(objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice()))).when(fileHelper).readLines(DATABASE_FILE);
        doThrow(IOException.class).when(fileHelper).writeLine(DATABASE_FILE, objectMapper.writeValueAsString(expected));
        //Then
        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.save(invoiceToAdd));
        verify(fileHelper).readLines(DATABASE_FILE);
        verify(fileHelper).writeLine(DATABASE_FILE, objectMapper.writeValueAsString(expected));
    }

    @Test
    void saveMethodShouldThrowExceptionWhenFileHelpersReplaceLineMethodThrowsException() throws IOException {
        //Given
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        //When
        doReturn(List.of(objectMapper.writeValueAsString(invoiceToAdd))).when(fileHelper).readLines(DATABASE_FILE);
        doThrow(IOException.class).when(fileHelper).replaceLine(DATABASE_FILE, 1, objectMapper.writeValueAsString(invoiceToAdd));
        //Then
        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.save(invoiceToAdd));
        verify(fileHelper, times(3)).readLines(DATABASE_FILE);
        verify(fileHelper).replaceLine(DATABASE_FILE, 1, objectMapper.writeValueAsString(invoiceToAdd));
    }
}
