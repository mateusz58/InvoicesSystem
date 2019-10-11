package pl.coderstrust.database.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

public class InvoiceEntriesRowMapper implements RowMapper<InvoiceEntry> {
    @Override
    public InvoiceEntry mapRow(ResultSet rs, int i) throws SQLException {
        return InvoiceEntry.builder()
            .withId(rs.getLong("id"))
            .withDescription(rs.getString("description"))
            .withQuantity(rs.getLong("quantity"))
            .withPrice(rs.getBigDecimal("price"))
            .withNetValue(rs.getBigDecimal("net_value"))
            .withGrossValue(rs.getBigDecimal("gross_value"))
            .withVatRate(Vat.getVatType(rs.getFloat("vat_rate")))
            .build();
    }
}
