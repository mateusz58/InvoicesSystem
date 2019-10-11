package pl.coderstrust.database.hibernate;

import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "invoice_entry")
public class InvoiceEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    private final String description;

    private final Long quantity;

    private final BigDecimal price;

    private final BigDecimal netValue;

    private final BigDecimal grossValue;

    private final Vat vatRate;

    private InvoiceEntry() {
        id = null;
        description = null;
        quantity = null;
        price = null;
        netValue = null;
        grossValue = null;
        vatRate = null;
    }

    private InvoiceEntry(InvoiceEntry.Builder builder) {
        id = builder.id;
        description = builder.description;
        quantity = builder.quantity;
        price = builder.price;
        netValue = builder.netValue;
        grossValue = builder.grossValue;
        vatRate = builder.vatRate;
    }

    public static InvoiceEntry.Builder builder() {
        return new InvoiceEntry.Builder();
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Long getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getNetValue() {
        return netValue;
    }

    public BigDecimal getGrossValue() {
        return grossValue;
    }

    public Vat getVatRate() {
        return vatRate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, quantity, price, netValue, grossValue, vatRate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InvoiceEntry that = (InvoiceEntry) o;
        return Objects.equals(id, that.id)
            && Objects.equals(description, that.description)
            && Objects.equals(quantity, that.quantity)
            && Objects.equals(price, that.price)
            && Objects.equals(netValue, that.netValue)
            && Objects.equals(grossValue, that.grossValue)
            && Objects.equals(vatRate, that.vatRate);
    }

    @Override
    public String toString() {
        return "InvoiceEntry{"
            + "id=" + id
            + ", description='" + description + '\''
            + ", quantity=" + quantity
            + ", price=" + price
            + ", netValue=" + netValue
            + ", grossValue=" + grossValue
            + ", vatRate=" + vatRate
            + '}';
    }

    public static class Builder {

        private Long id;
        private String description;
        private Long quantity;
        private BigDecimal price;
        private BigDecimal netValue;
        private BigDecimal grossValue;
        private Vat vatRate;

        public InvoiceEntry.Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public InvoiceEntry.Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public InvoiceEntry.Builder withQuantity(Long quantity) {
            this.quantity = quantity;
            return this;
        }

        public InvoiceEntry.Builder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public InvoiceEntry.Builder withNetValue(BigDecimal netValue) {
            this.netValue = netValue;
            return this;
        }

        public InvoiceEntry.Builder withGrossValue(BigDecimal grossValue) {
            this.grossValue = grossValue;
            return this;
        }

        public InvoiceEntry.Builder withVatRate(Vat vatRate) {
            this.vatRate = vatRate;
            return this;
        }

        public InvoiceEntry build() {
            return new InvoiceEntry(this);
        }
    }
}
