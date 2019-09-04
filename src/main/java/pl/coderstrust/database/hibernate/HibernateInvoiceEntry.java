package pl.coderstrust.database.hibernate;

import pl.coderstrust.model.Vat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "invoice_entry")
public class HibernateInvoiceEntry {

    //zrobić dodatkowe pole @ManyToOne + dodatkowa kolumna na id Invoicea?

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private final Long id;

    @Column(name = "description")
    private final String description;

    @Column(name = "quantity")
    private final Long quantity;

    @Column(name = "price")
    private final BigDecimal price;

    @Column(name = "net_value")
    private final BigDecimal netValue;

    @Column(name = "gross_value")
    private final BigDecimal grossValue;

    @Column(name = "vat_rate")
    private final Vat vatRate;

    private HibernateInvoiceEntry(HibernateInvoiceEntry.Builder builder) {

        id = builder.id;
        description = builder.description;
        quantity = builder.quantity;
        price = builder.price;
        netValue = builder.netValue;
        grossValue = builder.grossValue;
        vatRate = builder.vatRate;
    }

    public static HibernateInvoice.Builder builder() {
        return new HibernateInvoice.Builder();
    }

    public static class Builder {

        private Long id;
        private String description;
        private Long quantity;
        private BigDecimal price;
        private BigDecimal netValue;
        private BigDecimal grossValue;
        private Vat vatRate;

        public HibernateInvoiceEntry.Builder wthId(Long id) {
            this.id = id;
            return this;
        }

        public HibernateInvoiceEntry.Builder withNumber(String description) {
            this.description = description;
            return this;
        }

        public HibernateInvoiceEntry.Builder withQuantity(Long quantity) {
            this.quantity = quantity;
            return this;
        }

        public HibernateInvoiceEntry.Builder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public HibernateInvoiceEntry.Builder withNetValue(BigDecimal netValue) {
            this.netValue = netValue;
            return this;
        }

        public HibernateInvoiceEntry.Builder withGrossValue(BigDecimal grossValue) {
            this.grossValue = grossValue;
            return this;
        }

        public HibernateInvoiceEntry.Builder withVatRate(Vat vatRate) {
            this.vatRate = vatRate;
            return this;
        }

        public HibernateInvoiceEntry build() {
            return new HibernateInvoiceEntry(this);
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
        HibernateInvoiceEntry that = (HibernateInvoiceEntry) o;
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
}
