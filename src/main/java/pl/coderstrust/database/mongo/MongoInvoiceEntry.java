package pl.coderstrust.database.mongo;

import java.math.BigDecimal;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class MongoInvoiceEntry {

    @Id
    private final Long id;

    private final String description;

    private final Long quantity;

    private final BigDecimal price;

    private final BigDecimal netValue;

    private final BigDecimal grossValue;

    private final MongoVat vatRate;

    private MongoInvoiceEntry() {
        id = null;
        description = null;
        quantity = null;
        price = null;
        netValue = null;
        grossValue = null;
        vatRate = null;
    }
    private MongoInvoiceEntry(MongoInvoiceEntry.Builder builder) {
        id = builder.id;
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
        return Objects.equals(id, that.id)
                && Objects.equals(description, that.description)
                && Objects.equals(quantity, that.quantity)
                && Objects.equals(price, that.price)
                && Objects.equals(netValue, that.netValue)
                && Objects.equals(grossValue, that.grossValue)
                && Objects.equals(vatRate, that.vatRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, quantity, price, netValue, grossValue, vatRate);
    }

    @Override
    public String toString() {
        return "MongoInvoiceEntry{"
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
        private MongoVat vatRate;

        public MongoInvoiceEntry.Builder withId(Long id) {
            this.id = id;
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
