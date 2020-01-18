package pl.coderstrust.database.mongo;

import java.util.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface MongoModelMapper {

    Collection<Invoice> mapToMongoInvoices(Collection<pl.coderstrust.model.Invoice> invoices);

    Collection<pl.coderstrust.model.Invoice> mapToInvoices(Collection<Invoice> invoices);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "number", source = "number")
    @Mapping(target = "issuedDate", source = "issuedDate")
    @Mapping(target = "dueDate", source = "dueDate")
    @Mapping(target = "seller", source = "seller")
    @Mapping(target = "buyer", source = "buyer")
    @Mapping(target = "entries", source = "entries")
    Invoice mapToMongoInvoice(pl.coderstrust.model.Invoice invoice);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "number", source = "number")
    @Mapping(target = "issuedDate", source = "issuedDate")
    @Mapping(target = "dueDate", source = "dueDate")
    @Mapping(target = "seller", source = "seller")
    @Mapping(target = "buyer", source = "buyer")
    @Mapping(target = "entries", source = "entries")
    pl.coderstrust.model.Invoice mapToInvoice(Invoice invoice);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "taxId", source = "taxId")
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "email", source = "email")
    Company mapToMongoCompany(pl.coderstrust.model.Company company);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "taxId", source = "taxId")
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "email", source = "email")
    pl.coderstrust.model.Company mapToCompany(Company company);

    @Mapping(target = "description", source = "description")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "netValue", source = "netValue")
    @Mapping(target = "grossValue", source = "grossValue")
    @Mapping(target = "vatRate", source = "vatRate")
    InvoiceEntry mapToMongoInvoiceEntry(pl.coderstrust.model.InvoiceEntry invoiceEntry);

    @Mapping(target = "description", source = "description")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "netValue", source = "netValue")
    @Mapping(target = "grossValue", source = "grossValue")
    @Mapping(target = "vatRate", source = "vatRate")
    pl.coderstrust.model.InvoiceEntry mapToInvoiceEntry(InvoiceEntry invoiceEntry);

   Vat mapToMongoVat(pl.coderstrust.model.Vat vat);

    pl.coderstrust.model.Vat maptoVat(Vat vat);
}
