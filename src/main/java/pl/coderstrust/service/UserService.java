package pl.coderstrust.service;

import java.util.Collection;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.database.UserDatabase;
import pl.coderstrust.model.User;

@Service
@ConditionalOnExpression("'${pl.coderstrust.database}' == 'hibernate' || '${pl.coderstrust.database}' == 'mongo'")
public class UserService {

    private final UserDatabase database;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserDatabase database, BCryptPasswordEncoder passwordEncoder) {
        this.database = database;
        this.passwordEncoder = passwordEncoder;
    }

    public User add(User user) throws ServiceOperationException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        try {
            if (user.getId() != null && database.userExistsById(user.getId())) {
                throw new ServiceOperationException("User already exist in database");
            }
            return database.save(buildUserWithEncodedPassword(user));
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred while saving User", e);
        }
    }

    private User buildUserWithEncodedPassword(User user) {
        return User.builder()
            .withId(user.getId())
            .withName(user.getName())
            .withLastName(user.getLastName())
            .withActive(user.getActive())
            .withRoles(user.getRoles())
            .withPassword(passwordEncoder.encode(user.getPassword()))
            .withEmail(user.getEmail())
            .build();
    }

    public User update(User user) throws ServiceOperationException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        try {
            if (user.getId() == null || ! database.userExistsById(user.getId())) {
                throw new ServiceOperationException("User does not exist in database");
            }
            return database.save(user);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during updating User", e);
        }
    }

    public boolean userExistsByEmail(String email) throws ServiceOperationException {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        try {
            return database.userExistsByEmail(email);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during checking if User exist", e);
        }
    }

    public boolean userExistsById(Long id) throws DatabaseOperationException, ServiceOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        try {
            return database.userExistsById(id);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during checking if User exist", e);
        }
    }

    public Optional<User> getById(Long id) throws ServiceOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        try {
            return database.getById(id);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during getting user by id", e);
        }
    }

    public Optional<User> getByEmail(String email) throws ServiceOperationException {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        try {
            return database.getByEmail(email);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during getting user by Email", e);
        }
    }

    public Collection<User> getAll() throws DatabaseOperationException, ServiceOperationException {
        try {
            return database.getAll();
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during getting user by Email", e);
        }
    }

    public void deleteById(Long id) throws ServiceOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        try {
            if (! database.userExistsById(id)) {
                throw new ServiceOperationException("User does not exist in database");
            }
            database.delete(id);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during deleting user from database", e);
        }
    }

    public void deleteByEmail(String email) throws DatabaseOperationException, ServiceOperationException {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        try {
            if (! database.userExistsByEmail(email)) {
                throw new ServiceOperationException("User does not exist in database");
            }
            database.deleteByEmail(email);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during deleting user from database by email", e);
        }
    }

    public void deleteAll() throws ServiceOperationException {
        try {
            database.deleteAll();
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during deleting all users", e);
        }
    }

    public long count() throws DatabaseOperationException, ServiceOperationException {
        try {
            return database.count();
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("An error occurred during counting all users", e);
        }
    }
}
