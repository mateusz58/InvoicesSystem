package pl.coderstrust.database.mongo;

import java.util.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

@Mapper(componentModel = "spring")
public interface MongoModelMapper {

    Collection<MongoInvoice> mapToMongoInvoices(Collection<Invoice> invoices);

    Collection<Invoice> mapToInvoices(Collection<MongoInvoice> invoices);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withNumber", source = "number")
    @Mapping(target = "withIssuedDate", source = "issuedDate")
    @Mapping(target = "withDueDate", source = "dueDate")
    @Mapping(target = "withSeller", source = "seller")
    @Mapping(target = "withBuyer", source = "buyer")
    @Mapping(target = "withEntries", source = "entries")
    MongoInvoice mapToMongoInvoice(Invoice invoice);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withNumber", source = "number")
    @Mapping(target = "withIssuedDate", source = "issuedDate")
    @Mapping(target = "withDueDate", source = "dueDate")
    @Mapping(target = "withSeller", source = "seller")
    @Mapping(target = "withBuyer", source = "buyer")
    @Mapping(target = "withEntries", source = "entries")
    Invoice mapToInvoice(MongoInvoice invoice);

    @Mapping(target = "withName", source = "name")
    @Mapping(target = "withAddress", source = "address")
    @Mapping(target = "withTaxId", source = "taxId")
    @Mapping(target = "withAccountNumber", source = "accountNumber")
    @Mapping(target = "withPhoneNumber", source = "phoneNumber")
    @Mapping(target = "withEmail", source = "email")
    MongoCompany mapToMongoCompany(Company company);

    @Mapping(target = "withName", source = "name")
    @Mapping(target = "withAddress", source = "address")
    @Mapping(target = "withTaxId", source = "taxId")
    @Mapping(target = "withAccountNumber", source = "accountNumber")
    @Mapping(target = "withPhoneNumber", source = "phoneNumber")
    @Mapping(target = "withEmail", source = "email")
    Company mapToCompany(MongoCompany company);

    @Mapping(target = "withDescription", source = "description")
    @Mapping(target = "withQuantity", source = "quantity")
    @Mapping(target = "withPrice", source = "price")
    @Mapping(target = "withNetValue", source = "netValue")
    @Mapping(target = "withGrossValue", source = "grossValue")
    @Mapping(target = "withVatRate", source = "vatRate")
    MongoInvoiceEntry mapToMongoInvoiceEntry(InvoiceEntry invoiceEntry);

    @Mapping(target = "withDescription", source = "description")
    @Mapping(target = "withQuantity", source = "quantity")
    @Mapping(target = "withPrice", source = "price")
    @Mapping(target = "withNetValue", source = "netValue")
    @Mapping(target = "withGrossValue", source = "grossValue")
    @Mapping(target = "withVatRate", source = "vatRate")
    InvoiceEntry mapToInvoiceEntry(MongoInvoiceEntry invoiceEntry);

    MongoVat mapToMongoVat(Vat vat);

    Vat maptoVat(MongoVat vat);
}
