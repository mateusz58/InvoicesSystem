package pl.coderstrust.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Invoice {

    private Long id;
    private String number;
    private LocalDate issuedDate;
    private LocalDate dueDate;
    private Company seller;
    private Company buyer;
    private List<InvoiceEntry> entries;

    public static class Builder {

        private Long id;
        private String number;
        private LocalDate issuedDate;
        private LocalDate dueDate;
        private Company seller;
        private Company buyer;
        private List<InvoiceEntry> entries;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder number(String number) {
            this.number = number;
            return this;
        }

        public Builder issuedDate(LocalDate issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }

        public Builder dueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Builder seller(Company seller) {
            this.seller = seller;
            return this;
        }

        public Builder buyer(Company buyer) {
            this.buyer = buyer;
            return this;
        }

        public Builder entries(List<InvoiceEntry> entries) {
            this.entries = entries;
            return this;
        }

        public Invoice build() {
            Invoice invoice = new Invoice();
            invoice.id = this.id;
            invoice.number = this.number;
            invoice.issuedDate = this.issuedDate;
            invoice.dueDate = this.dueDate;
            invoice.seller = this.seller;
            invoice.buyer = this.buyer;
            invoice.entries = this.entries;
            return invoice;
        }
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
        Invoice invoice = (Invoice) o;
        return Objects.equals(id, invoice.id) &&
                Objects.equals(number, invoice.number) &&
                Objects.equals(issuedDate, invoice.issuedDate) &&
                Objects.equals(dueDate, invoice.dueDate) &&
                Objects.equals(seller, invoice.seller) &&
                Objects.equals(buyer, invoice.buyer) &&
                Objects.equals(entries, invoice.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, issuedDate, dueDate, seller, buyer, entries);
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", issuedDate=" + issuedDate +
                ", dueDate=" + dueDate +
                ", seller=" + seller +
                ", buyer=" + buyer +
                ", entries=" + entries +
                '}';
    }
}
