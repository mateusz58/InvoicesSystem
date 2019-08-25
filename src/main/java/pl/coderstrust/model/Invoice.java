package pl.coderstrust.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Invoice {

    private final Long id;
    private final String number;
    private final LocalDate issuedDate;
    private final LocalDate dueDate;
    private final Company seller;
    private final Company buyer;
    private final List<InvoiceEntry> entries;

    public Invoice(Long id, String number, LocalDate issuedDate, LocalDate dueDate, Company seller, Company buyer, List<InvoiceEntry> entries) {
        this.id = id;
        this.number = number;
        this.issuedDate = issuedDate;
        this.dueDate = dueDate;
        this.seller = seller;
        this.buyer = buyer;
        this.entries = entries;
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
