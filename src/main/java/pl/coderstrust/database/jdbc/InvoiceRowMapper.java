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
            .id(rs.getLong("ID"))
            .seller(getCompanySeller(rs))
            .buyer(getCompanyBuyer(rs))
            .number(rs.getString("number"))
            .issuedDate(rs.getDate("issued_date").toLocalDate())
            .dueDate(rs.getDate("due_date").toLocalDate())
            .build();
    }

    private Company getCompanySeller(ResultSet rs) throws SQLException {
        return Company.builder()
            .id(rs.getLong("seller_id"))
            .email(rs.getString("seller_email"))
            .address(rs.getString("seller_address"))
            .accountNumber(String.valueOf(rs.getString("seller_account_number")))
            .name(rs.getString("seller_name"))
            .phoneNumber(rs.getString("seller_phone_number"))
            .taxId(rs.getString("seller_tax_id"))
            .build();
    }

    private Company getCompanyBuyer(ResultSet rs) throws SQLException {
        return Company.builder()
            .id(rs.getLong("buyer_id"))
            .email(rs.getString("buyer_email"))
            .address(rs.getString("buyer_address"))
            .accountNumber(String.valueOf(rs.getString("buyer_account_number")))
            .name(rs.getString("buyer_name"))
            .phoneNumber(rs.getString("buyer_phone_number"))
            .taxId(rs.getString("buyer_tax_id"))
            .build();
    }
}
