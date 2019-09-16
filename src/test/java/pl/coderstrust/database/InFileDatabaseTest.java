package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        InFileDatabaseProperties inFileDatabasePropertiesTest = new InFileDatabaseProperties();
        inFileDatabasePropertiesTest.setFilePath(DATABASE_FILE);

        doReturn(false).when(fileHelper).exists(DATABASE_FILE);

        inFileDatabase = new InFileDatabase(inFileDatabasePropertiesTest, objectMapper, fileHelper);
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
    @Test
    void shouldUpdateInvoiceToFile() throws IOException, DatabaseOperationException {
        //Given
        Invoice invoiceInDatabase = InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        Invoice invoiceToUpdate =  InvoiceGenerator.getRandomInvoiceWithSpecificId(1L);
        doReturn(List.of(objectMapper.writeValueAsString(invoiceInDatabase))).when(fileHelper).readLines(DATABASE_FILE);
        doNothing().when(fileHelper).replaceLine(DATABASE_FILE, 1,objectMapper.writeValueAsString(invoiceToUpdate));
        inFileDatabase.save(invoiceInDatabase);
        //When
        Invoice updatedInvoice = inFileDatabase.save(invoiceToUpdate);
        //Then
        assertEquals(invoiceToUpdate, updatedInvoice);
        verify(fileHelper).replaceLine(DATABASE_FILE,1,objectMapper.writeValueAsString(updatedInvoice));
        verify(fileHelper, times(2)).readLines(DATABASE_FILE);
    }

}
