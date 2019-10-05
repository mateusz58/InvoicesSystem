package pl.coderstrust.controller;

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
import pl.coderstrust.service.InvoiceService;
import pl.coderstrust.service.ServiceOperationException;

@ExtendWith(SpringExtension.class)
@WebMvcTest(InvoiceController.class)
class InvoiceControllerTest {

    @MockBean
    private InvoiceService invoiceService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    String url = "/invoices/";

    @Test
    void shouldReturnInvoiceById() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.of(invoiceToGet)).when(invoiceService).getById(invoiceToGet.getId());

        //When
        mockMvc.perform(get(url + invoiceToGet.getId())
            .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(mapper.writeValueAsString(invoiceToGet)));

        //Then
        verify(invoiceService, times(1)).getById(invoiceToGet.getId());
    }

    @Test
    void getByIdMethodShouldReturnBadRequestStatusWhenInvoiceDoesNotExist() throws Exception {
        //Given
        doReturn(Optional.empty()).when(invoiceService).getById(1L);

        //When
        mockMvc.perform(get(url + 1L)
            .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound());

        //Then
        verify(invoiceService, times(1)).getById(1L);
    }

    @Test
    void getByIdMethodShouldReturnInternalServerErrorStatusWhenSomethingWentWrongOnServer() throws Exception {
        //Given
        doThrow(new ServiceOperationException()).when(invoiceService).getById(1L);

        //When
        mockMvc.perform(get(url + 1L)
            .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isInternalServerError());
        verify(invoiceService, times(1)).getById(1L);
    }

    @Test
    void shouldReturnInvoiceByNumber() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        String endPoint = String.format("byNumber?number=%s", invoiceToGet.getNumber());
        doReturn(Optional.of(invoiceToGet)).when(invoiceService).getByNumber(invoiceToGet.getNumber());

        //When
        mockMvc.perform(get(url + endPoint))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(mapper.writeValueAsString(invoiceToGet)));

        //Then
        verify(invoiceService, times(1)).getByNumber(invoiceToGet.getNumber());
    }

    @Test
    void shouldRemoveInvoiceById() throws Exception {
        //Given
        Invoice invoiceToDelete = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).exists(invoiceToDelete.getId());
        doNothing().when(invoiceService).deleteById(invoiceToDelete.getId());

        //When
        mockMvc.perform(delete(url + invoiceToDelete.getId()))
            .andExpect(status().isNoContent());

        //Then
        verify(invoiceService, times(1)).deleteById(invoiceToDelete.getId());
    }

    @Test
    void shouldRemoveAllInvoices() throws Exception {
        //Given
        doNothing().when(invoiceService).deleteAll();

        //When
        mockMvc.perform(delete(url))
            .andExpect(status().isOk());

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
    void shouldAddInvoice() throws Exception {
        //Given
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(invoiceService).exists(invoiceToAdd.getId());
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
    }

    @Test
    void shouldUpdateInvoice() throws Exception {
        //Given
        Invoice invoiceToUpdate = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).exists(invoiceToUpdate.getId());
        doReturn(invoiceToUpdate).when(invoiceService).update(invoiceToUpdate);

        //When
        mockMvc.perform(put(url + invoiceToUpdate.getId())
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
    void shouldReturnNotAcceptableStatusWhileGettingAllInvoicesWithNotSupportedMediaType() throws Exception {
        //When
        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(status().isNotAcceptable());

        //Then
        verify(invoiceService, never()).getAll();
    }

    @Test
    public void shouldReturnInternalServerErrorWhileGettingAllInvoices() throws Exception {
        //Given
        doThrow(ServiceOperationException.class).when(invoiceService).getAll();

        //When
        mockMvc.perform(get(url))
            .andExpect(status().isInternalServerError());

        //Then
        verify(invoiceService, times(1)).getAll();
    }

    @Test
    void shouldReturnNotAcceptableStatusWhileGettingInvoiceById() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.of(invoiceToGet)).when(invoiceService).getById(invoiceToGet.getId());

        //When
        mockMvc.perform(get(url + invoiceToGet.getId())
            .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(status().isNotAcceptable());

        //Then
        verify(invoiceService, never()).getAll();

    }

    @Test
    void shouldReturnNotFoundWhileGettingNonExistentInvoiceById() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.empty()).when(invoiceService).getById(invoiceToGet.getId());

        //When
        mockMvc.perform(get(url + invoiceToGet.getId()))
            .andExpect(status().isNotFound());

        //Then
        verify(invoiceService, times(1)).getById(invoiceToGet.getId());
    }

    @Test
    void shouldReturnInternalServerErrorWhileGettingInvoiceById() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doThrow(ServiceOperationException.class).when(invoiceService).getById(invoiceToGet.getId());

        //When
        mockMvc.perform(get(url + invoiceToGet.getId()))
            .andExpect(status().isInternalServerError());

        //Then
        verify(invoiceService, times(1)).getById(invoiceToGet.getId());

    }

    @Test
    void shouldReturnBadRequestStatusWhileGettingInvoiceWithNullNumber() throws Exception {
        //Given
        doReturn(Optional.empty()).when(invoiceService).getByNumber(null);
        String endPoint = String.format("byNumber?=number%s", null);

        //When
        mockMvc.perform(get(url + endPoint))
            .andExpect(status().isBadRequest());

        //Then
        verify(invoiceService, never()).getByNumber(null);
    }

    @Test
    void shouldReturnNotAcceptableStatusWhileGettingInvoiceByNumberWithNotSupportedMediaType() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.of(invoiceToGet)).when(invoiceService).getByNumber(invoiceToGet.getNumber());
        String endPoint = String.format("byNumber?=number%s", invoiceToGet.getNumber());

        //When
        mockMvc.perform(get(url + endPoint)
            .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(status().isNotAcceptable());

        //Then
        verify(invoiceService, never()).getByNumber(invoiceToGet.getNumber());
    }

    @Test
    void shouldReturnNotFoundStatusWhileGettingInvoiceByNumber() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.of(invoiceToGet)).when(invoiceService).getByNumber(invoiceToGet.getNumber());
        String endPoint = String.format("byNumber?=%s", invoiceToGet.getNumber());

        //When
        mockMvc.perform(get(url + endPoint)
            .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(status().isNotAcceptable());

        //Then
        verify(invoiceService, never()).getByNumber(invoiceToGet.getNumber());

    }

    @Test
    void shouldReturnInternalServerErrorWhileGettingInvoiceByNumber() throws Exception {
        //Given
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doThrow(ServiceOperationException.class).when(invoiceService).getByNumber(invoiceToGet.getNumber());
        String endPoint = String.format("byNumber?number=%s", invoiceToGet.getNumber());

        //When
        mockMvc.perform(get(url + endPoint))
            .andExpect(status().isInternalServerError());

        //Then
        verify(invoiceService, times(1)).getByNumber(invoiceToGet.getNumber());
    }

    @Test
    void shouldReturnUnsupportedMediaTypeStatusWhileAddingInvoice() throws Exception {
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
    }

    @Test
    void shouldReturnConflictStatusWhileAddingInvoiceWhenInvoiceExistsInDatabase() throws Exception {
        //Given
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).exists(invoiceToAdd.getId());
        doThrow(ServiceOperationException.class).when(invoiceService).add(invoiceToAdd);

        //When
        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoiceToAdd))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());


        //Then
        verify(invoiceService, times(1)).exists(invoiceToAdd.getId());
        verify(invoiceService, never()).add(invoiceToAdd);
    }

    @Test
    void shouldReturnBadRequestStatusWhileAddingNullAsInvoice() throws Exception {
        //Given
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).exists(invoiceToAdd.getId());
        doThrow(ServiceOperationException.class).when(invoiceService).add(invoiceToAdd);

        //When
        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoiceToAdd))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());

        //Then
        verify(invoiceService, times(1)).exists(invoiceToAdd.getId());
        verify(invoiceService, never()).add(invoiceToAdd);
    }

    @Test
    void shouldReturnInternalServerErrorWhileAddingInvoice() throws Exception {
        //Given
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        doThrow(ServiceOperationException.class).when(invoiceService).add(invoiceToAdd);

        //When
        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoiceToAdd))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        //Then
        verify(invoiceService, times(1)).add(invoiceToAdd);
    }

    @Test
    void shouldReturnUnsupportedMediaTypeStatusWhileUpdatingInvoice() throws Exception {
        //Given
        Invoice invoiceToUpdate = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).exists(invoiceToUpdate.getId());
        doThrow(ServiceOperationException.class).when(invoiceService).update(invoiceToUpdate);

        //When
        mockMvc.perform(put(url + invoiceToUpdate.getId())
            .contentType(MediaType.APPLICATION_ATOM_XML)
            .content(mapper.writeValueAsBytes(invoiceToUpdate))
            .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(status().isUnsupportedMediaType());

        //Then
        verify(invoiceService, never()).update(invoiceToUpdate);
    }

    @Test
    void shouldReturnBadRequestStatusWhileUpdatingNullAsInvoice() throws Exception {
        //When
        mockMvc.perform(put(url + 1)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(mapper.writeValueAsBytes(null))
            .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        //Then
        verify(invoiceService, never()).update(null);
    }

    @Test
    void shouldReturnBadRequestStatusWhileUpdatingInvoiceWhenPassedIdIsDifferentThanInvoiceId() throws Exception {
        //Given
        Invoice invoiceToUpdate = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).exists(invoiceToUpdate.getId());
        doReturn(invoiceToUpdate).when(invoiceService).update(invoiceToUpdate);

        //When
        mockMvc.perform(put(url + invoiceToUpdate.getId() + 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoiceToUpdate))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        //Then
        verify(invoiceService, never()).exists(invoiceToUpdate.getId());
        verify(invoiceService, never()).update(invoiceToUpdate);
    }

    @Test
    void shouldReturnNotFoundStatusWhileUpdatingInvoice() throws Exception {
        //Given
        Invoice invoiceToUpdate = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(invoiceService).exists(invoiceToUpdate.getId());
        doThrow(ServiceOperationException.class).when(invoiceService).update(invoiceToUpdate);

        //When
        mockMvc.perform(put(url + invoiceToUpdate.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoiceToUpdate))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        //Then
        verify(invoiceService, times(1)).exists(invoiceToUpdate.getId());
        verify(invoiceService, never()).update(invoiceToUpdate);
    }

    @Test
    void shouldReturnInternalServerErrorWhileUpdatingInvoice() throws Exception {
        //Given
        Invoice invoiceToUpdate = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).exists(invoiceToUpdate.getId());
        doThrow(ServiceOperationException.class).when(invoiceService).update(invoiceToUpdate);

        //When
        mockMvc.perform(put(url + invoiceToUpdate.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoiceToUpdate))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        //Then
        verify(invoiceService, times(1)).exists(invoiceToUpdate.getId());
        verify(invoiceService, times(1)).update(invoiceToUpdate);
    }

    @Test
    void shouldReturnNotFoundStatusWhileRemovingInvoice() throws Exception {
        //Given
        Invoice invoiceToUpdate = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(invoiceService).exists(invoiceToUpdate.getId());
        doThrow(ServiceOperationException.class).when(invoiceService).update(invoiceToUpdate);

        //When
        mockMvc.perform(put(url + invoiceToUpdate.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoiceToUpdate))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        //Then
        verify(invoiceService, times(1)).exists(invoiceToUpdate.getId());
        verify(invoiceService, never()).update(invoiceToUpdate);
    }

    @Test
    void shouldReturnInternalServerErrorWhileRemovingInvoice() throws Exception {
        //Given
        Invoice invoiceToDelete = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).exists(invoiceToDelete.getId());
        doThrow(ServiceOperationException.class).when(invoiceService).deleteById(invoiceToDelete.getId());

        //When
        mockMvc.perform(delete(url + invoiceToDelete.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(invoiceToDelete))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        //Then
        verify(invoiceService, times(1)).exists(invoiceToDelete.getId());
        verify(invoiceService, times(1)).deleteById(invoiceToDelete.getId());
    }
}
