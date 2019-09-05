package pl.coderstrust.database.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<HibernateInvoice, Long> {
}
