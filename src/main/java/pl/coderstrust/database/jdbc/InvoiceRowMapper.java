package pl.coderstrust.database.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;

public class InvoiceRowMapper implements RowMapper<Invoice> {

    @Override
    public Invoice mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Invoice
            .builder()
            .withId(rs.getLong("ID"))
            .withSeller(getCompanySeller(rs))
            .withBuyer(getCompanyBuyer(rs))
            .withNumber(rs.getString("number"))
            .withIssuedDate(rs.getDate("issued_date").toLocalDate())
            .withDueDate(rs.getDate("due_date").toLocalDate())
            .build();
    }

    private Company getCompanySeller(ResultSet rs) throws SQLException {
        return Company.builder()
            .withId(rs.getLong("seller_id"))
            .withEmail(rs.getString("seller_email"))
            .withAddress(rs.getString("seller_address"))
            .withAccountNumber(String.valueOf(rs.getString("seller_account_number")))
            .withName(rs.getString("seller_name"))
            .withPhoneNumber(rs.getString("seller_phone_number"))
            .withTaxId(rs.getString("seller_tax_id"))
            .build();
    }

    private Company getCompanyBuyer(ResultSet rs) throws SQLException {
        return Company.builder()
            .withId(rs.getLong("buyer_id"))
            .withEmail(rs.getString("buyer_email"))
            .withAddress(rs.getString("buyer_address"))
            .withAccountNumber(String.valueOf(rs.getString("buyer_account_number")))
            .withName(rs.getString("buyer_name"))
            .withPhoneNumber(rs.getString("buyer_phone_number"))
            .withTaxId(rs.getString("buyer_tax_id"))
            .build();
    }
}
