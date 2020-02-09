package pl.coderstrust.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.service.InvoiceEmailService;
import pl.coderstrust.service.InvoicePdfService;
import pl.coderstrust.service.InvoiceService;
import pl.coderstrust.service.ServiceOperationException;
//
@RestController
@RequestMapping("api/invoices")
@Api(value = "api/invoices")
public class InvoiceController {

    private Logger log = LoggerFactory.getLogger(InvoiceController.class);

    private InvoiceService invoiceService;
    private InvoiceEmailService invoiceEmailService;
    private InvoicePdfService invoicePdfService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService, InvoiceEmailService invoiceEmailService, InvoicePdfService invoicePdfService) {
        this.invoiceService = invoiceService;
        this.invoiceEmailService = invoiceEmailService;
        this.invoicePdfService = invoicePdfService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Add new invoice", notes = "Add new invoice to database", response = Invoice.class)
    @ApiResponses({
        @ApiResponse(code = 201, message = "Created", response = Invoice.class),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 406, message = "Not acceptable format"),
        @ApiResponse(code = 409, message = "Invoice exists"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @ApiImplicitParam(required = true, name = "invoice", value = "New invoice data", dataType = "Invoice")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> add(@RequestBody(required = false) Invoice invoice) {
        if (invoice == null) {
            log.error("Attempt to add null invoice.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            if (invoice.getId() != null && invoiceService.exists(invoice.getId())) {
                log.error("Attempt to add invoice already existing in database.");
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            Invoice addedInvoice = invoiceService.add(invoice);
            log.debug("New invoice added with id: {}.", addedInvoice.getId());
            invoiceEmailService.sendMailWithInvoice(addedInvoice);
            log.debug("Sent email with new invoice.");
            return new ResponseEntity<>(addedInvoice, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("An error occured during adding new invoice.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Update invoice", notes = "Update invoice with provided id", response = Invoice.class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "Updated", response = Invoice.class),
        @ApiResponse(code = 404, message = "Invoice not found"),
        @ApiResponse(code = 406, message = "Not acceptable format"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @ApiImplicitParams({
        @ApiImplicitParam(required = true, name = "id", value = "Id of invoice to update", dataType = "Long"),
        @ApiImplicitParam(required = true, name = "invoice", value = "Invoice with updated data", dataType = "Invoice")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody(required = false) Invoice invoice) {
        if (invoice == null) {
            log.error("Attempt to update invoice with null id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            if (!id.equals(invoice.getId())) {
                log.error("Attempt to update invoice with wrong id.");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (!invoiceService.exists(id)) {
                log.error("Attempt to update not existing invoice.");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            log.debug("invoice updated with id: {}.", invoice.getId());
            return new ResponseEntity<>(invoiceService.update(invoice), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("An error occured during updating invoice.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Get all invoices", notes = "Retrieving the collection of all invoices in database", response = Invoice[].class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "OK", response = Invoice[].class),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        try {
            log.debug("Successfully downloaded all invoices.");
            return new ResponseEntity<>(invoiceService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("An error occured during getting all invoices.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Find by Id", notes = "Finds Invoice by given Id", response = Invoice.class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "OK", response = Invoice.class),
        @ApiResponse(code = 404, message = "Invoice not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @ApiImplicitParam(required = true, name = "id", value = "Id of the invoice to get", dataType = "Long")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(@PathVariable("id") long id) {
        try {
            Optional<Invoice> invoice = invoiceService.getById(id);
            if (invoice.isPresent()) {
                log.debug("Found invoice with id {}.", id);
                return new ResponseEntity<>(invoice.get(), HttpStatus.OK);
            }
            log.debug("Invoice with id {} is not found.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("An error occured during getting invoice by id.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/pdf/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> getByIdAsPdf(@PathVariable("id") long id) {
        try {
            Optional<Invoice> invoice = invoiceService.getById(id);
            if (invoice.isPresent()) {
                log.debug("Found invoice with given id.");
                return getResponsePdfEntity(invoice.get());
            }
            log.debug("Invoice with given id is not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("An error occured during getting pdf invoice by id.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> getResponsePdfEntity(Invoice invoice) throws ServiceOperationException {
        byte[] invoiceAsPdf = invoicePdfService.createPdf(invoice);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(invoiceAsPdf, responseHeaders, HttpStatus.OK);
    }

    @ApiOperation(value = "Find by number", notes = "Finds Invoice by given number", response = Invoice.class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "OK", response = Invoice.class),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 404, message = "Invoice not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @ApiImplicitParam(required = true, name = "number", value = "Number of the invoice to get", dataType = "String")
    @GetMapping(value = "/byNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getByNumber(@RequestParam(required = false) String number) {
        if (number == null) {
            log.error("Attempt to get invoice with null number.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Optional<Invoice> invoice = invoiceService.getByNumber(number);
            if (invoice.isPresent()) {
                log.debug("Found invoice with number {}.", number);
                return new ResponseEntity<>(invoice.get(), HttpStatus.OK);
            }
            log.debug("Invoice with number {} is not found.", number);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("An error occured during getting invoice by number.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/pdf/byNumber", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> getByNumberAsPdf(@RequestParam(required = false) String number) {
        if (number == null) {
            log.error("Attempt to get invoice with null number.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Optional<Invoice> invoice = invoiceService.getByNumber(number);
            if (invoice.isPresent()) {
                log.debug("Found invoice with number {}.", number);
                return getResponsePdfEntity(invoice.get());
            }
            log.debug("Invoice with number {} is not found.", number);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("An error occured during getting invoice by number.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    @ApiOperation(value = "Delete all Invoices", notes = "Erases all data in database")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Deleted all", response = Invoice.class),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<?> deleteAll() {
        try {
            invoiceService.deleteAll();
            log.debug("Successfully deleted all invoices from database.");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("An error occured during deleting all invoices.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Delete by Id", notes = "Deletes Invoice with specific Id")
    @ApiResponses({
        @ApiResponse(code = 204, message = "Removed"),
        @ApiResponse(code = 404, message = "Invoice not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        try {
            if (invoiceService.exists(id)) {
                invoiceService.deleteById(id);
                log.debug("Successfully deleted invoice with id {}.", id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            log.debug("Invoice with id {} is not found. ", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("An error occured during deleting invoice by id.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
