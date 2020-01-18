package pl.coderstrust.database;

import static pl.coderstrust.database.jdbc.SqlQueries.COUNT_INVOICES;
import static pl.coderstrust.database.jdbc.SqlQueries.DELETE_ALL_DATA;
import static pl.coderstrust.database.jdbc.SqlQueries.DELETE_INVOICE_BY_ID;
import static pl.coderstrust.database.jdbc.SqlQueries.EXISTS_COMPANY;
import static pl.coderstrust.database.jdbc.SqlQueries.EXISTS_INVOICE;
import static pl.coderstrust.database.jdbc.SqlQueries.EXISTS_INVOICE_ENTRY;
import static pl.coderstrust.database.jdbc.SqlQueries.GET_ALL_INVOICES;
import static pl.coderstrust.database.jdbc.SqlQueries.GET_INVOICE_BY_ID;
import static pl.coderstrust.database.jdbc.SqlQueries.GET_INVOICE_BY_NUMBER;
import static pl.coderstrust.database.jdbc.SqlQueries.GET_INVOICE_ENTRIES;
import static pl.coderstrust.database.jdbc.SqlQueries.INSERT_COMPANY;
import static pl.coderstrust.database.jdbc.SqlQueries.INSERT_INVOICE;
import static pl.coderstrust.database.jdbc.SqlQueries.INSERT_INVOICE_ENTRIES;
import static pl.coderstrust.database.jdbc.SqlQueries.INSERT_INVOICE_ENTRY;
import static pl.coderstrust.database.jdbc.SqlQueries.UPDATE_INVOICE;
import static pl.coderstrust.database.jdbc.SqlQueries.UPDATE_INVOICE_ENTRY;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pl.coderstrust.database.jdbc.InvoiceEntriesRowMapper;
import pl.coderstrust.database.jdbc.InvoiceRowMapper;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "jdbc")
public class SqlDatabase implements Database {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SqlDatabase(JdbcTemplate jdbcTemplate) throws IOException {
        if (jdbcTemplate == null) {
            throw new IllegalArgumentException("JDBC template cannot be null.");
        }
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public synchronized Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("invoice cannot be null.");
        }
        try {
            if (exists(invoice.getId())) {
                return updateInvoice(invoice);
            }
            return insertInvoice(invoice);
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occured during adding invoice to database");
        }
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            int result = jdbcTemplate.update(DELETE_INVOICE_BY_ID, id, id);
            if (result == 0) {
                throw new DatabaseOperationException(String.format("There is no invoice  id: %s", id));
            }
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("An error occured during deleting invoice by Id");
        }
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return jdbcTemplate.query(GET_INVOICE_BY_ID, new Object[] {id}, new InvoiceRowMapper()).stream().map(i -> buildInvoice(i, getInvoiceEntries(i.getId()))).findFirst();
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("An error occured during getting invoice by Id");
        }
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null.");
        }
        try {
            return jdbcTemplate.query(GET_INVOICE_BY_NUMBER, new Object[] {number}, new InvoiceRowMapper()).stream().map(i -> buildInvoice(i, getInvoiceEntries(i.getId()))).findFirst();
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("An error occured during getting invoice by Number");
        }
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            return jdbcTemplate.query(GET_ALL_INVOICES, new InvoiceRowMapper()).stream().map(i -> buildInvoice(i, getInvoiceEntries(i.getId()))).collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("An error occured during getting all invoices from database");
        }
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {
        try {
            jdbcTemplate.execute(DELETE_ALL_DATA);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("An error occured during deleting all invoices from database");
        }
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return jdbcTemplate.queryForObject(EXISTS_INVOICE, new Object[] {id}, Boolean.class);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("An error occured during checking if invoice exists");
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return jdbcTemplate.queryForObject(COUNT_INVOICES, Integer.class);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("An error occured during counting all invoices");
        }
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

    private Invoice buildInvoice(Invoice invoice, List<InvoiceEntry> invoiceEntries) {
        return Invoice.builder()
            .id(invoice.getId())
            .entries(invoiceEntries)
            .number(invoice.getNumber())
            .buyer(invoice.getBuyer())
            .seller(invoice.getSeller())
            .dueDate(invoice.getDueDate())
            .issuedDate(invoice.getIssuedDate())
            .build();
    }

    private List<InvoiceEntry> getInvoiceEntries(Long invoiceId) {
        return jdbcTemplate.query(GET_INVOICE_ENTRIES, new Object[] {invoiceId}, new InvoiceEntriesRowMapper());
    }

    private Invoice insertInvoice(Invoice invoice) throws Exception {
        List<InvoiceEntry> invoiceEntries = insertAllInvoiceEntries(invoice.getEntries());
        if (! companyExists(invoice.getBuyer().getId())) {
            if (! companyExists(invoice.getSeller().getId())) {
                return insertInvoiceTable(invoice, insertCompanyTable(invoice.getBuyer()), insertCompanyTable(invoice.getSeller()), invoiceEntries);
            }
            return insertInvoiceTable(invoice, insertCompanyTable(invoice.getBuyer()), invoice.getSeller(), invoiceEntries);
        }
        if (! companyExists(invoice.getSeller().getId())) {
            if (! companyExists(invoice.getBuyer().getId())) {
                return insertInvoiceTable(invoice, insertCompanyTable(invoice.getBuyer()), insertCompanyTable(invoice.getSeller()), invoiceEntries);
            }
            return insertInvoiceTable(invoice, invoice.getBuyer(), insertCompanyTable(invoice.getSeller()), invoiceEntries);
        }
        return insertInvoiceTable(invoice, invoice.getBuyer(), invoice.getSeller(), invoice.getEntries());
    }

    private Invoice updateInvoice(Invoice invoice) throws DatabaseOperationException {
        List<InvoiceEntry> invoiceEntries = updateAllInvoiceEntries(invoice.getEntries());
        if (! companyExists(invoice.getBuyer().getId())) {
            if (! companyExists(invoice.getSeller().getId())) {
                return updateInvoiceTable(invoice, insertCompanyTable(invoice.getBuyer()), insertCompanyTable(invoice.getSeller()), invoiceEntries);
            }
            return updateInvoiceTable(invoice, insertCompanyTable(invoice.getBuyer()), invoice.getSeller(), invoiceEntries);
        }
        if (! companyExists(invoice.getSeller().getId())) {
            if (! companyExists(invoice.getBuyer().getId())) {
                return updateInvoiceTable(invoice, insertCompanyTable(invoice.getBuyer()), insertCompanyTable(invoice.getSeller()), invoiceEntries);
            }
            return updateInvoiceTable(invoice, invoice.getBuyer(), insertCompanyTable(invoice.getSeller()), invoiceEntries);
        }
        return updateInvoiceTable(invoice, invoice.getBuyer(), invoice.getSeller(), invoice.getEntries());
    }

    private boolean companyExists(Long id) throws DatabaseOperationException {
        return jdbcTemplate.queryForObject(EXISTS_COMPANY, new Object[] {id}, Boolean.class);
    }

    private boolean invoiceEntryExists(Long id) throws DatabaseOperationException {
        return jdbcTemplate.queryForObject(EXISTS_INVOICE_ENTRY, new Object[] {id}, Boolean.class);
    }

    private Company insertCompanyTable(Company company) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_COMPANY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, company.getAccountNumber());
            ps.setString(2, company.getAddress());
            ps.setString(3, company.getEmail());
            ps.setString(4, company.getName());
            ps.setString(5, company.getPhoneNumber());
            ps.setString(6, company.getTaxId());
            return ps;
        }, holder);
        return buildCompany(company, Long.valueOf(holder.getKeys().get("id").toString()));
    }

    private InvoiceEntry insertInvoiceEntryTable(InvoiceEntry invoiceEntry) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_INVOICE_ENTRY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, invoiceEntry.getDescription());
            ps.setBigDecimal(2, invoiceEntry.getGrossValue());
            ps.setBigDecimal(3, invoiceEntry.getNetValue());
            ps.setBigDecimal(4, invoiceEntry.getPrice());
            ps.setLong(5, invoiceEntry.getQuantity());
            ps.setFloat(6, invoiceEntry.getVatRate().getValue());
            return ps;
        }, holder);
        return buildInvoiceEntry(invoiceEntry, Long.valueOf(holder.getKeys().get("id").toString()));
    }

    private InvoiceEntry updateInvoiceEntryTable(InvoiceEntry invoiceEntry) {
        jdbcTemplate.update(UPDATE_INVOICE_ENTRY,
            invoiceEntry.getDescription(), invoiceEntry.getGrossValue(), invoiceEntry.getNetValue(), invoiceEntry.getPrice(), invoiceEntry.getQuantity(), invoiceEntry.getId());
        return invoiceEntry;
    }

    private void insertInvoiceEntriesTable(Long invoiceId, List<InvoiceEntry> invoiceEntries) {
        for (int i = 0; i < invoiceEntries.size(); i++) {
            jdbcTemplate.update(
                INSERT_INVOICE_ENTRIES,
                invoiceId, invoiceEntries.get(i).getId());
        }
    }

    private Invoice insertInvoiceTable(Invoice invoice, Company buyer, Company seller, List<InvoiceEntry> invoiceEntries) throws Exception {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_INVOICE, Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1, Date.valueOf(invoice.getDueDate()));
            ps.setDate(2, Date.valueOf(invoice.getIssuedDate()));
            ps.setString(3, invoice.getNumber());
            ps.setLong(4, buyer.getId());
            ps.setLong(5, seller.getId());
            return ps;
        }, holder);
        insertInvoiceEntriesTable(Long.valueOf(holder.getKeys().get("id").toString()), invoiceEntries);
        return buildInvoice(Long.valueOf(holder.getKeys().get("id").toString()), invoice, buyer, seller, invoiceEntries);
    }

    private Invoice updateInvoiceTable(Invoice invoice, Company buyer, Company seller, List<InvoiceEntry> invoiceEntries) {
        jdbcTemplate.update(UPDATE_INVOICE,
            invoice.getDueDate(), invoice.getIssuedDate(), invoice.getNumber(), buyer.getId(), seller.getId(), invoice.getId());
        insertInvoiceEntriesTable(invoice.getId(), invoiceEntries);
        return Invoice.builder()
            .id(invoice.getId())
            .number(invoice.getNumber())
            .dueDate(invoice.getDueDate())
            .issuedDate(invoice.getIssuedDate())
            .buyer(invoice.getBuyer())
            .seller(invoice.getSeller())
            .entries(invoice.getEntries())
            .build();
    }

    private List<InvoiceEntry> insertAllInvoiceEntries(List<InvoiceEntry> invoiceEntries) {
        List<InvoiceEntry> entriesToAdd = new ArrayList<>();
        for (int i = 0; i < invoiceEntries.size(); i++) {
            insertInvoiceEntryTable(invoiceEntries.get(i));
            entriesToAdd.add(insertInvoiceEntryTable(invoiceEntries.get(i)));
        }
        return entriesToAdd;
    }

    private List<InvoiceEntry> updateAllInvoiceEntries(List<InvoiceEntry> invoiceEntries) throws DatabaseOperationException {
        List<InvoiceEntry> entriesToAdd = new ArrayList<>();
        for (int i = 0; i < invoiceEntries.size(); i++) {
            if (! invoiceEntryExists(invoiceEntries.get(i).getId())) {
                insertInvoiceEntryTable(invoiceEntries.get(i));
                entriesToAdd.add(insertInvoiceEntryTable(invoiceEntries.get(i)));
            }
        }
        return entriesToAdd;
    }

    private Company buildCompany(Company company, Long id) {
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

    private InvoiceEntry buildInvoiceEntry(InvoiceEntry invoiceEntry, Long id) {
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
}
