package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import pl.coderstrust.database.mongo.MongoUserModelMapper;
import pl.coderstrust.database.mongo.MongoUserModelMapperImpl;
import pl.coderstrust.generators.UserGenerator;
import pl.coderstrust.model.User;
import pl.coderstrust.service.ServiceOperationException;

@ExtendWith(MockitoExtension.class)
class MongoUserDatabaseTest {

    private MongoUserDatabase database;
    private MongoUserModelMapper modelMapper;

    @Mock
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setup() {
        modelMapper = new MongoUserModelMapperImpl();
        database = new MongoUserDatabase(mongoTemplate, modelMapper);
    }

    @Test
    void constructorShouldThrowExceptionForNullMongoTemplate() {
        assertThrows(IllegalArgumentException.class, () -> new MongoUserDatabase(null, modelMapper));
    }

    @Test
    void constructorShouldThrowExceptionForNullModelMapper() {
        assertThrows(IllegalArgumentException.class, () -> new MongoUserDatabase(mongoTemplate, null));
    }

    @Test
    void shouldInsert() throws DatabaseOperationException {
        //given
        User user = UserGenerator.getRandomUser();
        Query findId = Query.query(Criteria.where("id").is(user.getId()));
        doReturn(null).when(mongoTemplate).findOne(findId, pl.coderstrust.database.mongo.User.class);
        User userToBeInserted = User.builder().withUser(user).withId(1L).build();
        doReturn(modelMapper.mapToMongoUser(userToBeInserted)).when(mongoTemplate).insert(modelMapper.mapToMongoUser(userToBeInserted));

        //when
        User insertedUser = database.save(user);

        //then
        assertEquals(userToBeInserted, insertedUser);
        verify(mongoTemplate).findOne(findId, pl.coderstrust.database.mongo.User.class);
        verify(mongoTemplate).insert(modelMapper.mapToMongoUser(userToBeInserted));
    }

    @Test
    void shouldUpdate() throws DatabaseOperationException {
        //given
        User userInDatabase = UserGenerator.getRandomUser();
        pl.coderstrust.database.mongo.User mongoUserInDatabase = modelMapper.mapToMongoUser(userInDatabase);
        Query findId = Query.query(Criteria.where("id").is(userInDatabase.getId()));
        doReturn(mongoUserInDatabase).when(mongoTemplate).findOne(findId, pl.coderstrust.database.mongo.User.class);
        User userUpdate = UserGenerator.getRandomUserWithSpecificId(userInDatabase.getId());
        pl.coderstrust.database.mongo.User mongoUserUpdate = modelMapper.mapToMongoUser(userUpdate);
        doReturn(mongoUserUpdate).when(mongoTemplate).save(mongoUserUpdate);

        //when
        User updatedUser = database.save(userUpdate);

        //then
        assertEquals(userUpdate, updatedUser);
        verify(mongoTemplate).findOne(findId, pl.coderstrust.database.mongo.User.class);
        verify(mongoTemplate).save(mongoUserUpdate);
    }

    @Test
    void saveMethodShouldThrowExceptionForNullUser() {
        assertThrows(IllegalArgumentException.class, () -> database.save(null));
    }

    @Test
    void saveMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringSearchingForUser() {
        //given
        User user = UserGenerator.getRandomUser();
        Query findId = Query.query(Criteria.where("id").is(user.getId()));
        doThrow(new MockitoException("") {}).when(mongoTemplate).findOne(findId, pl.coderstrust.database.mongo.User.class);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.save(user));
        verify(mongoTemplate).findOne(findId, pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void saveMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringInsertingUser() {
        //given
        User user = UserGenerator.getRandomUser();
        pl.coderstrust.database.mongo.User addedUser = modelMapper.mapToMongoUser(User.builder().withUser(user).withId(1L).build());
        pl.coderstrust.database.mongo.User mongoUser = modelMapper.mapToMongoUser(user);
        doThrow(new MockitoException("") {}).when(mongoTemplate).insert(mongoUser);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.save(user));
        verify(mongoTemplate).insert(addedUser);
    }

    @Test
    void saveMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringUpdatingUser() {
        //given
        User user = UserGenerator.getRandomUser();
        pl.coderstrust.database.mongo.User mongoUser = modelMapper.mapToMongoUser(user);
        Query findId = Query.query(Criteria.where("id").is(user.getId()));
        doReturn(mongoUser).when(mongoTemplate).findOne(findId, pl.coderstrust.database.mongo.User.class);
        doThrow(new MockitoException("") {}).when(mongoTemplate).save(mongoUser);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.save(user));
        verify(mongoTemplate).findOne(findId, pl.coderstrust.database.mongo.User.class);
        verify(mongoTemplate).save(mongoUser);
    }

    @Test
    void shouldDeleteById() throws DatabaseOperationException {
        //given
        User user = UserGenerator.getRandomUser();
        pl.coderstrust.database.mongo.User mongoUser = modelMapper.mapToMongoUser(user);
        Long id = user.getId();
        Query findId = Query.query(Criteria.where("id").is(id));
        when(mongoTemplate.findAndRemove(findId, pl.coderstrust.database.mongo.User.class)).thenReturn(mongoUser);

        //when
        database.delete(id);

        //then
        verify(mongoTemplate).findAndRemove(findId, pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.delete(null));
    }

    @Test
    void deleteMethodShouldThrowExceptionDuringDeletingNotExistingUserById() {
        //given
        Query findId = Query.query(Criteria.where("id").is(10L));
        when(mongoTemplate.findAndRemove(findId, pl.coderstrust.database.mongo.User.class)).thenReturn(null);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(10L));
        verify(mongoTemplate).findAndRemove(findId, pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void deleteMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringDeletingUserById() {
        //given
        Query findId = Query.query(Criteria.where("id").is(10L));
        doThrow(new MockitoException("") {}).when(mongoTemplate).findAndRemove(findId, pl.coderstrust.database.mongo.User.class);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(10L));
        verify(mongoTemplate).findAndRemove(findId, pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void deleteMethodShouldThrowExceptionDuringDeletingNotExistingUser() {
        //given
        Query findId = Query.query(Criteria.where("id").is(10L));
        when(mongoTemplate.findAndRemove(findId, pl.coderstrust.database.mongo.User.class)).thenReturn(null);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(10L));
        verify(mongoTemplate).findAndRemove(findId, pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void deleteMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringDeletingUser() {
        //given
        Query findId = Query.query(Criteria.where("id").is(10L));
        doThrow(new MockitoException("") {}).when(mongoTemplate).findAndRemove(findId, pl.coderstrust.database.mongo.User.class);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.delete(10L));
        verify(mongoTemplate).findAndRemove(findId, pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void shouldDeleteUserByEmail() throws DatabaseOperationException, ServiceOperationException {
        //given
        User user = UserGenerator.getRandomUser();
        pl.coderstrust.database.mongo.User mongoUser = modelMapper.mapToMongoUser(user);
        String email = user.getEmail();
        Query findEmail = Query.query(Criteria.where("email").is(email));
        when(mongoTemplate.findAndRemove(findEmail, pl.coderstrust.database.mongo.User.class)).thenReturn(mongoUser);

        //when
        database.deleteByEmail(email);

        //then
        verify(mongoTemplate).findAndRemove(findEmail, pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> database.deleteByEmail(null));
    }

    @Test
    void deleteMethodShouldThrowExceptionDuringDeletingNotExistingUserByEmail() {
        //given
        Query findEmail = Query.query(Criteria.where("email").is("sample@mail.com"));
        when(mongoTemplate.findAndRemove(findEmail, pl.coderstrust.database.mongo.User.class)).thenReturn(null);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.deleteByEmail("sample@mail.com"));
        verify(mongoTemplate).findAndRemove(findEmail, pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void deleteMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringDeletingUserByEmail() {
        //given
        Query findEmail = Query.query(Criteria.where("email").is("sample@mail.com"));
        doThrow(new MockitoException("") {}).when(mongoTemplate).findAndRemove(findEmail, pl.coderstrust.database.mongo.User.class);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.deleteByEmail("sample@mail.com"));
        verify(mongoTemplate).findAndRemove(findEmail, pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void shouldReturnUserById() throws DatabaseOperationException {
        //given
        User user = UserGenerator.getRandomUser();
        pl.coderstrust.database.mongo.User mongoUser = modelMapper.mapToMongoUser(user);
        Query findId = Query.query(Criteria.where("id").is(user.getId()));
        doReturn(mongoUser).when(mongoTemplate).findOne(findId, pl.coderstrust.database.mongo.User.class);

        //when
        Optional<User> gotUser = database.getById(user.getId());

        //then
        assertTrue(gotUser.isPresent());
        assertEquals(user, gotUser.get());
        verify(mongoTemplate).findOne(findId, pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void shouldReturnEmptyOptionalWhileGettingNonExistingUserById() throws DatabaseOperationException {
        //given
        Query findId = Query.query(Criteria.where("id").is(10L));
        when(mongoTemplate.findOne(findId, pl.coderstrust.database.mongo.User.class)).thenReturn(null);

        //when
        Optional<User> gotUser = database.getById(10L);

        //then
        assertTrue(gotUser.isEmpty());
        verify(mongoTemplate).findOne(findId, pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void getByIdMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringGettingUserById() {
        //given
        Query findId = Query.query(Criteria.where("id").is(10L));
        doThrow(new MockitoException("") {}).when(mongoTemplate).findOne(findId, pl.coderstrust.database.mongo.User.class);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getById(10L));
        verify(mongoTemplate).findOne(findId, pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));
    }

    @Test
    void shouldReturnUserByEmail() throws DatabaseOperationException, ServiceOperationException {
        //given
        User user = UserGenerator.getRandomUser();
        Query findByEmail = Query.query(Criteria.where("email").is(user.getEmail()));
        pl.coderstrust.database.mongo.User mongoUser = modelMapper.mapToMongoUser(user);
        when(mongoTemplate.findOne(findByEmail, pl.coderstrust.database.mongo.User.class)).thenReturn(mongoUser);

        //when
        Optional<User> gotUser = database.getByEmail(user.getEmail());

        //then
        assertTrue(gotUser.isPresent());
        assertEquals(user, gotUser.get());
        verify(mongoTemplate).findOne(findByEmail, pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void shouldReturnEmptyOptionalWhileGettingNonExistingUserByEmail() throws DatabaseOperationException, ServiceOperationException {
        //given
        String email = "sample@mail.com";
        Query findByEmail = Query.query(Criteria.where("email").is(email));
        when(mongoTemplate.findOne(findByEmail, pl.coderstrust.database.mongo.User.class)).thenReturn(null);

        //when
        Optional<User> gotUser = database.getByEmail(email);

        //then
        assertTrue(gotUser.isEmpty());
        verify(mongoTemplate).findOne(findByEmail, pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void getByEmailShouldThrowExceptionForNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> database.getByEmail(null));
    }

    @Test
    void getByEmailMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringGettingUserByEmail() {
        //given
        Query findByEmail = Query.query(Criteria.where("email").is("sample@mail.com"));
        doThrow(new MockitoException("") {}).when(mongoTemplate).findOne(findByEmail, pl.coderstrust.database.mongo.User.class);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getByEmail("sample@mail.com"));
        verify(mongoTemplate).findOne(findByEmail, pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void shouldReturnAllUsers() throws DatabaseOperationException {
        //given
        User user1 = UserGenerator.getRandomUser();
        User user2 = UserGenerator.getRandomUser();
        Collection<User> userList = List.of(user1, user2);
        Collection<pl.coderstrust.database.mongo.User> mongoUserList = modelMapper.mapToMongoUsers(userList);
        doReturn(mongoUserList).when(mongoTemplate).findAll(pl.coderstrust.database.mongo.User.class);

        //when
        Collection<User> gotList = database.getAll();

        //then
        assertEquals(gotList, userList);
        verify(mongoTemplate).findAll(pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void getAllMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringGettingAllUsers() {
        //given
        doThrow(new MockitoException("") {}).when(mongoTemplate).findAll(pl.coderstrust.database.mongo.User.class);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.getAll());
        verify(mongoTemplate).findAll(pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void shouldDeleteAllUsers() throws DatabaseOperationException {
        //given
        doNothing().when(mongoTemplate).dropCollection(pl.coderstrust.database.mongo.User.class);

        //when
        database.deleteAll();

        //then
        verify(mongoTemplate).dropCollection(pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void deleteAllMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringDeletingAllUsers() {
        //given
        doThrow(new MockitoException("") {}).when(mongoTemplate).dropCollection(pl.coderstrust.database.mongo.User.class);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.deleteAll());
        verify(mongoTemplate).dropCollection(pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void shouldReturnTrueForExistingUser() throws DatabaseOperationException {
        //given
        Query findId = Query.query(Criteria.where("id").is(10L));
        when(mongoTemplate.exists(findId, pl.coderstrust.database.mongo.User.class)).thenReturn(true);

        //then
        assertTrue(database.userExistsById(10L));
        verify(mongoTemplate).exists(findId, pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void shouldReturnFalseForNotExistingUser() throws DatabaseOperationException {
        //given
        Query findId = Query.query(Criteria.where("id").is(10L));
        when(mongoTemplate.exists(findId, pl.coderstrust.database.mongo.User.class)).thenReturn(false);

        //then
        assertFalse(database.userExistsById(10L));
        verify(mongoTemplate).exists(findId, pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.userExistsById(null));
    }

    @Test
    void existsMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringCheckingIfUserExists() {
        //given
        Query findId = Query.query(Criteria.where("id").is(10L));
        doThrow(new MockitoException("") {}).when(mongoTemplate).exists(findId, pl.coderstrust.database.mongo.User.class);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.userExistsById(10L));
        verify(mongoTemplate).exists(findId, pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void shouldReturnNumberOfUsers() throws DatabaseOperationException {
        //given
        when(mongoTemplate.count(new Query(), pl.coderstrust.database.mongo.User.class)).thenReturn(10L);

        //when
        long numberOfUsers = database.count();

        //then
        assertEquals(10L, numberOfUsers);
        verify(mongoTemplate).count(new Query(), pl.coderstrust.database.mongo.User.class);
    }

    @Test
    void countMethodShouldThrowDatabaseOperationExceptionWhenErrorOccurDuringGettingNumberOfUsers() {
        //given
        doThrow(new MockitoException("") {}).when(mongoTemplate).count(new Query(), pl.coderstrust.database.mongo.User.class);

        //then
        assertThrows(DatabaseOperationException.class, () -> database.count());
        verify(mongoTemplate).count(new Query(), pl.coderstrust.database.mongo.User.class);
    }
}
