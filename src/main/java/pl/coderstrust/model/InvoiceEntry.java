package pl.coderstrust.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class InvoiceEntry {

    private Long id;
    private String description;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal netValue;
    private BigDecimal grossValue;
    private BigDecimal vatRate;

    public InvoiceEntry(Long id, String description, BigDecimal quantity, BigDecimal price, BigDecimal netValue, BigDecimal grossValue, BigDecimal vatRate) {
        this.id = id;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.netValue = netValue;
        this.grossValue = grossValue;
        this.vatRate = vatRate;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getQuantity() {
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

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setNetValue(BigDecimal netValue) {
        this.netValue = netValue;
    }

    public void setGrossValue(BigDecimal grossValue) {
        this.grossValue = grossValue;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
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
