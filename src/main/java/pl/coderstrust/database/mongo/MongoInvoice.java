package pl.coderstrust.database.mongo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.InvoiceEntry;

@Document
public class MongoInvoice {

    @Id
    private final String mongoId;

    @Indexed
    private final Long id;
    private final String number;
    private final LocalDate issuedDate;
    private final LocalDate dueDate;
    private final Company seller;
    private final Company buyer;
    private final List<InvoiceEntry> entries;

    @PersistenceConstructor
    public MongoInvoice(String mongoId, Long id, String number, LocalDate issuedDate, LocalDate dueDate, Company seller, Company buyer, List<InvoiceEntry> entries) {
        this.mongoId = mongoId;
        this.id = id;
        this.number = number;
        this.issuedDate = issuedDate;
        this.dueDate = dueDate;
        this.seller = seller;
        this.buyer = buyer;
        this.entries = entries;
    }

    private MongoInvoice() {
        mongoId = null;
        id = null;
        number = null;
        issuedDate = null;
        dueDate = null;
        seller = null;
        buyer = null;
        entries = null;
    }

    private MongoInvoice(MongoInvoice.Builder builder) {
        mongoId = builder.mongoId;
        id = builder.id;
        number = builder.number;
        issuedDate = builder.issuedDate;
        dueDate = builder.dueDate;
        seller = builder.seller;
        buyer = builder.buyer;
        entries = builder.entries;
    }

    public static MongoInvoice.Builder builder() {
        return new MongoInvoice.Builder();
    }

    public String getMongoId() {
        return mongoId;
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
        MongoInvoice invoice = (MongoInvoice) o;
        return Objects.equals(id, invoice.id)
                && Objects.equals(mongoId, invoice.mongoId)
                && Objects.equals(number, invoice.number)
                && Objects.equals(issuedDate, invoice.issuedDate)
                && Objects.equals(dueDate, invoice.dueDate)
                && Objects.equals(seller, invoice.seller)
                && Objects.equals(buyer, invoice.buyer)
                && Objects.equals(entries, invoice.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mongoId, id, number, issuedDate, dueDate, seller, buyer, entries);
    }

    @Override
    public String toString() {
        return "MongoInvoice{"
                + "mongoId=" + mongoId
                + ", id=" + id
                + ", number='" + number + '\''
                + ", issuedDate=" + issuedDate
                + ", dueDate=" + dueDate
                + ", seller=" + seller
                + ", buyer=" + buyer
                + ", entries=" + entries
                + '}';
    }

    public static class Builder {
        private String mongoId;
        private Long id;
        private String number;
        private LocalDate issuedDate;
        private LocalDate dueDate;
        private Company seller;
        private Company buyer;
        private List<InvoiceEntry> entries;

        public MongoInvoice.Builder withMongoId(String mongoId) {
            this.mongoId = mongoId;
            return this;
        }

        public MongoInvoice.Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public MongoInvoice.Builder withNumber(String number) {
            this.number = number;
            return this;
        }

        public MongoInvoice.Builder withIssuedDate(LocalDate issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }

        public MongoInvoice.Builder withDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public MongoInvoice.Builder withSeller(Company seller) {
            this.seller = seller;
            return this;
        }

        public MongoInvoice.Builder withBuyer(Company buyer) {
            this.buyer = buyer;
            return this;
        }

        public MongoInvoice.Builder withEntries(List<InvoiceEntry> entries) {
            this.entries = entries;
            return this;
        }

        public MongoInvoice build() {
            return new MongoInvoice(this);
        }
    }
}
