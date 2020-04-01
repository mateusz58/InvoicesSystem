package pl.coderstrust.database.mongo;

import java.time.LocalDate;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.InvoiceEntry;

@Data
@Builder(builderClassName = "InvoiceBuilder", toBuilder = true)
@NoArgsConstructor
@Document
public  class Invoice{

    @Id
    private  String mongoId;

    @Indexed(unique = true)
    private Long id;
    private String number;
    private LocalDate issuedDate;
    private LocalDate dueDate;
    private Company seller;
    private Company buyer;
    private List<InvoiceEntry> entries;

    @PersistenceConstructor
    private Invoice(String mongoId, Long id, String number, LocalDate issuedDate, LocalDate dueDate, Company seller, Company buyer, List<InvoiceEntry> entries) {
        this.mongoId = mongoId;
        this.id = id;
        this.number = number;
        this.issuedDate = issuedDate;
        this.dueDate = dueDate;
        this.seller = seller;
        this.buyer = buyer;
        this.entries = entries;
    }

    public static class InvoiceBuilder {
    }
}