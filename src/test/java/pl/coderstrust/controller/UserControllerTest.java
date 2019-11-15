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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderstrust.generators.UserGenerator;
import pl.coderstrust.model.User;
import pl.coderstrust.service.ServiceOperationException;
import pl.coderstrust.service.UserService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@WithMockUser(roles = "USER")
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    String url = "/users/";

    @Test
    void shouldReturnUserById() throws Exception {
        //Given
        User userToGet = UserGenerator.getRandomUser();
        doReturn(Optional.of(userToGet)).when(userService).getById(userToGet.getId());

        //When
        mockMvc.perform(get(String.format("%s%d", url, userToGet.getId()))
            .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(mapper.writeValueAsString(userToGet)));

        //Then
        verify(userService, times(1)).getById(userToGet.getId());
    }

    @Test
    void shouldReturnNotAcceptableStatusDuringGettingUserByIdWithNotSupportedMediaType() throws Exception {
        //Given
        User userToGet = UserGenerator.getRandomUser();
        doReturn(Optional.of(userToGet)).when(userService).getById(userToGet.getId());

        //When
        mockMvc.perform(get(String.format("%s%d", url, userToGet.getId()))
            .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(status().isNotAcceptable());

        //Then
        verify(userService, never()).getAll();
    }

    @Test
    void shouldReturnNotFoundStatusWhileGettingNonExistingUserById() throws Exception {
        //Given
        User userToGet = UserGenerator.getRandomUser();
        doReturn(Optional.empty()).when(userService).getById(userToGet.getId());

        //When
        mockMvc.perform(get(String.format("%s%d", url, userToGet.getId())))
            .andExpect(status().isNotFound());

        //Then
        verify(userService, times(1)).getById(userToGet.getId());
    }

    @Test
    void shouldReturnInternalServerErrorStatusDuringGettingUserByIdWhenSomethingWentWrongOnServer() throws Exception {
        //Given
        User userToGet = UserGenerator.getRandomUser();
        doThrow(ServiceOperationException.class).when(userService).getById(userToGet.getId());

        //When
        mockMvc.perform(get(String.format("%s%d", url, userToGet.getId())))
            .andExpect(status().isInternalServerError());

        //Then
        verify(userService, times(1)).getById(userToGet.getId());
    }

    @Test
    void shouldReturnUserByEmail() throws Exception {
        //Given
        User userToGet = UserGenerator.getRandomUser();
        String endPoint = String.format("byEmail?email=%s", userToGet.getEmail());
        doReturn(Optional.of(userToGet)).when(userService).getByEmail(userToGet.getEmail());

        //When
        mockMvc.perform(get(String.format("%s%s", url, endPoint)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(mapper.writeValueAsString(userToGet)));

        //Then
        verify(userService, times(1)).getByEmail(userToGet.getEmail());
    }

    @Test
    void shouldReturnBadRequestStatusWhileGettingUserWithNullEmail() throws Exception {
        //When
        mockMvc.perform(get(String.format("%s%d", url, null)))
            .andExpect(status().isBadRequest());

        //Then
        verify(userService, never()).getByEmail(null);
    }

    @Test
    void shouldReturnNotFoundStatusWhileGettingNonExistingUserByEmail() throws Exception {
        //Given
        User userToGet = UserGenerator.getRandomUser();
        doReturn(Optional.empty()).when(userService).getByEmail(userToGet.getEmail());
        String endPoint = String.format("byEmail?email=%s", userToGet.getEmail());

        //When
        mockMvc.perform(get(String.format("%s%s", url, endPoint)))
            .andExpect(status().isNotFound());

        //Then
        verify(userService, times(1)).getByEmail(userToGet.getEmail());
    }

    @Test
    void shouldReturnInternalServerErrorStatusDuringGettingUserByEmailWhenSomethingWentWrongOnServer() throws Exception {
        //Given
        User userToGet = UserGenerator.getRandomUser();
        doThrow(ServiceOperationException.class).when(userService).getByEmail(userToGet.getEmail());
        String endPoint = String.format("byEmail?email=%s", userToGet.getEmail());

        //When
        mockMvc.perform(get(String.format("%s%s", url, endPoint)))
            .andExpect(status().isInternalServerError());

        //Then
        verify(userService, times(1)).getByEmail(userToGet.getEmail());
    }

    @Test
    void shouldRemoveUserById() throws Exception {
        //Given
        User userToDelete = UserGenerator.getRandomUser();
        doReturn(true).when(userService).userExistsById(userToDelete.getId());
        doNothing().when(userService).deleteById(userToDelete.getId());

        //When
        mockMvc.perform(delete(String.format("%s%d", url, userToDelete.getId())))
            .andExpect(status().isNoContent());

        //Then
        verify(userService, times(1)).userExistsById(userToDelete.getId());
        verify(userService, times(1)).deleteById(userToDelete.getId());
    }

    @Test
    void shouldReturnNotFoundStatusWhileRemovingNotExistingUserById() throws Exception {
        //Given
        User userToDelete = UserGenerator.getRandomUser();
        doReturn(false).when(userService).userExistsById(userToDelete.getId());
        doThrow(ServiceOperationException.class).when(userService).update(userToDelete);

        //When
        mockMvc.perform(delete(String.format("%s%d", url, userToDelete.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(userToDelete))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        //Then
        verify(userService, times(1)).userExistsById(userToDelete.getId());
        verify(userService, never()).update(userToDelete);
    }

    @Test
    void shouldReturnInternalServerErrorStatusDuringRemovingUserByIdWhenSomethingWentWrongOnServer() throws Exception {
        //Given
        User userToDelete = UserGenerator.getRandomUser();
        doReturn(true).when(userService).userExistsById(userToDelete.getId());
        doThrow(ServiceOperationException.class).when(userService).deleteById(userToDelete.getId());

        //When
        mockMvc.perform(delete(String.format("%s%d", url, userToDelete.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(userToDelete))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        //Then
        verify(userService, times(1)).userExistsById(userToDelete.getId());
        verify(userService, times(1)).deleteById(userToDelete.getId());
    }

    @Test
    void shouldRemoveAllUsers() throws Exception {
        //Given
        doNothing().when(userService).deleteAll();

        //When
        mockMvc.perform(delete(url))
            .andExpect(status().isNoContent());

        //Then
        verify(userService, times(1)).deleteAll();
    }

    @Test
    void shouldReturnInternalServerErrorStatusDuringRemovingAllUsersWhenSomethingWentWrongOnServer() throws Exception {
        //Given
        doThrow(ServiceOperationException.class).when(userService).deleteAll();

        //When
        mockMvc.perform(delete(url))
            .andExpect(status().isInternalServerError());

        //Then
        verify(userService, times(1)).deleteAll();
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        //Given
        Collection<User> users = Arrays.asList(UserGenerator.getRandomUser(), UserGenerator.getRandomUser());
        doReturn(users).when(userService).getAll();

        //When
        mockMvc.perform(get(url))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(mapper.writeValueAsString(users)));

        //Then
        verify(userService, times(1)).getAll();
    }

    @Test
    void shouldReturnNotAcceptableStatusDuringGettingAllUsersWithNotSupportedMediaType() throws Exception {
        //When
        mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(status().isNotAcceptable());

        //Then
        verify(userService, never()).getAll();
    }

    @Test
    void shouldReturnInternalServerErrorStatusDuringGettingAllUsersWhenSomethingWentWrongOnServer() throws Exception {
        //Given
        doThrow(ServiceOperationException.class).when(userService).getAll();

        //When
        mockMvc.perform(get(url))
            .andExpect(status().isInternalServerError());

        //Then
        verify(userService, times(1)).getAll();
    }

    @Test
    void shouldAddUser() throws Exception {
        //Given
        User userToAdd = UserGenerator.getRandomUser();
        doReturn(false).when(userService).userExistsById(userToAdd.getId());
        doReturn(userToAdd).when(userService).add(userToAdd);
        String endPoint = "register";

        //When
        mockMvc.perform(post(String.format("%s%s",url, endPoint))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(userToAdd)))
            .andExpect(status().isCreated());

        //Then
        verify(userService, times(1)).userExistsByEmail(userToAdd.getEmail());
        verify(userService, times(1)).add(userToAdd);
    }

    @Test
    void shouldReturnUnsupportedMediaTypeStatusDuringAddingUserWithNotSupportedMediaType() throws Exception {
        //Given
        User userToAdd = UserGenerator.getRandomUser();
        doReturn(userToAdd).when(userService).add(userToAdd);
        String endPoint = "register";

        //When
        mockMvc.perform(post(String.format("%s%s",url, endPoint))
            .contentType(MediaType.APPLICATION_ATOM_XML)
            .content(mapper.writeValueAsBytes(userToAdd))
            .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(status().isUnsupportedMediaType());

        //Then
        verify(userService, never()).add(userToAdd);
    }

    @Test
    void shouldReturnConflictStatusDuringAddingUserWhenUserExistsInDatabase() throws Exception {
        //Given
        User userToAdd = UserGenerator.getRandomUser();
        doReturn(true).when(userService).userExistsByEmail(userToAdd.getEmail());
        String endPoint = "register";

        //When
        mockMvc.perform(post(String.format("%s%s",url, endPoint))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(userToAdd))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());

        //Then
        verify(userService, times(1)).userExistsByEmail(userToAdd.getEmail());
        verify(userService, never()).add(userToAdd);
    }

    @Test
    void shouldReturnBadRequestStatusDuringAddingNullAsUser() throws Exception {
        //Given
        String endPoint = "register";

        //When
        mockMvc.perform(post(String.format("%s%s",url, endPoint))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(null))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        //Then
        verify(userService, never()).add(null);
    }

    @Test
    void shouldReturnInternalServerErrorStatusDuringAddingUserWhenSomethingWentWrongOnServer() throws Exception {
        //Given
        User userToAdd = UserGenerator.getRandomUser();
        doReturn(false).when(userService).userExistsByEmail(userToAdd.getEmail());
        doThrow(ServiceOperationException.class).when(userService).add(userToAdd);
        String endPoint = "register";

        //When
        mockMvc.perform(post(String.format("%s%s",url, endPoint))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(userToAdd))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        //Then
        verify(userService, times(1)).userExistsByEmail(userToAdd.getEmail());
        verify(userService, times(1)).add(userToAdd);
    }

    @Test
    void shouldUpdateUser() throws Exception {
        //Given
        User userToUpdate = UserGenerator.getRandomUser();
        doReturn(true).when(userService).userExistsById(userToUpdate.getId());
        doReturn(userToUpdate).when(userService).update(userToUpdate);

        //When
        mockMvc.perform(put(String.format("%s%d", url, userToUpdate.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(userToUpdate))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().json(mapper.writeValueAsString(userToUpdate)));

        //Then
        verify(userService, times(1)).userExistsById(userToUpdate.getId());
        verify(userService, times(1)).update(userToUpdate);
    }

    @Test
    void shouldReturnInternalServerErrorStatusDuringUpdatingUserWhenSomethingWentWrongOnServer() throws Exception {
        //Given
        User userToUpdate = UserGenerator.getRandomUser();
        doReturn(true).when(userService).userExistsById(userToUpdate.getId());
        doThrow(ServiceOperationException.class).when(userService).update(userToUpdate);

        //When
        mockMvc.perform(put(String.format("%s%d", url, userToUpdate.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(userToUpdate))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());

        //Then
        verify(userService, times(1)).userExistsById(userToUpdate.getId());
        verify(userService, times(1)).update(userToUpdate);
    }

    @Test
    void shouldReturnUnsupportedMediaTypeStatusDuringUpdatingUserWithNotSupportedMediaType() throws Exception {
        //Given
        User userToUpdate = UserGenerator.getRandomUser();
        doReturn(true).when(userService).userExistsById(userToUpdate.getId());
        doThrow(ServiceOperationException.class).when(userService).update(userToUpdate);

        //When
        mockMvc.perform(put(String.format("%s%d", url, userToUpdate.getId()))
            .contentType(MediaType.APPLICATION_ATOM_XML)
            .content(mapper.writeValueAsBytes(userToUpdate))
            .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(status().isUnsupportedMediaType());

        //Then
        verify(userService, never()).update(userToUpdate);
    }

    @Test
    void shouldReturnBadRequestStatusDuringUpdatingNullAsUser() throws Exception {
        //When
        mockMvc.perform(put(String.format("%s%d", url, null))
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(mapper.writeValueAsBytes(null))
            .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        //Then
        verify(userService, never()).update(null);
    }

    @Test
    void shouldReturnBadRequestStatusDuringUpdatingUserWhenPassedIdIsDifferentThanUserId() throws Exception {
        //Given
        User userToUpdate = UserGenerator.getRandomUser();
        doReturn(true).when(userService).userExistsById(userToUpdate.getId());
        doReturn(userToUpdate).when(userService).update(userToUpdate);

        //When
        mockMvc.perform(put(String.format("%s%d", url, userToUpdate.getId() + 1))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(userToUpdate))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        //Then
        verify(userService, never()).userExistsById(userToUpdate.getId());
        verify(userService, never()).update(userToUpdate);
    }

    @Test
    void shouldReturnNotFoundStatusDuringUpdatingNotExistingUser() throws Exception {
        //Given
        User userToUpdate = UserGenerator.getRandomUser();
        doReturn(false).when(userService).userExistsById(userToUpdate.getId());
        doThrow(ServiceOperationException.class).when(userService).update(userToUpdate);

        //When
        mockMvc.perform(put(String.format("%s%d", url, userToUpdate.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsBytes(userToUpdate))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        //Then
        verify(userService, times(1)).userExistsById(userToUpdate.getId());
        verify(userService, never()).update(userToUpdate);
    }
}
