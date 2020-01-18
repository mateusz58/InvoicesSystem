package pl.coderstrust.database;

import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderstrust.database.hibernate.Invoice;

@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "hibernate")
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> getFirstByNumber(String number);
}

