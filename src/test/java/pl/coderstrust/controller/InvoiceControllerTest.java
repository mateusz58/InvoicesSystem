package pl.coderstrust.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.service.InvoiceEmailService;
import pl.coderstrust.service.InvoicePdfService;
import pl.coderstrust.service.InvoiceService;
import pl.coderstrust.service.ServiceOperationException;

@ExtendWith(SpringExtension.class)
@WebMvcTest(InvoiceController.class)
class InvoiceControllerTest {

    @MockBean
    private InvoiceService invoiceService;

    @MockBean
    private InvoiceEmailService invoiceEmailService;

    @MockBean
    private InvoicePdfService invoicePdfService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    String url = "/api/invoices/";
    String urlPdf = "/api/invoices/pdf/";
    byte[] invoiceAsPdf = "xc45#2f".getBytes();

    @Test
    void shouldReturnInvoiceById() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.of(invoiceToGet)).when(invoiceService).getById(invoiceToGet.getId());

        //When
        mockMvc.perform(get(String.format("%s%d", url, invoiceToGet.getId()))
            .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(mapper.writeValueAsString(invoiceToGet)));

        //Then
        verify(invoiceService, times(1)).getById(invoiceToGet.getId());
    }

    @Test
    void shouldReturnNotAcceptableStatusDuringGettingInvoiceByIdWithNotSupportedMediaType() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.of(invoiceToGet)).when(invoiceService).getById(invoiceToGet.getId());

        //When
        mockMvc.perform(get(String.format("%s%d", url, invoiceToGet.getId()))
            .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(status().isNotAcceptable());

        //Then
        verify(invoiceService, never()).getById(invoiceToGet.getId());
    }

    @Test
    void shouldReturnNotFoundStatusWhileGettingNonExistingInvoiceById() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.empty()).when(invoiceService).getById(invoiceToGet.getId());

        //When
        mockMvc.perform(get(String.format("%s%d", url, invoiceToGet.getId())))
            .andExpect(status().isNotFound());

        //Then
        verify(invoiceService, times(1)).getById(invoiceToGet.getId());
    }

    @Test
    void shouldReturnInternalServerErrorStatusDuringGettingInvoiceByIdWhenSomethingWentWrongOnServer() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doThrow(ServiceOperationException.class).when(invoiceService).getById(invoiceToGet.getId());

        //When
        mockMvc.perform(get(String.format("%s%d", url, invoiceToGet.getId())))
            .andExpect(status().isInternalServerError());

        //Then
        verify(invoiceService, times(1)).getById(invoiceToGet.getId());
    }

    @Test
    void shouldReturnInvoiceAsPdfById() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.of(invoiceToGet)).when(invoiceService).getById(invoiceToGet.getId());
        doReturn(invoiceAsPdf).when(invoicePdfService).createPdf(invoiceToGet);

        //When
        mockMvc.perform(get(String.format("%s%d", "/api/invoices/pdf/", invoiceToGet.getId()))
            .accept(MediaType.APPLICATION_PDF_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE))
            .andExpect(content().bytes(invoiceAsPdf));

        //Then
        assertNotNull(invoiceAsPdf);
        assertTrue(invoiceAsPdf.length > 0);
        verify(invoiceService, times(1)).getById(invoiceToGet.getId());
        verify(invoicePdfService, times(1)).createPdf(invoiceToGet);
    }

    @Test
    void shouldReturnNotAcceptableStatusDuringGettingInvoiceAsPdfByIdWithNotSupportedMediaType() throws Exception {
        //Given
        doReturn(Optional.of(InvoiceGenerator.generateRandomInvoice())).when(invoiceService).getById(1L);

        //When
        mockMvc.perform(get(String.format("%s%d", urlPdf, 1L))
            .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(status().isNotAcceptable());

        //Then
        verify(invoiceService, never()).getById(anyLong());
    }

    @Test
    void shouldReturnNotFoundStatusWhileGettingNonExistingInvoiceAsPdfById() throws Exception {
        //Given
        long id = 1L;
        doReturn(Optional.empty()).when(invoiceService).getById(id);

        //When
        mockMvc.perform(get(String.format("%s%d", urlPdf, id)))
            .andExpect(status().isNotFound());

        //Then
        verify(invoiceService, times(1)).getById(id);
    }

    @Test
    void shouldReturnInternalServerErrorStatusDuringGettingInvoiceAsPdfByIdWhenSomethingWentWrongOnServer() throws Exception {
        //Given
        long id = 1L;
        doThrow(ServiceOperationException.class).when(invoiceService).getById(id);

        //When
        mockMvc.perform(get(String.format("%s%d", urlPdf, id)))
            .andExpect(status().isInternalServerError());

        //Then
        verify(invoiceService, times(1)).getById(id);
    }

    @Test
    void shouldReturnInvoiceByNumber() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        String endPoint = String.format("byNumber?number=%s", invoiceToGet.getNumber());
        doReturn(Optional.of(invoiceToGet)).when(invoiceService).getByNumber(invoiceToGet.getNumber());

        //When
        mockMvc.perform(get(String.format("%s%s", url, endPoint)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(mapper.writeValueAsString(invoiceToGet)));

        //Then
        verify(invoiceService, times(1)).getByNumber(invoiceToGet.getNumber());
    }

    @Test
    void shouldReturnBadRequestStatusWhileGettingInvoiceWithNullNumber() throws Exception {
        //When
        mockMvc.perform(get(String.format("%s%d", url, null)))
            .andExpect(status().isBadRequest());

        //Then
        verify(invoiceService, never()).getByNumber(null);
    }

    @Test
    void shouldReturnNotFoundStatusWhileGettingNonExistingInvoiceByNumber() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.empty()).when(invoiceService).getByNumber(invoiceToGet.getNumber());
        String endPoint = String.format("byNumber?number=%s", invoiceToGet.getNumber());

        //When
        mockMvc.perform(get(String.format("%s%s", url, endPoint)))
            .andExpect(status().isNotFound());

        //Then
        verify(invoiceService, times(1)).getByNumber(invoiceToGet.getNumber());
    }

    @Test
    void shouldReturnNotAcceptableStatusDuringGettingInvoiceByNumberWithNotSupportedMediaType() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.of(invoiceToGet)).when(invoiceService).getByNumber(invoiceToGet.getNumber());
        String endPoint = String.format("byNumber?=number%s", invoiceToGet.getNumber());

        //When
        mockMvc.perform(get(String.format("%s%s", url, endPoint))
            .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(status().isNotAcceptable());

        //Then
        verify(invoiceService, never()).getByNumber(invoiceToGet.getNumber());
    }

    @Test
    void shouldReturnInternalServerErrorStatusDuringGettingInvoiceByNumberWhenSomethingWentWrongOnServer() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doThrow(ServiceOperationException.class).when(invoiceService).getByNumber(invoiceToGet.getNumber());
        String endPoint = String.format("byNumber?number=%s", invoiceToGet.getNumber());

        //When
        mockMvc.perform(get(String.format("%s%s", url, endPoint)))
            .andExpect(status().isInternalServerError());

        //Then
        verify(invoiceService, times(1)).getByNumber(invoiceToGet.getNumber());
    }

    @Test
    void shouldReturnInvoiceAsPdfByNumber() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        String endPoint = String.format("byNumber?number=%s", invoiceToGet.getNumber());
        doReturn(Optional.of(invoiceToGet)).when(invoiceService).getByNumber(invoiceToGet.getNumber());
        doReturn(invoiceAsPdf).when(invoicePdfService).createPdf(invoiceToGet);

        //When
        mockMvc.perform(get(String.format("%s%s", "/api/invoices/pdf/", endPoint))
            .accept(MediaType.APPLICATION_PDF_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE))
            .andExpect(content().bytes(invoiceAsPdf));

        //Then
        assertNotNull(invoiceAsPdf);
        assertTrue(invoiceAsPdf.length > 0);
        verify(invoiceService, times(1)).getByNumber(invoiceToGet.getNumber());
        verify(invoicePdfService, times(1)).createPdf(invoiceToGet);
    }

    @Test
    void shouldReturnBadRequestStatusWhileGettingInvoiceAsPdfWithNullNumber() throws Exception {
        //When
        mockMvc.perform(get(String.format("%s%d", urlPdf, null)))
            .andExpect(status().isBadRequest());

        //Then
        verify(invoiceService, never()).getByNumber(anyString());
    }

    @Test
    void shouldReturnNotFoundStatusWhileGettingNonExistingInvoiceAsPdfByNumber() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.empty()).when(invoiceService).getByNumber(invoiceToGet.getNumber());
        String endPoint = String.format("byNumber?number=%s", invoiceToGet.getNumber());

        //When
        mockMvc.perform(get(String.format("%s%s", urlPdf, endPoint)))
            .andExpect(status().isNotFound());

        //Then
        verify(invoiceService, times(1)).getByNumber(invoiceToGet.getNumber());
    }

    @Test
    void shouldReturnNotAcceptableStatusDuringGettingInvoiceAsPdfByNumberWithNotSupportedMediaType() throws Exception {
        //Given
        String endPoint = "byNumber?number=asdasd";

        //When
        mockMvc.perform(get(String.format("%s%s", urlPdf, endPoint))
            .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(status().isNotAcceptable());

        //Then
        verify(invoiceService, never()).getByNumber(anyString());
    }

    @Test
    void shouldReturnInternalServerErrorStatusDuringGettingInvoiceAsPdfByNumberWhenSomethingWentWrongOnServer() throws Exception {
        //Given
        String number = "asdasd";
        doThrow(ServiceOperationException.class).when(invoiceService).getByNumber(number);
        String endPoint = String.format("byNumber?number=%s", number);

        //When
        mockMvc.perform(get(String.format("%s%s", urlPdf, endPoint)))
            .andExpect(status().isInternalServerError());

        //Then
        verify(invoiceService, times(1)).getByNumber(number);
    }

    @Test
    void shouldRemoveInvoiceById() throws Exception {
        //Given
        Invoice invoiceToDelete = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).exists(invoiceToDelete.getId());
        doNothing().when(invoiceService).deleteById(invoiceToDelete.getId());

        //When
        mockMvc.perform(delete(String.format("%s%d", url, invoiceToDelete.getId())))
            .andExpect(status().isNoContent());

        //Then
        verify(invoiceService, times(1)).exists(invoiceToDelete.getId());
        verify(invoiceService, times(1)).deleteById(invoiceToDelete.getId());
    }

    @Test
    void shouldReturnNotFoundStatusWhileRemovingNotExistingInvoice() throws Exception {
        //Given
        Invoice invoiceToDelete = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(invoiceService).exists(invoiceToDelete.getId());
        doThrow(ServiceOperationException.class).when(invoiceService).update(invoiceToDelete);

        //When
        mockMvc.perform(delete(String.format("%s%d", url, invoiceToDelete.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoiceToDelete))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        //Then
        verify(invoiceService, times(1)).exists(invoiceToDelete.getId());
        verify(invoiceService, never()).update(invoiceToDelete);
    }

    @Test
    void shouldReturnInternalServerErrorStatusDuringRemovingInvoiceWhenSomethingWentWrongOnServer() throws Exception {
        //Given
        Invoice invoiceToDelete = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).exists(invoiceToDelete.getId());
        doThrow(ServiceOperationException.class).when(invoiceService).deleteById(invoiceToDelete.getId());

        //When
        mockMvc.perform(delete(String.format("%s%d", url, invoiceToDelete.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoiceToDelete))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        //Then
        verify(invoiceService, times(1)).exists(invoiceToDelete.getId());
        verify(invoiceService, times(1)).deleteById(invoiceToDelete.getId());
    }

    @Test
    void shouldRemoveAllInvoices() throws Exception {
        //Given
        doNothing().when(invoiceService).deleteAll();

        //When
        mockMvc.perform(delete(url))
            .andExpect(status().isNoContent());

        //Then
        verify(invoiceService, times(1)).deleteAll();
    }

    @Test
    void shouldReturnInternalServerErrorStatusDuringRemovingAllInvoicesWhenSomethingWentWrongOnServer() throws Exception {
        //Given
        doThrow(ServiceOperationException.class).when(invoiceService).deleteAll();

        //When
        mockMvc.perform(delete(url))
            .andExpect(status().isInternalServerError());

        //Then
        verify(invoiceService, times(1)).deleteAll();
    }

    @Test
    void shouldReturnAllInvoices() throws Exception {
        //Given
        Collection<Invoice> invoices = Arrays.asList(InvoiceGenerator.generateRandomInvoice(), InvoiceGenerator.generateRandomInvoice());
        doReturn(invoices).when(invoiceService).getAll();

        //When
        mockMvc.perform(get(url))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(mapper.writeValueAsString(invoices)));

        //Then
        verify(invoiceService, times(1)).getAll();
    }

    @Test
    void shouldReturnNotAcceptableStatusDuringGettingAllInvoicesWithNotSupportedMediaType() throws Exception {
        //When
        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(status().isNotAcceptable());

        //Then
        verify(invoiceService, never()).getAll();
    }

    @Test
    void shouldReturnInternalServerErrorStatusDuringGettingAllInvoicesWhenSomethingWentWrongOnServer() throws Exception {
        //Given
        doThrow(ServiceOperationException.class).when(invoiceService).getAll();

        //When
        mockMvc.perform(get(url))
            .andExpect(status().isInternalServerError());

        //Then
        verify(invoiceService, times(1)).getAll();
    }

    @Test
    void shouldAddInvoice() throws Exception {
        //Given
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(invoiceService).exists(invoiceToAdd.getId());
        doNothing().when(invoiceEmailService).sendMailWithInvoice(invoiceToAdd);
        doReturn(invoiceToAdd).when(invoiceService).add(invoiceToAdd);

        //When
        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoiceToAdd)))
            .andExpect(status().isCreated());

        //Then
        verify(invoiceService, times(1)).exists(invoiceToAdd.getId());
        verify(invoiceService, times(1)).add(invoiceToAdd);
        verify(invoiceEmailService, times(1)).sendMailWithInvoice(invoiceToAdd);
    }

    @Test
    void shouldReturnUnsupportedMediaTypeStatusDuringAddingInvoiceWithNotSupportedMediaType() throws Exception {
        //Given
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        doReturn(invoiceToAdd).when(invoiceService).add(invoiceToAdd);

        //When
        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_ATOM_XML)
            .content(mapper.writeValueAsBytes(invoiceToAdd))
            .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(status().isUnsupportedMediaType());

        //Then
        verify(invoiceService, never()).add(invoiceToAdd);
        verify(invoiceEmailService, never()).sendMailWithInvoice(invoiceToAdd);
    }

    @Test
    void shouldReturnConflictStatusDuringAddingInvoiceWhenInvoiceExistsInDatabase() throws Exception {
        //Given
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).exists(invoiceToAdd.getId());

        //When
        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoiceToAdd))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());

        //Then
        verify(invoiceService, times(1)).exists(invoiceToAdd.getId());
        verify(invoiceService, never()).add(invoiceToAdd);
        verify(invoiceEmailService, never()).sendMailWithInvoice(invoiceToAdd);
    }

    @Test
    void shouldReturnBadRequestStatusDuringAddingNullAsInvoice() throws Exception {
        //Given
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).exists(invoiceToAdd.getId());

        //When
        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoiceToAdd))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());

        //Then
        verify(invoiceService, times(1)).exists(invoiceToAdd.getId());
        verify(invoiceService, never()).add(invoiceToAdd);
        verify(invoiceEmailService, never()).sendMailWithInvoice(invoiceToAdd);
    }

    @Test
    void shouldReturnInternalServerErrorStatusDuringAddingInvoiceWhenSomethingWentWrongOnServer() throws Exception {
        //Given
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(invoiceService).exists(invoiceToAdd.getId());
        doThrow(ServiceOperationException.class).when(invoiceService).add(invoiceToAdd);

        //When
        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoiceToAdd))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        //Then
        verify(invoiceService, times(1)).exists(invoiceToAdd.getId());
        verify(invoiceService, times(1)).add(invoiceToAdd);
        verify(invoiceEmailService, never()).sendMailWithInvoice(invoiceToAdd);
    }

    @Test
    void shouldUpdateInvoice() throws Exception {
        //Given
        Invoice invoiceToUpdate = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).exists(invoiceToUpdate.getId());
        doReturn(invoiceToUpdate).when(invoiceService).update(invoiceToUpdate);

        //When
        mockMvc.perform(put(String.format("%s%d", url, invoiceToUpdate.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoiceToUpdate))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().json(mapper.writeValueAsString(invoiceToUpdate)));

        //Then
        verify(invoiceService, times(1)).exists(invoiceToUpdate.getId());
        verify(invoiceService, times(1)).update(invoiceToUpdate);
    }

    @Test
    void shouldReturnInternalServerErrorStatusDuringUpdatingInvoiceWhenSomethingWentWrongOnServer() throws Exception {
        //Given
        Invoice invoiceToUpdate = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).exists(invoiceToUpdate.getId());
        doThrow(ServiceOperationException.class).when(invoiceService).update(invoiceToUpdate);

        //When
        mockMvc.perform(put(String.format("%s%d", url, invoiceToUpdate.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoiceToUpdate))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        //Then
        verify(invoiceService, times(1)).exists(invoiceToUpdate.getId());
        verify(invoiceService, times(1)).update(invoiceToUpdate);
    }

    @Test
    void shouldReturnUnsupportedMediaTypeStatusDuringUpdatingInvoiceWithNotSupportedMediaType() throws Exception {
        //Given
        Invoice invoiceToUpdate = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).exists(invoiceToUpdate.getId());
        doThrow(ServiceOperationException.class).when(invoiceService).update(invoiceToUpdate);

        //When
        mockMvc.perform(put(String.format("%s%d", url, invoiceToUpdate.getId()))
            .contentType(MediaType.APPLICATION_ATOM_XML)
            .content(mapper.writeValueAsBytes(invoiceToUpdate))
            .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(status().isUnsupportedMediaType());

        //Then
        verify(invoiceService, never()).update(invoiceToUpdate);
    }

    @Test
    void shouldReturnBadRequestStatusDuringUpdatingNullAsInvoice() throws Exception {
        //When
        mockMvc.perform(put(String.format("%s%d", url, null))
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(mapper.writeValueAsBytes(null))
            .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        //Then
        verify(invoiceService, never()).update(null);
    }

    @Test
    void shouldReturnBadRequestStatusDuringUpdatingInvoiceWhenPassedIdIsDifferentThanInvoiceId() throws Exception {
        //Given
        Invoice invoiceToUpdate = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).exists(invoiceToUpdate.getId());
        doReturn(invoiceToUpdate).when(invoiceService).update(invoiceToUpdate);

        //When
        mockMvc.perform(put(String.format("%s%d", url, invoiceToUpdate.getId() + 1))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoiceToUpdate))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        //Then
        verify(invoiceService, never()).exists(invoiceToUpdate.getId());
        verify(invoiceService, never()).update(invoiceToUpdate);
    }

    @Test
    void shouldReturnNotFoundStatusDuringUpdatingNotExistingInvoice() throws Exception {
        //Given
        Invoice invoiceToUpdate = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(invoiceService).exists(invoiceToUpdate.getId());
        doThrow(ServiceOperationException.class).when(invoiceService).update(invoiceToUpdate);

        //When
        mockMvc.perform(put(String.format("%s%d", url, invoiceToUpdate.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoiceToUpdate))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        //Then
        verify(invoiceService, times(1)).exists(invoiceToUpdate.getId());
        verify(invoiceService, never()).update(invoiceToUpdate);
    }
}
