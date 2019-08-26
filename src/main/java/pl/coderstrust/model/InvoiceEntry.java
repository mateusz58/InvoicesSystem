package pl.coderstrust.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public final class InvoiceEntry {

    private Long id;
    private String description;
    private Long quantity;
    private BigDecimal price;
    private BigDecimal netValue;
    private BigDecimal grossValue;
    private Vat vatRate;

    public static class Builder {

        private Long id;
        private String description;
        private Long quantity;
        private BigDecimal price;
        private BigDecimal netValue;
        private BigDecimal grossValue;
        private Vat vatRate;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder number(String description) {
            this.description = description;
            return this;
        }

        public Builder quantity(Long quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder netValue(BigDecimal netValue) {
            this.netValue = netValue;
            return this;
        }

        public Builder grossValue(BigDecimal grossValue) {
            this.grossValue = grossValue;
            return this;
        }

        public Builder vatRate(Vat vatRate) {
            this.vatRate = vatRate;
            return this;
        }

        public InvoiceEntry build() {
            InvoiceEntry invoiceEntry = new InvoiceEntry();
            invoiceEntry.id = this.id;
            invoiceEntry.description = this.description;
            invoiceEntry.quantity = this.quantity;
            invoiceEntry.price = this.price;
            invoiceEntry.netValue = this.netValue;
            invoiceEntry.grossValue = this.grossValue;
            invoiceEntry.vatRate = this.vatRate;
            return invoiceEntry;
        }
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InvoiceEntry that = (InvoiceEntry) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(description, that.description) &&
                Objects.equals(quantity, that.quantity) &&
                Objects.equals(price, that.price) &&
                Objects.equals(netValue, that.netValue) &&
                Objects.equals(grossValue, that.grossValue) &&
                Objects.equals(vatRate, that.vatRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, quantity, price, netValue, grossValue, vatRate);
    }

    @Override
    public String toString() {
        return "InvoiceEntry{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", netValue=" + netValue +
                ", grossValue=" + grossValue +
                ", vatRate=" + vatRate +
                '}';
    }
}
