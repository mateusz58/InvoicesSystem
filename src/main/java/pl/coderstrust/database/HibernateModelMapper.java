package pl.coderstrust.database;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.coderstrust.database.hibernate.HibernateCompany;
import pl.coderstrust.database.hibernate.HibernateInvoice;
import pl.coderstrust.database.hibernate.HibernateInvoiceEntry;
import pl.coderstrust.database.hibernate.HibernateVat;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface HibernateModelMapper {

    Collection<HibernateInvoice> mapToHibernateInvoices(Collection<Invoice> invoices);

    Collection<Invoice> mapToInvoices(Collection<HibernateInvoice> invoices);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withNumber", source = "number")
    @Mapping(target = "withIssuedDate", source = "issuedDate")
    @Mapping(target = "withDueDate", source = "dueDate")
    @Mapping(target = "withSeller", source = "seller")
    @Mapping(target = "withBuyer", source = "buyer")
    @Mapping(target = "withEntries", source = "entries")
    HibernateInvoice mapToHibernateInvoice(Invoice invoice);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withNumber", source = "number")
    @Mapping(target = "withIssuedDate", source = "issuedDate")
    @Mapping(target = "withDueDate", source = "dueDate")
    @Mapping(target = "withSeller", source = "seller")
    @Mapping(target = "withBuyer", source = "buyer")
    @Mapping(target = "withEntries", source = "entries")
    Invoice mapToInvoice(HibernateInvoice invoice);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withName", source = "name")
    @Mapping(target = "withAddress", source = "address")
    @Mapping(target = "withTaxId", source = "taxId")
    @Mapping(target = "withAccountNumber", source = "accountNumber")
    @Mapping(target = "withPhoneNumber", source = "phoneNumber")
    @Mapping(target = "withEmail", source = "email")
    HibernateCompany mapToHibernateIComapny(Company company);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withName", source = "name")
    @Mapping(target = "withAddress", source = "address")
    @Mapping(target = "withTaxId", source = "taxId")
    @Mapping(target = "withAccountNumber", source = "accountNumber")
    @Mapping(target = "withPhoneNumber", source = "phoneNumber")
    @Mapping(target = "withEmail", source = "email")
    Company mapToComapny(HibernateCompany company);

    @Mapping(target = "wthId", source = "id")
    @Mapping(target = "withNumber", source = "number")
    @Mapping(target = "withQuantity", source = "quantity")
    @Mapping(target = "withPrice", source = "price")
    @Mapping(target = "withNetValue", source = "netValue")
    @Mapping(target = "withGrossValue", source = "grossValue")
    @Mapping(target = "withVatRate", source = "vatRate")
    HibernateInvoiceEntry mapToHibernateInvoiceEntry(InvoiceEntry invoiceEntry);

    @Mapping(target = "wthId", source = "id")
    @Mapping(target = "withNumber", source = "number")
    @Mapping(target = "withQuantity", source = "quantity")
    @Mapping(target = "withPrice", source = "price")
    @Mapping(target = "withNetValue", source = "netValue")
    @Mapping(target = "withGrossValue", source = "grossValue")
    @Mapping(target = "withVatRate", source = "vatRate")
    InvoiceEntry mapToInvoiceEntry(HibernateInvoiceEntry invoiceEntry);

    Vat mapToHibernateVat(Vat vat);

    HibernateVat maptoVat(HibernateVat vat);
}
