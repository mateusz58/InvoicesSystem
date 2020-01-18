package pl.coderstrust.database.mongo;

import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.PersistenceConstructor;

@Data
@Builder(builderClassName = "InvoiceEntryBuilder", toBuilder = true)
@NoArgsConstructor
public  class InvoiceEntry{

    private String description;
    private Long quantity;
    private BigDecimal price;
    private BigDecimal netValue;
    private BigDecimal grossValue;
    private Vat vatRate;

    @PersistenceConstructor
    private InvoiceEntry(String description, Long quantity, BigDecimal price, BigDecimal netValue, BigDecimal grossValue, Vat vatRate) {
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.netValue = netValue;
        this.grossValue = grossValue;
        this.vatRate = vatRate;
    }
}