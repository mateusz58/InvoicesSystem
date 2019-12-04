package pl.coderstrust.database.mongo;

import java.math.BigDecimal;
import java.util.Objects;
import org.springframework.data.annotation.PersistenceConstructor;

public final class InvoiceEntry {

    private final String description;
    private final Long quantity;
    private final BigDecimal price;
    private final BigDecimal netValue;
    private final BigDecimal grossValue;
    private final Vat vatRate;

    @PersistenceConstructor
    private InvoiceEntry(String description, Long quantity, BigDecimal price, BigDecimal netValue, BigDecimal grossValue, Vat vatRate) {
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.netValue = netValue;
        this.grossValue = grossValue;
        this.vatRate = vatRate;
    }

    private InvoiceEntry(Builder builder) {
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
        return Objects.hash(description, quantity, price, netValue, grossValue, vatRate);
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
        return Objects.equals(description, that.description)
            && Objects.equals(quantity, that.quantity)
            && Objects.equals(price, that.price)
            && Objects.equals(netValue, that.netValue)
            && Objects.equals(grossValue, that.grossValue)
            && Objects.equals(vatRate, that.vatRate);
    }

    @Override
    public String toString() {
        return "InvoiceEntry{"
            + ", description='" + description + '\''
            + ", quantity=" + quantity
            + ", price=" + price
            + ", netValue=" + netValue
            + ", grossValue=" + grossValue
            + ", vatRate=" + vatRate
            + '}';
    }

    public static class Builder {

        private String description;
        private Long quantity;
        private BigDecimal price;
        private BigDecimal netValue;
        private BigDecimal grossValue;
        private Vat vatRate;

        public InvoiceEntry.Builder withId(Long id) {
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
