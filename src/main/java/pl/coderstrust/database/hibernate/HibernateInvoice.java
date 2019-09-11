package pl.coderstrust.database.hibernate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.InvoiceEntry;

@Entity
@Table(name = "invoice")
public class HibernateInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    private final String number;

    private final LocalDate issuedDate;

    private final LocalDate dueDate;

    @ManyToOne(cascade = CascadeType.ALL)
    private final Company seller;

    @ManyToOne(cascade = CascadeType.ALL)
    private final Company buyer;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private final List<InvoiceEntry> entries;

    private HibernateInvoice(HibernateInvoice.Builder builder) {
        id = builder.id;
        number = builder.number;
        issuedDate = builder.issuedDate;
        dueDate = builder.dueDate;
        seller = builder.seller;
        buyer = builder.buyer;
        entries = builder.entries;
    }

    public static HibernateInvoice.Builder builder() {
        return new HibernateInvoice.Builder();
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Company getSeller() {
        return seller;
    }

    public Company getBuyer() {
        return buyer;
    }

    public List<InvoiceEntry> getEntries() {
        return entries != null ? new ArrayList(entries) : new ArrayList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HibernateInvoice invoice = (HibernateInvoice) o;
        return Objects.equals(id, invoice.id)
            && Objects.equals(number, invoice.number)
            && Objects.equals(issuedDate, invoice.issuedDate)
            && Objects.equals(dueDate, invoice.dueDate)
            && Objects.equals(seller, invoice.seller)
            && Objects.equals(buyer, invoice.buyer)
            && Objects.equals(entries, invoice.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, issuedDate, dueDate, seller, buyer, entries);
    }

    @Override
    public String toString() {
        return "HibernateInvoice{"
            + "id=" + id
            + ", number='" + number + '\''
            + ", issuedDate=" + issuedDate
            + ", dueDate=" + dueDate
            + ", seller=" + seller
            + ", buyer=" + buyer
            + ", entries=" + entries
            + '}';
    }

    public static class Builder {

        private Long id;
        private String number;
        private LocalDate issuedDate;
        private LocalDate dueDate;
        private Company seller;
        private Company buyer;
        private List<InvoiceEntry> entries;

        public HibernateInvoice.Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public HibernateInvoice.Builder withNumber(String number) {
            this.number = number;
            return this;
        }

        public HibernateInvoice.Builder withIssuedDate(LocalDate issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }

        public HibernateInvoice.Builder withDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public HibernateInvoice.Builder withSeller(Company seller) {
            this.seller = seller;
            return this;
        }

        public HibernateInvoice.Builder withBuyer(Company buyer) {
            this.buyer = buyer;
            return this;
        }

        public HibernateInvoice.Builder withEntries(List<InvoiceEntry> entries) {
            this.entries = entries;
            return this;
        }

        public HibernateInvoice build() {
            return new HibernateInvoice(this);
        }
    }
}
