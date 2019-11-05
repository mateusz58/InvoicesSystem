package pl.coderstrust.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderstrust.config.TestDataBaseConfiguration;
import pl.coderstrust.generators.CompanyGenerator;
import pl.coderstrust.generators.InvoiceEntryGenerator;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;

@SpringBootTest(classes = TestDataBaseConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({SpringExtension.class})
public class SqlDatabaseIT {

    private static final String ENCODING = "UTF-8";
    private final String CREATE_TABLE = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/CREATE-TABLES.sql"), ENCODING);
    private final String DROP_TABLE = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/DROP-ALL-TABLES.sql"), ENCODING);
    private final String TRIGGERS = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/TRIGGERS.sql"), ENCODING);
    private final String DROP_TRIGGERS = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/DROP-TRIGGERS.sql"), ENCODING);
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    SqlDatabase sqlDatabase;
    Random random;
    Invoice testedInvoice;

    Collection<Invoice> listOfInvoicesAddedToDatabase;

    public SqlDatabaseIT() throws IOException {
    }

    @BeforeEach
    void prepareDatabaseForTest() throws IOException {
        jdbcTemplate.execute(CREATE_TABLE);
        jdbcTemplate.execute(TRIGGERS);

        random = new Random();
        listOfInvoicesAddedToDatabase = new ArrayList<>();
        List<InvoiceEntry> listOfInvoiceEntries = new ArrayList<>();
        SimpleJdbcInsert simpleJdbcInsertCompany = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
            .withTableName("company")
            .usingGeneratedKeyColumns("id");
        SimpleJdbcInsert simpleJdbcInsertInvoice = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
            .withTableName("invoice")
            .usingGeneratedKeyColumns("id");
        SimpleJdbcInsert simpleJdbcInsertInvoiceEntry = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
            .withTableName("invoice_entry")
            .usingGeneratedKeyColumns("id");
        SimpleJdbcInsert simpleJdbcInsertInvoiceEntries = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
            .withTableName("invoice_entries");
        for (int i = 0; i < 5; i++) {
            Invoice generatedInvoice = InvoiceGenerator.generateRandomInvoice();
            Number sellerKey = simpleJdbcInsertCompany.executeAndReturnKey(mapCompany(generatedInvoice.getSeller()));
            Number buyerKey = simpleJdbcInsertCompany.executeAndReturnKey(mapCompany(generatedInvoice.getBuyer()));
            Number invoiceKey = simpleJdbcInsertInvoice.executeAndReturnKey(mapInvoice(generatedInvoice, buyerKey, sellerKey));
            for (int j = 0; j < generatedInvoice.getEntries().size(); j++) {
                Number invoiceKeyEntry = simpleJdbcInsertInvoiceEntry.executeAndReturnKey(mapInvoiceEntry(generatedInvoice.getEntries().get(j)));
                simpleJdbcInsertInvoiceEntries.execute(mapInvoiceEntries(invoiceKeyEntry, invoiceKey));
                listOfInvoiceEntries.add(buildInvoiceEntry(invoiceKeyEntry.longValue(), generatedInvoice.getEntries().get(j)));
            }
            listOfInvoicesAddedToDatabase.add(buildInvoice(invoiceKey.longValue(), generatedInvoice, buildCompany(buyerKey.longValue(), generatedInvoice.getBuyer()), buildCompany(sellerKey.longValue(), generatedInvoice.getSeller()), List.copyOf(listOfInvoiceEntries)));
            listOfInvoiceEntries.clear();
        }
        testedInvoice = listOfInvoicesAddedToDatabase.stream().skip(random.nextInt(listOfInvoicesAddedToDatabase.size())).findFirst().get();
    }

    @AfterEach
    void finish() {
        jdbcTemplate.execute(DROP_TRIGGERS);
        jdbcTemplate.execute(DROP_TABLE);
    }

    @Test
    void constructorShouldThrowExceptionForNullJdbcTemplate() {
        assertThrows(IllegalArgumentException.class, () -> new SqlDatabase(null));
    }

    @Test
    void saveMethodShouldReturnUpdatedInvoiceWhenGivenInvoiceExistInDataBase() throws DatabaseOperationException {
        //Given
        Invoice expected = InvoiceGenerator.getRandomInvoiceWithSpecificId(testedInvoice.getId());
        //When
        Invoice actual = sqlDatabase.save(expected);
        //Then
        assertEquals(expected, actual);
    }

    @Test
    void saveMethodShouldReturnAddedInvoiceWhenGivenInvoiceDoesNotExistInDatabase() throws DatabaseOperationException {
        //Given
        Invoice expected = Invoice.builder()
            .withId(6L)
            .withNumber("Number")
            .withBuyer(CompanyGenerator.generateRandomCompanyWithSpecificId(11L))
            .withSeller(CompanyGenerator.generateRandomCompanyWithSpecificId(12L))
            .withIssuedDate(LocalDate.now().minusDays(10))
            .withDueDate(LocalDate.now().plusDays(10))
            .withEntries(List.of(InvoiceEntryGenerator.getRandomEntryWithSpecificId(27L)))
            .build();

        //When
        Invoice actual = sqlDatabase.save(expected);

        //Then
        assertEquals(expected, actual);
    }

    @Test
    void saveMethodShouldThrowDatabaseOperationExceptionWhenTryingToAddInvoiceWithEqualOrGreaterIssuedDateValueToDueDateValue() {
        //Given
        Invoice givenInvoice = InvoiceGenerator.generateRandomInvoiceWithGreaterIssuedDate();

        //Then
        assertThrows(DatabaseOperationException.class, () -> sqlDatabase.save(givenInvoice));
    }

    @Test
    void saveMethodShouldThrowDatabaseOperationExceptionWhenTryingToAddInvoiceWithTheSameBuyerAndSeller() {
        //Given
        Invoice givenInvoice = InvoiceGenerator.generateInvoiceWithTheSameBuyerAndSeller();

        //Then
        assertThrows(DatabaseOperationException.class, () -> sqlDatabase.save(givenInvoice));
    }

    @Test
    void saveMethodShouldThrowExceptionForNullInvoice() {
        Exception e = assertThrows(Exception.class, () -> sqlDatabase.save(null));
        assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }

    @Test
    void deleteMethodShouldDeleteInvoice() throws DatabaseOperationException {
        //When
        sqlDatabase.delete(testedInvoice.getId());

        //Then
        assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT invoice_id FROM invoice_entries WHERE invoice_id= ?)", new Object[] {testedInvoice.getId()}, Boolean.class));
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        Exception e = assertThrows(Exception.class, () -> sqlDatabase.delete(null));
        assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }

    @Test
    void deleteMethodShouldThrowExceptionForDeletingNotExistingInvoice() {
        assertThrows(DatabaseOperationException.class, () -> sqlDatabase.delete(listOfInvoicesAddedToDatabase.size() + 1L));
    }

    @Test
    void getByIdMethodShouldReturnInvoiceById() throws DatabaseOperationException {
        //When
        Optional<Invoice> actualInvoice = sqlDatabase.getById(testedInvoice.getId());

        //Then
        assertEquals(testedInvoice, actualInvoice.get());
    }

    @Test
    void getByIdMethodShouldReturnEmptyOptionalWhenNonExistingInvoiceIsGotById() throws DatabaseOperationException {
        //When
        Optional<Invoice> actualInvoice = sqlDatabase.getById(listOfInvoicesAddedToDatabase.size() + 1L);

        //Then
        assertTrue(actualInvoice.isEmpty());
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        Exception e = assertThrows(Exception.class, () -> sqlDatabase.getById(null));
        assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }

    @Test
    void getByNumberMethodShouldReturnInvoiceByNumber() throws DatabaseOperationException {
        //When
        Optional<Invoice> actualInvoice = sqlDatabase.getByNumber(testedInvoice.getNumber());

        //Then
        assertEquals(testedInvoice, actualInvoice.get());
    }

    @Test
    void getByNumberMethodShouldReturnEmptyOptionalWhenNonExistingInvoiceIsGotByNumber() throws DatabaseOperationException {
        //When
        Optional<Invoice> actualInvoice = sqlDatabase.getByNumber("No existent number");

        //Then
        assertTrue(actualInvoice.isEmpty());
    }

    @Test
    void getByNumberMethodShouldThrowExceptionForNullId() {
        Exception e = assertThrows(Exception.class, () -> sqlDatabase.getByNumber(null));

        assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }

    @Test
    void getAllMethodShouldReturnAllInvoices() throws DatabaseOperationException {
        //Given
        Collection<Invoice> expectedListOfInvoices = listOfInvoicesAddedToDatabase;

        //When
        Collection<Invoice> actualListOfInvoices = sqlDatabase.getAll();

        //Then
        assertEquals(expectedListOfInvoices, actualListOfInvoices);
    }

    @Test
    void deleteAllMethodShouldDeleteAllInvoices() throws DatabaseOperationException {
        //When
        sqlDatabase.deleteAll();

        //Then
        assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT *FROM invoice,invoice_entry,invoice_entries,company)", Boolean.class));
    }

    @Test
    void existsMethodShouldReturnTrueForExistingInvoice() throws DatabaseOperationException {
        assertTrue(sqlDatabase.exists(testedInvoice.getId()));
    }

    @Test
    void existsMethodShouldReturnFalseForNotExistingInvoice() throws DatabaseOperationException {
        assertFalse(sqlDatabase.exists(Long.valueOf(listOfInvoicesAddedToDatabase.size() + 1)));
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() {
        Exception e = assertThrows(Exception.class, () -> sqlDatabase.exists(null));
        assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }

    @Test
    void countMethodShouldReturnNumberOfInvoices() throws DatabaseOperationException {
        //Given
        long expected = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM INVOICE",Long.class);

        //When
        long actual = sqlDatabase.count();

        //Then
        assertEquals(expected, actual);
    }

    private Invoice buildInvoice(long invoiceId, Invoice invoice, Company buyer, Company seller, List<InvoiceEntry> invoiceEntries) {
        return Invoice.builder()
            .withId(invoiceId)
            .withEntries(invoiceEntries)
            .withNumber(invoice.getNumber())
            .withBuyer(buyer)
            .withSeller(seller)
            .withDueDate(invoice.getDueDate())
            .withIssuedDate(invoice.getIssuedDate())
            .build();
    }

    private Company buildCompany(long id, Company company) {
        return Company.builder()
            .withId(id)
            .withAccountNumber(company.getAccountNumber())
            .withAddress(company.getAddress())
            .withEmail(company.getEmail())
            .withName(company.getName())
            .withPhoneNumber(company.getPhoneNumber())
            .withTaxId(company.getTaxId())
            .build();
    }

    private InvoiceEntry buildInvoiceEntry(Long id, InvoiceEntry invoiceEntry) {
        return InvoiceEntry.builder()
            .withId(id)
            .withDescription(invoiceEntry.getDescription())
            .withGrossValue(invoiceEntry.getGrossValue())
            .withNetValue(invoiceEntry.getNetValue())
            .withPrice(invoiceEntry.getPrice())
            .withQuantity(invoiceEntry.getQuantity())
            .withVatRate(invoiceEntry.getVatRate())
            .build();
    }

    private Map mapCompany(Company company) {
        return Map.of(
            "account_number", company.getAccountNumber(),
            "address", company.getAddress(),
            "email", company.getEmail(),
            "name", company.getName(),
            "phone_number", company.getPhoneNumber(),
            "tax_id", company.getTaxId());
    }

    private Map mapInvoice(Invoice invoice, Number buyerKey, Number sellerKey) {
        return Map.of(
            "due_date", invoice.getDueDate(),
            "issued_date", invoice.getIssuedDate(),
            "number", invoice.getNumber(),
            "buyer_id", buyerKey,
            "seller_id", sellerKey);
    }

    private Map mapInvoiceEntry(InvoiceEntry invoiceEntry) {
        return Map.of(
            "description", invoiceEntry.getDescription(),
            "gross_value", invoiceEntry.getGrossValue(),
            "net_value", invoiceEntry.getNetValue(),
            "price", invoiceEntry.getPrice(),
            "quantity", invoiceEntry.getQuantity(),
            "vat_rate", invoiceEntry.getVatRate().getValue());
    }

    private Map mapInvoiceEntries(Number invoiceKeyEntry, Number invoiceKey) {
        return Map.of("invoice_id", invoiceKey,
            "entries_id", invoiceKeyEntry);
    }
}
