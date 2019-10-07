package pl.coderstrust.database.mongo;

import java.math.BigDecimal;
import java.util.Objects;
import org.springframework.data.annotation.PersistenceConstructor;

public final class MongoInvoiceEntry {

    private final String description;
    private final Long quantity;
    private final BigDecimal price;
    private final BigDecimal netValue;
    private final BigDecimal grossValue;
    private final MongoVat vatRate;

    @PersistenceConstructor
    private MongoInvoiceEntry(String description, Long quantity, BigDecimal price, BigDecimal netValue, BigDecimal grossValue, MongoVat vatRate) {
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.netValue = netValue;
        this.grossValue = grossValue;
        this.vatRate = vatRate;
    }

    private MongoInvoiceEntry(Builder builder) {
        description = builder.description;
        quantity = builder.quantity;
        price = builder.price;
        netValue = builder.netValue;
        grossValue = builder.grossValue;
        vatRate = builder.vatRate;
    }

    public static MongoInvoiceEntry.Builder builder() {
        return new MongoInvoiceEntry.Builder();
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

    public MongoVat getVatRate() {
        return vatRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MongoInvoiceEntry that = (MongoInvoiceEntry) o;
        return Objects.equals(description, that.description)
            && Objects.equals(quantity, that.quantity)
            && Objects.equals(price, that.price)
            && Objects.equals(netValue, that.netValue)
            && Objects.equals(grossValue, that.grossValue)
            && Objects.equals(vatRate, that.vatRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, quantity, price, netValue, grossValue, vatRate);
    }

    @Override
    public String toString() {
        return "MongoInvoiceEntry{"
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
        private MongoVat vatRate;

        public MongoInvoiceEntry.Builder withId(Long id) {
            return this;
        }

        public MongoInvoiceEntry.Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public MongoInvoiceEntry.Builder withQuantity(Long quantity) {
            this.quantity = quantity;
            return this;
        }

        public MongoInvoiceEntry.Builder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public MongoInvoiceEntry.Builder withNetValue(BigDecimal netValue) {
            this.netValue = netValue;
            return this;
        }

        public MongoInvoiceEntry.Builder withGrossValue(BigDecimal grossValue) {
            this.grossValue = grossValue;
            return this;
        }

        public MongoInvoiceEntry.Builder withVatRate(MongoVat vatRate) {
            this.vatRate = vatRate;
            return this;
        }

        public MongoInvoiceEntry build() {
            return new MongoInvoiceEntry(this);
        }
    }
}
