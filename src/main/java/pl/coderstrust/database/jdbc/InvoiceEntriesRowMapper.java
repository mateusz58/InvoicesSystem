package pl.coderstrust.database.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

public class InvoiceEntriesRowMapper implements RowMapper<InvoiceEntry> {
    @Override
    public InvoiceEntry mapRow(ResultSet rs, int i) throws SQLException {
        return InvoiceEntry.builder()
            .id(rs.getLong("id"))
            .description(rs.getString("description"))
            .quantity(rs.getLong("quantity"))
            .price(rs.getBigDecimal("price"))
            .netValue(rs.getBigDecimal("net_value"))
            .grossValue(rs.getBigDecimal("gross_value"))
            .vatRate(Vat.getVatType(rs.getFloat("vat_rate")))
            .build();
    }
}
