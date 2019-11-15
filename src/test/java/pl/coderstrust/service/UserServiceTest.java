package pl.coderstrust.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.database.UserDatabase;
import pl.coderstrust.generators.UserGenerator;
import pl.coderstrust.model.User;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserDatabase database;

    @Mock
    private BCryptPasswordEncoder mockBCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void saveMethodShouldThrowExceptionForNullAsUser() {
        assertThrows(IllegalArgumentException.class, () -> userService.add(null));
    }

    @Test
    void addMethodShouldThrowExceptionIfUserAlreadyExist() throws DatabaseOperationException, ServiceOperationException {
        User user = UserGenerator.getRandomUser();
        doReturn(true).when(database).userExistsById(user.getId());

        assertThrows(ServiceOperationException.class, () -> userService.add(user));
        verify(database).userExistsById(user.getId());
        verify(database, never()).save(user);
    }

    @Test
    void shouldAddUser() throws DatabaseOperationException, ServiceOperationException {
        User userToAdd = UserGenerator.getRandomUser();
        User addedUser = UserGenerator.getRandomUser();
        when(database.userExistsById(userToAdd.getId())).thenReturn(false);
        when(mockBCryptPasswordEncoder.encode(userToAdd.getPassword())).thenReturn(userToAdd.getPassword());
        when(database.save(userToAdd)).thenReturn(addedUser);

        User result = userService.add(userToAdd);

        assertEquals(addedUser, result);
        verify(database).save(userToAdd);
        verify(database).userExistsById(userToAdd.getId());
        verify(mockBCryptPasswordEncoder,times(1)).encode(userToAdd.getPassword());
    }

    @Test
    void shouldAddUserWithNullId() throws DatabaseOperationException, ServiceOperationException {
        User userToAdd = UserGenerator.getRandomUserWithNullId();
        User addedUser = UserGenerator.getRandomUserWithNullId();
        when(mockBCryptPasswordEncoder.encode(userToAdd.getPassword())).thenReturn(userToAdd.getPassword());
        when(database.save(userToAdd)).thenReturn(addedUser);

        User result = userService.add(userToAdd);

        assertEquals(addedUser, result);
        verify(database).save(userToAdd);
        verify(mockBCryptPasswordEncoder,times(1)).encode(userToAdd.getPassword());
    }

    @Test
    void addMethodShouldThrowExceptionWhenAnErrorOccurDuringAddingUserToDatabase() throws DatabaseOperationException, ServiceOperationException {
        User user = UserGenerator.getRandomUser();
        when(mockBCryptPasswordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());
        when(database.save(user)).thenThrow(DatabaseOperationException.class);

        assertThrows(ServiceOperationException.class, () -> userService.add(user));
        verify(mockBCryptPasswordEncoder, times(1)).encode(user.getPassword());
    }

    @Test
    void updateMethodShouldThrowExceptionForNullAsUser() {
        assertThrows(IllegalArgumentException.class, () -> userService.update(null));
    }

    @Test
    void updateUserMethodShouldThrowExceptionWhenUserNotExist() throws DatabaseOperationException, ServiceOperationException {
        User user = UserGenerator.getRandomUser();
        doReturn(false).when(database).userExistsById(user.getId());

        assertThrows(ServiceOperationException.class, () -> userService.update(user));
        verify(database).userExistsById(user.getId());
        verify(database, never()).save(user);
    }

    @Test
    void updateMethodShouldThrowExceptionForNullUserId() {
        User userWithNullId = UserGenerator.getRandomUserWithNullId();
        assertThrows(ServiceOperationException.class, () -> userService.update(userWithNullId));
    }

    @Test
    void updateMethodShouldUpdateUser() throws ServiceOperationException, DatabaseOperationException {
        User userToUpdate = UserGenerator.getRandomUser();
        User userUpdated = UserGenerator.getRandomUser();
        when(database.userExistsById(userToUpdate.getId())).thenReturn(true);
        when(database.save(userToUpdate)).thenReturn(userUpdated);

        User result = userService.update(userToUpdate);
        assertEquals(userUpdated, result);
        verify(database).save(userToUpdate);
        verify(database).userExistsById(userToUpdate.getId());
    }

    @Test
    void updateMethodShouldThrowExceptionWhenAnErrorOccurDuringUpdatingUserInDatabase() throws DatabaseOperationException, ServiceOperationException {
        User user = UserGenerator.getRandomUser();
        when(database.userExistsById(user.getId())).thenReturn(true);
        when(database.save(user)).thenThrow(DatabaseOperationException.class);

        assertThrows(ServiceOperationException.class, () -> userService.update(user));
        verify(database).userExistsById(user.getId());
        verify(database).save(user);
    }

    @Test
    void deleteByIdMethodShouldThrowExceptionForNullAsId() {
        assertThrows(IllegalArgumentException.class, () -> new UserService(database, mockBCryptPasswordEncoder).deleteById(null));
    }

    @Test
    void deleteByIdMethodShouldThrowExceptionWhenAnErrorOccurDuringDeletingUserByIdFromDatabase() throws DatabaseOperationException, ServiceOperationException {
        User user = UserGenerator.getRandomUser();
        when(database.userExistsById(user.getId())).thenReturn(true);
        doThrow(DatabaseOperationException.class).when(database).delete(user.getId());


        assertThrows(ServiceOperationException.class, () -> userService.deleteById(user.getId()));
        verify(database).delete(user.getId());
    }

    @Test
    void deleteByIdMethodShouldThrowExceptionWhenUserDoesNotExist() throws DatabaseOperationException, ServiceOperationException {
        doReturn(false).when(database).userExistsById(1L);

        assertThrows(ServiceOperationException.class, () -> userService.deleteById(1L));
        verify(database).userExistsById(1L);
        verify(database, never()).delete(1L);
    }

    @Test
    void shouldDeleteUserById() throws ServiceOperationException, DatabaseOperationException {
        doReturn(true).when(database).userExistsById(1L);

        userService.deleteById(1L);
        verify(database).userExistsById(1L);
        verify(database).delete(1L);
    }

    @Test
    void deleteByEmailMethodShouldThrowExceptionForNullAsEmail() {
        assertThrows(IllegalArgumentException.class, () -> new UserService(database, mockBCryptPasswordEncoder).deleteByEmail(null));
    }

    @Test
    void deleteByEmailMethodShouldThrowExceptionWhenAnErrorOccurDuringDeletingUserByEmailFromDatabase() throws DatabaseOperationException, ServiceOperationException {
        User user = UserGenerator.getRandomUser();
        when(database.userExistsByEmail(user.getEmail())).thenReturn(true);
        doThrow(DatabaseOperationException.class).when(database).deleteByEmail(user.getEmail());


        assertThrows(ServiceOperationException.class, () -> userService.deleteByEmail(user.getEmail()));
        verify(database).deleteByEmail(user.getEmail());
    }

    @Test
    void deleteByEmailMethodShouldThrowExceptionWhenUserDoesNotExist() throws DatabaseOperationException, ServiceOperationException {
        doReturn(false).when(database).userExistsByEmail("sample@mail.com");

        assertThrows(ServiceOperationException.class, () -> userService.deleteByEmail("sample@mail.com"));
        verify(database).userExistsByEmail("sample@mail.com");
        verify(database, never()).deleteByEmail("sample@mail.com");
    }

    @Test
    void shouldDeleteUserByEmail() throws ServiceOperationException, DatabaseOperationException {
        doReturn(true).when(database).userExistsByEmail("sample@mail.com");

        userService.deleteByEmail("sample@mail.com");
        verify(database).userExistsByEmail("sample@mail.com");
        verify(database).deleteByEmail("sample@mail.com");
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullAsId() {
        assertThrows(IllegalArgumentException.class, () -> userService.getById(null));
    }

    @Test
    void shouldGetUserById() throws DatabaseOperationException, ServiceOperationException {
        User user = UserGenerator.getRandomUserWithSpecificId(1L);
        doReturn(Optional.of(user)).when(database).getById(user.getId());

        Optional<User> result = userService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(database).getById(1L);
    }

    @Test
    void getByIdMethodShouldThrowExceptionWhenAnErrorOccurDuringGettingUserByIdFromDatabase() throws DatabaseOperationException, ServiceOperationException {
        User user = UserGenerator.getRandomUser();
        when(database.getById(user.getId())).thenThrow(DatabaseOperationException.class);

        assertThrows(ServiceOperationException.class, () -> userService.getById(user.getId()));
        verify(database).getById(user.getId());
    }

    @Test
    void getByEmailMethodShouldThrowExceptionForNullAsEmail() {
        assertThrows(IllegalArgumentException.class, () -> userService.getByEmail(null));
    }

    @Test
    void shouldGetUserByEmail() throws DatabaseOperationException, ServiceOperationException {
        Optional<User> expected = Optional.of(UserGenerator.getRandomUser());
        doReturn(expected).when(database).getByEmail("sample@mail.com");

        Optional<User> actual = userService.getByEmail("sample@mail.com");

        assertEquals(expected, actual);
        verify(database).getByEmail("sample@mail.com");
    }

    @Test
    void shouldReturnAllUsers() throws ServiceOperationException, DatabaseOperationException {
        List<User> expected = List.of(UserGenerator.getRandomUser(), UserGenerator.getRandomUser());
        doReturn(expected).when(database).getAll();

        Collection<User> actual = userService.getAll();

        assertEquals(expected, actual);
        verify(database).getAll();
    }

    @Test
    void getAllMethodShouldThrowExceptionWhenAnErrorOccurDuringGettingAllUsersFromDatabase() throws DatabaseOperationException, ServiceOperationException {
        when(database.getAll()).thenThrow(DatabaseOperationException.class);

        UserService userService = new UserService(database, mockBCryptPasswordEncoder);

        assertThrows(ServiceOperationException.class, () -> userService.getAll());
        verify(database).getAll();
    }

    @Test
    void shouldDeleteAllUsers() throws ServiceOperationException, DatabaseOperationException {
        doNothing().when(database).deleteAll();

        userService.deleteAll();

        verify(database).deleteAll();
    }

    @Test
    void deleteAllMethodShouldThrowExceptionWhenAnErrorOccurDuringDeletingAllUsersFromDatabase() throws DatabaseOperationException, ServiceOperationException {
        doThrow(DatabaseOperationException.class).when(database).deleteAll();

        assertThrows(ServiceOperationException.class, () -> userService.deleteAll());
        verify(database).deleteAll();
    }

    @Test
    void shouldReturnTrueWhenUserExistsInDatabaseBaseOnId() throws DatabaseOperationException, ServiceOperationException {
        User user = UserGenerator.getRandomUser();
        when(database.userExistsById(user.getId())).thenReturn(true);

        assertTrue(userService.userExistsById(user.getId()));
        verify(database).userExistsById(user.getId());
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotExistsInDatabaseBaseOnId() throws DatabaseOperationException, ServiceOperationException {
        User user = UserGenerator.getRandomUser();
        when(database.userExistsById(user.getId())).thenReturn(false);

        assertFalse(userService.userExistsById(user.getId()));
        verify(database).userExistsById(user.getId());
    }

    @Test
    void userExistsByIdMethodShouldThrowExceptionForNullAsId() {
        assertThrows(IllegalArgumentException.class, () -> userService.userExistsById(null));
    }

    @Test
    void userExistsByIdMethodShouldThrowExceptionWhenAnErrorOccurDuringCheckingUserExists() throws DatabaseOperationException, ServiceOperationException {
        User user = UserGenerator.getRandomUser();
        when(database.userExistsById(user.getId())).thenThrow(DatabaseOperationException.class);

        assertThrows(ServiceOperationException.class, () -> userService.userExistsById(user.getId()));
        verify(database).userExistsById(user.getId());
    }

    @Test
    void shouldReturnTrueWhenUserExistsInDatabaseBaseOnEmail() throws DatabaseOperationException, ServiceOperationException {
        User user = UserGenerator.getRandomUser();
        when(database.userExistsByEmail(user.getEmail())).thenReturn(true);

        assertTrue(userService.userExistsByEmail(user.getEmail()));
        verify(database).userExistsByEmail(user.getEmail());
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotExistsInDatabaseBaseOnEmail() throws DatabaseOperationException, ServiceOperationException {
        User user = UserGenerator.getRandomUser();
        when(database.userExistsByEmail(user.getEmail())).thenReturn(false);

        assertFalse(userService.userExistsByEmail(user.getEmail()));
        verify(database).userExistsByEmail(user.getEmail());
    }

    @Test
    void userExistsByEmailMethodShouldThrowExceptionForNullAsEmail() {
        assertThrows(IllegalArgumentException.class, () -> userService.userExistsByEmail(null));
    }

    @Test
    void userExistsByEmailMethodShouldThrowExceptionWhenAnErrorOccurDuringCheckingUserExists() throws DatabaseOperationException, ServiceOperationException {
        User user = UserGenerator.getRandomUser();
        when(database.userExistsByEmail(user.getEmail())).thenThrow(DatabaseOperationException.class);

        assertThrows(ServiceOperationException.class, () -> userService.userExistsByEmail(user.getEmail()));
        verify(database).userExistsByEmail(user.getEmail());
    }

    @Test
    void shouldReturnNumberOfUsers() throws ServiceOperationException, DatabaseOperationException {
        doReturn(10L).when(database).count();

        long result = userService.count();

        assertEquals(10L, result);
        verify(database).count();
    }

    @Test
    void countMethodShouldThrowExceptionWhenAnErrorOccurDuringGettingNumberOfUsers() throws DatabaseOperationException, ServiceOperationException {
        when(database.count()).thenThrow(DatabaseOperationException.class);

        assertThrows(ServiceOperationException.class, () -> userService.count());
        verify(database).count();
    }
}
