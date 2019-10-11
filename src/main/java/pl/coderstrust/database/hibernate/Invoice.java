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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "invoice")
public class Invoice {

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
    @JoinTable(name = "invoice_entries",
        joinColumns = {@JoinColumn(name = "invoice_id")},
        inverseJoinColumns = {@JoinColumn(name = "entries_id")})
    private final List<InvoiceEntry> entries;

    private Invoice() {
        id = null;
        number = null;
        issuedDate = null;
        dueDate = null;
        seller = null;
        buyer = null;
        entries = null;
    }

    private Invoice(Invoice.Builder builder) {
        id = builder.id;
        number = builder.number;
        issuedDate = builder.issuedDate;
        dueDate = builder.dueDate;
        seller = builder.seller;
        buyer = builder.buyer;
        entries = builder.entries;
    }

    public static Invoice.Builder builder() {
        return new Invoice.Builder();
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
    public int hashCode() {
        return Objects.hash(id, number, issuedDate, dueDate, seller, buyer, entries);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Invoice invoice = (Invoice) o;
        return Objects.equals(id, invoice.id)
            && Objects.equals(number, invoice.number)
            && Objects.equals(issuedDate, invoice.issuedDate)
            && Objects.equals(dueDate, invoice.dueDate)
            && Objects.equals(seller, invoice.seller)
            && Objects.equals(buyer, invoice.buyer)
            && Objects.equals(entries, invoice.entries);
    }

    @Override
    public String toString() {
        return "Invoice{"
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

        public Invoice.Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Invoice.Builder withNumber(String number) {
            this.number = number;
            return this;
        }

        public Invoice.Builder withIssuedDate(LocalDate issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }

        public Invoice.Builder withDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Invoice.Builder withSeller(Company seller) {
            this.seller = seller;
            return this;
        }

        public Invoice.Builder withBuyer(Company buyer) {
            this.buyer = buyer;
            return this;
        }

        public Invoice.Builder withEntries(List<InvoiceEntry> entries) {
            this.entries = entries;
            return this;
        }

        public Invoice build() {
            return new Invoice(this);
        }
    }
}
