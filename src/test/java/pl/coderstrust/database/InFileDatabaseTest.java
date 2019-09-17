package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
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
import org.springframework.beans.factory.annotation.Autowired;
import pl.coderstrust.configuration.InFileDatabaseProperties;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.helpers.FileHelper;
import pl.coderstrust.model.Invoice;

@ExtendWith(MockitoExtension.class)
class InFileDatabaseTest {

    @Mock
    private FileHelper fileHelper;

    @Autowired
    private InFileDatabase inFileDatabase;

    private static ObjectMapper objectMapper;

   private static final String DATABASE_FILE =  "src/test/resources/database/database.json";

    @BeforeEach
    void setup() throws IOException {
        objectMapper = new ObjectMapper();
        //Test features
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        InFileDatabaseProperties inFileDatabasePropertiesTest = new InFileDatabaseProperties();
        inFileDatabasePropertiesTest.setFilePath(DATABASE_FILE);

        doReturn(false).when(fileHelper).exists(DATABASE_FILE);

        inFileDatabase = new InFileDatabase(inFileDatabasePropertiesTest, objectMapper, fileHelper);
    }

    @Test
    void constructorClassShouldThrowExceptionForNullinFileDatabaseProperties() {
        assertThrows(IllegalArgumentException.class, () -> new InFileDatabase(null,null,null));
    }

    @Test
    void shouldAddInvoiceToFile() throws DatabaseOperationException, IOException {
        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        doNothing().when(fileHelper).writeLine(DATABASE_FILE,objectMapper.writeValueAsString(invoice1));
        doReturn(new ArrayList<>()).when(fileHelper).readLines(DATABASE_FILE);

        Invoice expectedInvoice1 = inFileDatabase.save(invoice1);

        assertEquals(expectedInvoice1, invoice1);
        verify(fileHelper).readLines(DATABASE_FILE);
        verify(fileHelper).writeLine(DATABASE_FILE, objectMapper.writeValueAsString(invoice1));
    }
    //Test work for hard coded object
    @Test
    void shouldUpdateInvoiceToFile__Testing() throws IOException, DatabaseOperationException {
        //TODO Correct
        //Given
        InFileDatabase.invoiceToGenerate=InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        Invoice invoiceToUpdate=InFileDatabase.invoiceToGenerate;
        doNothing().when(fileHelper).replaceLine(DATABASE_FILE, 1,objectMapper.writeValueAsString( invoiceToUpdate));
        doReturn(Collections.singletonList(objectMapper.writeValueAsString( invoiceToUpdate))).when(fileHelper).readLines(DATABASE_FILE);
        //When
        Invoice updatedInvoice = inFileDatabase.save( invoiceToUpdate);
        //Then
        verify(fileHelper).replaceLine(DATABASE_FILE,1,objectMapper.writeValueAsString(updatedInvoice));
        verify(fileHelper, times(3)).readLines(DATABASE_FILE);
        assertEquals(invoiceToUpdate,updatedInvoice);
    }

    //Tested with deserialization
    @Test
    void shouldUpdateInvoiceToFile() throws IOException, DatabaseOperationException {
        //Given
        Invoice invoiceToUpdate=InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        doNothing().when(fileHelper).replaceLine(DATABASE_FILE, 1,objectMapper.writeValueAsString(invoiceToUpdate));
        doReturn(Collections.singletonList(objectMapper.writeValueAsString( invoiceToUpdate))).when(fileHelper).readLines(DATABASE_FILE);
        //When
        Invoice updatedInvoice = inFileDatabase.save(invoiceToUpdate );
        //Then
        verify(fileHelper).replaceLine(DATABASE_FILE,1,objectMapper.writeValueAsString(updatedInvoice));
        verify(fileHelper, times(2)).readLines(DATABASE_FILE);
    }


    @Test
    void shouldReturnAllInvoices() throws IOException, DatabaseOperationException {
        //TODO Correct methodOOO
        Invoice invoice1 = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        Invoice invoice2 = InvoiceGenerator.getRandomInvoiceWithSpecificId(2L);
        List<Invoice> expected = Arrays.asList(invoice1, invoice2);
        doReturn(List.of(objectMapper.writeValueAsString(invoice1), objectMapper.writeValueAsString(invoice2))).when(fileHelper).readLines(DATABASE_FILE);

        Collection<Invoice> result = inFileDatabase.getAll();

        assertEquals(expected, result);
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnInvoiceById() throws DatabaseOperationException, IOException {
        doReturn(List.of(objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice()), objectMapper.writeValueAsString(InvoiceGenerator.getRandomInvoiceWithSpecificId(1L)))).when(fileHelper).readLines(DATABASE_FILE);

        Optional<Invoice> optionalInvoice = inFileDatabase.getById(1L);

        assertTrue(optionalInvoice.isPresent());
        assertEquals(1L, optionalInvoice.get().getId());
        verify(fileHelper).readLines(DATABASE_FILE);
    }


    @Test
    void shouldReturnInvoiceByNumber() throws DatabaseOperationException, IOException {
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice()), objectMapper.writeValueAsString(invoiceToGet))).when(fileHelper).readLines(DATABASE_FILE);

        Optional<Invoice> optionalInvoice = inFileDatabase.getByNumber(invoiceToGet.getNumber());

        assertTrue(optionalInvoice.isPresent());
        assertEquals(invoiceToGet, optionalInvoice.get());
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldDeleteAllInvoices() throws DatabaseOperationException, IOException {
        doNothing().when(fileHelper).clear(DATABASE_FILE);

        inFileDatabase.deleteAll();

        verify(fileHelper).clear(DATABASE_FILE);
    }

    @Test
    void shouldReturnNumberOfInvoices () throws IOException,DatabaseOperationException {
        doReturn(List.of(objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice()), objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice()))).when(fileHelper).readLines(DATABASE_FILE);

        long result = inFileDatabase.count();

        assertEquals(2, result);
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnFalseForNonExistingInvoice() throws IOException, DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(invoice))).when(fileHelper).readLines(DATABASE_FILE);

        boolean result = inFileDatabase.exists(invoice.getId() + 1L);

        assertFalse(result);
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldReturnTrueForExistingInvoice() throws IOException, DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(invoice))).when(fileHelper).readLines(DATABASE_FILE);

        inFileDatabase.save(invoice);
        boolean result = inFileDatabase.exists(invoice.getId());

        assertTrue(result);
        verify(fileHelper).readLines(DATABASE_FILE);
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException, IOException {
        Invoice invoiceToDelete = InvoiceGenerator.generateRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(invoiceToDelete))).when(fileHelper).readLines(DATABASE_FILE);
        doNothing().when(fileHelper).removeLine(DATABASE_FILE, 1);

        inFileDatabase.delete(invoiceToDelete.getId());

        verify(fileHelper).readLines(DATABASE_FILE);
        verify(fileHelper).removeLine(DATABASE_FILE, 1);
    }

    @Test
    void saveMethodShouldThrowExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.save(null));
    }

    @Test
    void deleteMethodShouldThrowExceptionDuringDeletingNotExistingInvoice() throws DatabaseOperationException,IOException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(List.of(objectMapper.writeValueAsString(invoice))).when(fileHelper).readLines(DATABASE_FILE);

        assertThrows(DatabaseOperationException.class, () -> inFileDatabase.delete(invoice.getId() + 1L));
        verify(fileHelper).readLines(DATABASE_FILE);
        verify(fileHelper, never()).removeLine(anyString(), anyInt());
    }
    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.delete(null));
    }

    @Test
    void getByIdShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.getById(null));
    }

    @Test
    void getByNumberShouldThrowExceptionForNullNumber() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.getByNumber(null));
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.exists(null));
    }



}
