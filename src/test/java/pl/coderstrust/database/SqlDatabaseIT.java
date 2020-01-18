package pl.coderstrust.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.coderstrust.database.TestingSqlQueries.CREATE_TABLE;
import static pl.coderstrust.database.TestingSqlQueries.DROP_TABLE;
import static pl.coderstrust.database.TestingSqlQueries.DROP_TRIGGERS;
import static pl.coderstrust.database.TestingSqlQueries.TRIGGERS;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.TestPropertySource;
import pl.coderstrust.config.TestDataBaseConfiguration;
import pl.coderstrust.generators.CompanyGenerator;
import pl.coderstrust.generators.InvoiceEntryGenerator;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;


@SpringBootTest(classes = {SqlDatabase.class, TestDataBaseConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-jdbc.properties")
public class SqlDatabaseIT {

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

    private InvoiceEntry buildInvoiceEntry(Long id, InvoiceEntry invoiceEntry) {
        return InvoiceEntry.builder()
            .id(id)
            .description(invoiceEntry.getDescription())
            .grossValue(invoiceEntry.getGrossValue())
            .netValue(invoiceEntry.getNetValue())
            .price(invoiceEntry.getPrice())
            .quantity(invoiceEntry.getQuantity())
            .vatRate(invoiceEntry.getVatRate())
            .build();
    }

    private Invoice buildInvoice(long invoiceId, Invoice invoice, Company buyer, Company seller, List<InvoiceEntry> invoiceEntries) {
        return Invoice.builder()
            .id(invoiceId)
            .entries(invoiceEntries)
            .number(invoice.getNumber())
            .buyer(buyer)
            .seller(seller)
            .dueDate(invoice.getDueDate())
            .issuedDate(invoice.getIssuedDate())
            .build();
    }

    private Company buildCompany(long id, Company company) {
        return Company.builder()
            .id(id)
            .accountNumber(company.getAccountNumber())
            .address(company.getAddress())
            .email(company.getEmail())
            .name(company.getName())
            .phoneNumber(company.getPhoneNumber())
            .taxId(company.getTaxId())
            .build();
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
            .id(6L)
            .number("Number")
            .buyer(CompanyGenerator.generateRandomCompanyWithSpecificId(11L))
            .seller(CompanyGenerator.generateRandomCompanyWithSpecificId(12L))
            .issuedDate(LocalDate.now().minusDays(10))
            .dueDate(LocalDate.now().plusDays(10))
            .entries(List.of(InvoiceEntryGenerator.getRandomEntryWithSpecificId(27L)))
            .build();

        //When
        Invoice actual = sqlDatabase.save(expected);

        //Then
        assertEquals(expected, actual);
    }

    @Test
    void saveMethodShouldThrowDatabaseOperationExceptionWhenTryingToAddInvoiceWithEqualOrGreaterIssuedDateValueToDueDateValue() {
        assertThrows(IllegalArgumentException.class, () -> sqlDatabase.save(null));
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
        assertThrows(IllegalArgumentException.class, () -> sqlDatabase.save(null));
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
        assertThrows(IllegalArgumentException.class, () -> sqlDatabase.delete(null));
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
        assertThrows(IllegalArgumentException.class, () -> sqlDatabase.getById(null));
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
        assertThrows(IllegalArgumentException.class, () -> sqlDatabase.getByNumber(null));
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
        assertThrows(IllegalArgumentException.class, () -> sqlDatabase.exists(null));
    }

    @Test
    void countMethodShouldReturnNumberOfInvoices() throws DatabaseOperationException {
        //Given
        long expected = 5L;

        //When
        long actual = sqlDatabase.count();

        //Then
        assertEquals(expected, actual);
    }
}
