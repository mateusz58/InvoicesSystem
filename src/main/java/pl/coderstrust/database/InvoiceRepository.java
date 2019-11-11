package pl.coderstrust.database;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderstrust.database.hibernate.HibernateInvoice;

public interface InvoiceRepository extends JpaRepository<HibernateInvoice, Long> {
}
