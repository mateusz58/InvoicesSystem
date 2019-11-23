package pl.coderstrust.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonDeserialize(builder = Invoice.Builder.class)
@ApiModel(value = "Invoice", description = "Vat Invoice")
public final class Invoice {

    @ApiModelProperty(value = "The unique identifier of the invoice", position = - 1, dataType = "Long")
    private final Long id;
    @ApiModelProperty(value = "Invoice number", example = "FV/1/05/2019")
    private final String number;
    @ApiModelProperty(value = "Date of Invoice creation", example = "2019-11-21")
    private final LocalDate issuedDate;
    @ApiModelProperty(value = "Term of payment", example = "2019-11-21")
    private final LocalDate dueDate;
    @ApiModelProperty(value = "Data of vendor")
    private final Company seller;
    @ApiModelProperty(value = "Data of customer")
    private final Company buyer;
    @ApiModelProperty(value = "Merchandise")
    private final List<InvoiceEntry> entries;

    private Invoice(Builder builder) {
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

    @JsonPOJOBuilder
    public static class Builder {

        private Long id;
        private String number;
        private LocalDate issuedDate;
        private LocalDate dueDate;
        private Company seller;
        private Company buyer;
        private List<InvoiceEntry> entries;

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withNumber(String number) {
            this.number = number;
            return this;
        }

        public Builder withIssuedDate(LocalDate issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }

        public Builder withDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Builder withSeller(Company seller) {
            this.seller = seller;
            return this;
        }

        public Builder withBuyer(Company buyer) {
            this.buyer = buyer;
            return this;
        }

        public Builder withInvoice(Invoice invoice) {
            this.id = invoice.id;
            this.number = invoice.number;
            this.dueDate = invoice.dueDate;
            this.issuedDate = invoice.issuedDate;
            this.seller = invoice.seller;
            this.buyer = invoice.buyer;
            this.entries = invoice.entries;
            return this;
        }

        public Builder withEntries(List<InvoiceEntry> entries) {
            this.entries = entries;
            return this;
        }

        public Invoice build() {
            return new Invoice(this);
        }
    }
}
