package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.NonTransientDataAccessException;
import pl.coderstrust.database.hibernate.HibernateUserModelMapper;
import pl.coderstrust.database.hibernate.HibernateUserModelMapperImpl;
import pl.coderstrust.database.hibernate.UserRepository;
import pl.coderstrust.generators.UserGenerator;
import pl.coderstrust.model.User;
import pl.coderstrust.service.ServiceOperationException;

@ExtendWith(MockitoExtension.class)
class HibernateUserDatabaseTest {
    
    private HibernateUserDatabase database;
    private HibernateUserModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @Test
    void constructorShouldThrowExceptionForNullInvoiceRepository() {
        assertThrows(IllegalArgumentException.class, () -> new HibernateUserDatabase(null, modelMapper));
    }

    @Test
    void constructorShouldThrowExceptionForNullModelMapper() {
        assertThrows(IllegalArgumentException.class, () -> new HibernateUserDatabase(userRepository, null));
    }


    @BeforeEach
    void setup() {
        modelMapper = new HibernateUserModelMapperImpl();
        database = new HibernateUserDatabase(userRepository, modelMapper);
    }

    @Test
    void shouldSave() throws DatabaseOperationException {
        //given
        User userToBeSaved = UserGenerator.getRandomUser();
        pl.coderstrust.database.hibernate.User hibernateUserToBeSaved = modelMapper.mapToHibernateUser(userToBeSaved);
        doReturn(hibernateUserToBeSaved).when(userRepository).save(hibernateUserToBeSaved);

        //when
        User savedUser = database.save(userToBeSaved);

        //then
        assertEquals(userToBeSaved, savedUser);
        verify(userRepository).save(hibernateUserToBeSaved);
    }

    @Test
    void saveMethodShouldThrowExceptionForNullUser() {
        assertThrows(IllegalArgumentException.class, () -> database.save(null));
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionIsThrownWhenSavingUser() {
        //given
        User user = UserGenerator.getRandomUser();
        pl.coderstrust.database.hibernate.User hibernateUser = modelMapper.mapToHibernateUser(user);
        doThrow(new NonTransientDataAccessException("") {}).when(userRepository).save(hibernateUser);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.save(user));
        verify(userRepository).save(modelMapper.mapToHibernateUser(user));
    }

    @Test
    void shouldDeleteUserByEmail() throws DatabaseOperationException, ServiceOperationException {
        //given
        when(userRepository.existsByEmail("sample@mail.com")).thenReturn(true);

        //when
        database.deleteByEmail("sample@mail.com");

        //then
        verify(userRepository).deleteUserByEmail("sample@mail.com");
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> database.deleteByEmail(null));
    }

    @Test
    void deleteMethodShouldThrowExceptionForDeletingNotExistingUserBaseOnEmail() {
        //given
        when(userRepository.existsByEmail("sample@mail.com")).thenReturn(false);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.deleteByEmail("sample@mail.com"));
        verify(userRepository).existsByEmail("sample@mail.com");
        verify(userRepository, never()).deleteUserByEmail("sample@mail.com");
    }

    @Test
    void deleteMethodShouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionOccurDuringDeletingUserBaseOnEmail() {
        //given
        when(userRepository.existsByEmail("sample@mail.com")).thenReturn(true);
        doThrow(new NonTransientDataAccessException("") {}).when(userRepository).deleteUserByEmail("sample@mail.com");

        //then
        assertThrows(DatabaseOperationException.class, () -> database.deleteByEmail("sample@mail.com"));
        verify(userRepository).deleteUserByEmail("sample@mail.com");
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNoSuchElementExceptionIsThrownWhenDeletingUserBaseOnEmail() {
        //given
        when(userRepository.existsByEmail("sample@mail.com")).thenReturn(true);
        doThrow(new NoSuchElementException()).when(userRepository).deleteUserByEmail("sample@mail.com");

        //then
        assertThrows(DatabaseOperationException.class, () -> database.deleteByEmail("sample@mail.com"));
        verify(userRepository).deleteUserByEmail("sample@mail.com");
    }

    @Test
    void shouldDeleteUserById() throws DatabaseOperationException {
        //given
        when(userRepository.existsById(10L)).thenReturn(true);

        //when
        database.delete(10L);

        //then
        verify(userRepository).deleteById(10L);
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.delete(null));
    }

    @Test
    void deleteMethodShouldThrowExceptionForDeletingNotExistingUserBaseOnId() {
        //given
        when(userRepository.existsById(10L)).thenReturn(false);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(10L));
        verify(userRepository).existsById(10L);
        verify(userRepository, never()).deleteById(10L);
    }

    @Test
    void deleteMethodShouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionOccurDuringDeletingInvoiceBaseOnId() {
        //given
        when(userRepository.existsById(10L)).thenReturn(true);
        doThrow(new NonTransientDataAccessException("") {}).when(userRepository).deleteById(10L);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(10L));
        verify(userRepository).deleteById(10L);
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNoSuchElementExceptionIsThrownWhenDeletingUserBaseOnId() {
        //given
        when(userRepository.existsById(10L)).thenReturn(true);
        doThrow(new NoSuchElementException()).when(userRepository).deleteById(10L);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(10L));
        verify(userRepository).deleteById(10L);
    }

    @Test
    void shouldGetUserById() throws DatabaseOperationException {
        //given
        User user = UserGenerator.getRandomUser();
        pl.coderstrust.database.hibernate.User hibernateUser = modelMapper.mapToHibernateUser(user);
        doReturn(Optional.of(hibernateUser)).when(userRepository).findById(hibernateUser.getId());

        //when
        Optional<User> gotUser = database.getById(user.getId());

        //then
        assertTrue(gotUser.isPresent());
        assertEquals(user, gotUser.get());
        verify(userRepository).findById(hibernateUser.getId());
    }

    @Test
    void shouldReturnEmptyOptionalWhenNonExistingUserIsGotById() throws DatabaseOperationException {
        //given
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        //when
        Optional<User> gotUser = database.getById(10L);

        //then
        assertTrue(gotUser.isEmpty());
        verify(userRepository).findById(10L);
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNoSuchElementExceptionIsThrownWhenGettingById() {
        //given
        doThrow(new NoSuchElementException()).when(userRepository).findById(10L);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getById(10L));
        verify(userRepository).findById(10L);
    }

    @Test
    void shouldGetUserByEmail() throws DatabaseOperationException {
        //given
        User user = UserGenerator.getRandomUser();
        pl.coderstrust.database.hibernate.User hibernateUser = modelMapper.mapToHibernateUser(user);
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(hibernateUser));

        //when
        Optional<User> gotUser = database.getByEmail(user.getEmail());

        //then
        assertTrue(gotUser.isPresent());
        assertEquals(user, gotUser.get());
        verify(userRepository).findUserByEmail(user.getEmail());
    }

    @Test
    void shouldReturnEmptyOptionalWhenNonExistingUserIsGotByEmail() throws DatabaseOperationException {
        //given
        when(userRepository.findUserByEmail("sample@mail.com")).thenReturn(Optional.empty());

        //when
        Optional<User> gotUser = database.getByEmail("sample@mail.com");

        //then
        assertTrue(gotUser.isEmpty());
        verify(userRepository).findUserByEmail("sample@mail.com");
    }

    @Test
    void getByEmailMethodShouldThrowExceptionForNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> database.getByEmail(null));
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNoSuchElementExceptionIsThrownWhenGettingByEmail() {
        //given
        doThrow(new NoSuchElementException()).when(userRepository).findUserByEmail("sample@mail.com");

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getByEmail("sample@mail.com"));
        verify(userRepository).findUserByEmail("sample@mail.com");
    }

    @Test
    void shouldGetAllUsers() throws DatabaseOperationException {
        //given
        User user1 = UserGenerator.getRandomUser();
        User user2 = UserGenerator.getRandomUser();
        Collection<User> userList = List.of(user1, user2);
        Collection<pl.coderstrust.database.hibernate.User> hibernateInvoiceList = modelMapper.mapToHibernateUsers(userList);
        doReturn(hibernateInvoiceList).when(userRepository).findAll();

        //when
        Collection<User> gotList = database.getAll();

        //then
        assertEquals(gotList, userList);
        verify(userRepository).findAll();
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionIsThrownWhenGettingAllUsers() {
        //given
        doThrow(new NonTransientDataAccessException("") {}).when(userRepository).findAll();

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getAll());
        verify(userRepository).findAll();
    }

    @Test
    void shouldDeleteAllUsers() throws DatabaseOperationException {
        //given
        doNothing().when(userRepository).deleteAll();

        //when
        database.deleteAll();

        //then
        verify(userRepository).deleteAll();
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionIsThrownWhenDeletingAllUsers() {
        //given
        doThrow(new NonTransientDataAccessException("") {}).when(userRepository).deleteAll();

        //then
        assertThrows(DatabaseOperationException.class, () -> database.deleteAll());
        verify(userRepository).deleteAll();
    }

    @Test
    void shouldReturnTrueIfUserExistsBasedOnGivenId() throws DatabaseOperationException {
        //given
        when(userRepository.existsById(10L)).thenReturn(true);

        //then
        assertTrue(database.userExistsById(10L));
        verify(userRepository).existsById(10L);
    }

    @Test
    void shouldReturnFalseIfUserExistsBasedOnGivenId() throws DatabaseOperationException {
        //given
        when(userRepository.existsById(10L)).thenReturn(false);

        //then
        assertFalse(database.userExistsById(10L));
        verify(userRepository).existsById(10L);
    }

    @Test
    void existsBasedOnIdMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.userExistsById(null));
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionIsThrownWhenCheckingIfUserExistsBaseOnId() {
        //given
        doThrow(new NonTransientDataAccessException("") {}).when(userRepository).existsById(10L);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.userExistsById(10L));
        verify(userRepository).existsById(10L);
    }

    @Test
    void shouldReturnTrueIfUserExistsBasedOnGivenEmail() throws DatabaseOperationException {
        //given
        when(userRepository.existsByEmail("sample@mail.com")).thenReturn(true);

        //then
        assertTrue(database.userExistsByEmail("sample@mail.com"));
        verify(userRepository).existsByEmail("sample@mail.com");
    }

    @Test
    void shouldReturnFalseIfUserExistsBasedOnGivenEmail() throws DatabaseOperationException {
        //given
        when(userRepository.existsByEmail("sample@mail.com")).thenReturn(false);

        //then
        assertFalse(database.userExistsByEmail("sample@mail.com"));
        verify(userRepository).existsByEmail("sample@mail.com");
    }

    @Test
    void existsBasedOnEmailMethodShouldThrowExceptionForNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> database.userExistsByEmail(null));
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionIsThrownWhenCheckingIfUserExistsBaseOnEmail() throws DatabaseOperationException  {
        //given
        doThrow(new NonTransientDataAccessException("") {}).when(userRepository).existsByEmail("sample@mail.com");

        //then
        assertThrows(DatabaseOperationException.class, () -> database.userExistsByEmail("sample@mail.com"));
        verify(userRepository).existsByEmail("sample@mail.com");
    }

    @Test
    void shouldReturnNumberOfUsers() throws DatabaseOperationException {
        //given
        when(userRepository.count()).thenReturn(10L);

        //when
        long numberOfInvoices = database.count();

        //then
        assertEquals(10L, numberOfInvoices);
        verify(userRepository).count();
    }

    @Test
    void shouldThrowDatabaseOperationExceptionWhenNonTransientDataAccessExceptionIsThrownWhenCountingNumberOfUsers() {
        //given
        doThrow(new NonTransientDataAccessException("") {}).when(userRepository).count();

        //then
        assertThrows(DatabaseOperationException.class, () -> database.count());
        verify(userRepository).count();
    }
}
