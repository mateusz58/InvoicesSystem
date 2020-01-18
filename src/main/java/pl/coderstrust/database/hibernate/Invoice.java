package pl.coderstrust.database.hibernate;

import java.time.LocalDate;
import java.util.List;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderClassName = "InvoiceBuilder", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;

    private LocalDate issuedDate;

    private LocalDate dueDate;

    @ManyToOne(cascade = CascadeType.ALL)
    private Company seller;

    @ManyToOne(cascade = CascadeType.ALL)
    private  Company buyer;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "invoice_entries",
        joinColumns = {@JoinColumn(name = "invoice_id")},
        inverseJoinColumns = {@JoinColumn(name = "entries_id")})
    private  List<InvoiceEntry> entries;

    public static class InvoiceBuilder {
    }
}