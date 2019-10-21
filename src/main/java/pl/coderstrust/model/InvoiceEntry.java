package pl.coderstrust.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Objects;

@JsonDeserialize(builder = InvoiceEntry.Builder.class)
@ApiModel(value = "Invoice Entry", description = "Name, quantity and values of sold product")
public final class InvoiceEntry {

    @ApiModelProperty(value = "The unique identifier of the Invoice Entry", position = -1, dataType = "Long")
    private final Long id;
    @ApiModelProperty(value = "Description of what is sold", example = "Siatka ogrodzeniowa")
    private final String description;
    @ApiModelProperty(value = "How much is sold", example = "5")
    private final Long quantity;
    @ApiModelProperty(value = "Value per unit", example = "123")
    private final BigDecimal price;
    @ApiModelProperty(value = "Total value before taxation", example = "500")
    private final BigDecimal netValue;
    @ApiModelProperty(value = "Total value after taxation", example = "615")
    private final BigDecimal grossValue;
    @ApiModelProperty(value = "Percentage level of taxation", example = "VAT_23")
    private final Vat vatRate;

    private InvoiceEntry(Builder builder) {

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
    public int hashCode() {
        return Objects.hash(id, description, quantity, price, netValue, grossValue, vatRate);
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

    @JsonPOJOBuilder
    public static class Builder {

        private Long id;
        private String description;
        private Long quantity;
        private BigDecimal price;
        private BigDecimal netValue;
        private BigDecimal grossValue;
        private Vat vatRate;

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withQuantity(Long quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder withNetValue(BigDecimal netValue) {
            this.netValue = netValue;
            return this;
        }

        public Builder withGrossValue(BigDecimal grossValue) {
            this.grossValue = grossValue;
            return this;
        }

        public Builder withVatRate(Vat vatRate) {
            this.vatRate = vatRate;
            return this;
        }

        public InvoiceEntry build() {
            return new InvoiceEntry(this);
        }
    }
}
