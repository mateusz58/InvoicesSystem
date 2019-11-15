package pl.coderstrust.database;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.stereotype.Repository;
import pl.coderstrust.database.hibernate.HibernateUserModelMapper;
import pl.coderstrust.database.hibernate.UserRepository;
import pl.coderstrust.model.User;
import pl.coderstrust.service.ServiceOperationException;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "hibernate")
public class HibernateUserDatabase implements UserDatabase {
    private final UserRepository userRepository;
    private final HibernateUserModelMapper modelMapper;

    public HibernateUserDatabase(UserRepository userRepository, HibernateUserModelMapper modelMapper) {
        if (userRepository == null) {
            throw new IllegalArgumentException("Database is empty.");
        }
        if (modelMapper == null) {
            throw new IllegalArgumentException("Mapper is empty.");
        }
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean userExistsByEmail(String email) throws DatabaseOperationException {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null.");
        }
        try {
            return userRepository.existsByEmail(email);
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during checking if user exists in database.", e);
        }
    }

    @Override
    public boolean userExistsById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return userRepository.existsById(id);
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during checking if user exists.", e);
        }
    }

    @Override
    public Optional<User> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            Optional<pl.coderstrust.database.hibernate.User> user = userRepository.findById(id);
            if (user.isPresent()) {
                return Optional.of(modelMapper.mapToUser(user.get()));
            }
            return Optional.empty();
        } catch (NoSuchElementException e) {
            throw new DatabaseOperationException("An error occurred during getting user by id.", e);
        }
    }

    @Override
    public Optional<User> getByEmail(String email) throws DatabaseOperationException {
        if (email == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            Optional<pl.coderstrust.database.hibernate.User> user = userRepository.findUserByEmail(email);
            if (user.isPresent()) {
                return Optional.of(modelMapper.mapToUser(user.get()));
            }
            return Optional.empty();
        } catch (NoSuchElementException e) {
            throw new DatabaseOperationException("An error occurred during getting user by id.", e);
        }
    }

    @Override
    public Collection<User> getAll() throws DatabaseOperationException {
        try {
            return modelMapper.mapToUsers(userRepository.findAll());
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during getting all users.", e);
        }
    }

    @Override
    public User save(User user) throws DatabaseOperationException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        try {
            pl.coderstrust.database.hibernate.User userSaved = userRepository.save(modelMapper.mapToHibernateUser(user));
            return modelMapper.mapToUser(userSaved);
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during saving user.", e);
        }
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Email cannot be null.");
        }
        if (! userRepository.existsById(id)) {
            throw new DatabaseOperationException(String.format("There is no user with id: %s", id));
        }
        try {
            userRepository.deleteById(id);
        } catch (NonTransientDataAccessException | NoSuchElementException e) {
            throw new DatabaseOperationException("An error occurred during deleting user.", e);
        }
    }

    @Override
    public void deleteByEmail(String email) throws DatabaseOperationException, ServiceOperationException {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null.");
        }
        if (! userRepository.existsByEmail(email)) {
            throw new DatabaseOperationException(String.format("There is no user with email: %s", email));
        }
        try {
            userRepository.deleteUserByEmail(email);
        } catch (NonTransientDataAccessException | NoSuchElementException e) {
            throw new DatabaseOperationException("An error occurred during deleting user.", e);
        }
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {
        try {
            userRepository.deleteAll();
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during deleting all invoices.", e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return userRepository.count();
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during getting number of invoices.", e);
        }
    }
}
