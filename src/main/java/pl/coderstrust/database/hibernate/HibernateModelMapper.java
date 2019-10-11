package pl.coderstrust.database.hibernate;

import java.util.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HibernateModelMapper {

    Collection<Invoice> mapToHibernateInvoices(Collection<pl.coderstrust.model.Invoice> invoices);

    Collection<pl.coderstrust.model.Invoice> mapToInvoices(Collection<Invoice> invoices);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withNumber", source = "number")
    @Mapping(target = "withIssuedDate", source = "issuedDate")
    @Mapping(target = "withDueDate", source = "dueDate")
    @Mapping(target = "withSeller", source = "seller")
    @Mapping(target = "withBuyer", source = "buyer")
    @Mapping(target = "withEntries", source = "entries")
    Invoice mapToHibernateInvoice(pl.coderstrust.model.Invoice invoice);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withNumber", source = "number")
    @Mapping(target = "withIssuedDate", source = "issuedDate")
    @Mapping(target = "withDueDate", source = "dueDate")
    @Mapping(target = "withSeller", source = "seller")
    @Mapping(target = "withBuyer", source = "buyer")
    @Mapping(target = "withEntries", source = "entries")
    pl.coderstrust.model.Invoice mapToInvoice(Invoice invoice);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withName", source = "name")
    @Mapping(target = "withAddress", source = "address")
    @Mapping(target = "withTaxId", source = "taxId")
    @Mapping(target = "withAccountNumber", source = "accountNumber")
    @Mapping(target = "withPhoneNumber", source = "phoneNumber")
    @Mapping(target = "withEmail", source = "email")
    Company mapToHibernateCompany(pl.coderstrust.model.Company company);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withName", source = "name")
    @Mapping(target = "withAddress", source = "address")
    @Mapping(target = "withTaxId", source = "taxId")
    @Mapping(target = "withAccountNumber", source = "accountNumber")
    @Mapping(target = "withPhoneNumber", source = "phoneNumber")
    @Mapping(target = "withEmail", source = "email")
    pl.coderstrust.model.Company mapToCompany(Company company);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withDescription", source = "description")
    @Mapping(target = "withQuantity", source = "quantity")
    @Mapping(target = "withPrice", source = "price")
    @Mapping(target = "withNetValue", source = "netValue")
    @Mapping(target = "withGrossValue", source = "grossValue")
    @Mapping(target = "withVatRate", source = "vatRate")
    InvoiceEntry mapToHibernateInvoiceEntry(pl.coderstrust.model.InvoiceEntry invoiceEntry);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withDescription", source = "description")
    @Mapping(target = "withQuantity", source = "quantity")
    @Mapping(target = "withPrice", source = "price")
    @Mapping(target = "withNetValue", source = "netValue")
    @Mapping(target = "withGrossValue", source = "grossValue")
    @Mapping(target = "withVatRate", source = "vatRate")
    pl.coderstrust.model.InvoiceEntry mapToInvoiceEntry(InvoiceEntry invoiceEntry);

    Vat mapToHibernateVat(pl.coderstrust.model.Vat vat);

    pl.coderstrust.model.Vat maptoVat(Vat vat);
}
