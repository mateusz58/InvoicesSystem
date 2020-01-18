package pl.coderstrust.database.hibernate;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderClassName = "InvoiceEntryBuilder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoice_entry")
public class InvoiceEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private Long quantity;

    private BigDecimal price;

    private BigDecimal netValue;

    private BigDecimal grossValue;

    private Vat vatRate;

    public static class InvoiceEntryBuilder {
    }
}