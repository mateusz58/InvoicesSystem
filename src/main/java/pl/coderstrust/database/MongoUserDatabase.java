package pl.coderstrust.database;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.coderstrust.database.mongo.MongoUserModelMapper;
import pl.coderstrust.model.Role;
import pl.coderstrust.model.User;
import pl.coderstrust.service.ServiceOperationException;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "mongo")
public class MongoUserDatabase implements UserDatabase {
    private final MongoTemplate mongoTemplate;
    private final MongoUserModelMapper modelMapper;
    private AtomicLong lastId;

    public MongoUserDatabase(MongoTemplate mongoTemplate, MongoUserModelMapper modelMapper) {
        if (mongoTemplate == null) {
            throw new IllegalArgumentException("Mongo template cannot be null.");
        }
        if (modelMapper == null) {
            throw new IllegalArgumentException("Mapper cannot be null.");
        }
        this.mongoTemplate = mongoTemplate;
        this.modelMapper = modelMapper;
        init();
    }

    private void init() {
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "id"));
        query.limit(1);
        pl.coderstrust.database.mongo.User user = mongoTemplate.findOne(query, pl.coderstrust.database.mongo.User.class);
        if (user == null) {
            lastId = new AtomicLong(0);
            return;
        }
        lastId = new AtomicLong(user.getId());
    }

    @Override
    public boolean userExistsByEmail(String email) throws DatabaseOperationException, ServiceOperationException {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null.");
        }
        try {
            return mongoTemplate.exists(Query.query(Criteria.where("email").is(email)), pl.coderstrust.database.mongo.User.class);
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during checking if user exists.", e);
        }
    }

    @Override
    public boolean userExistsById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return mongoTemplate.exists(Query.query(Criteria.where("id").is(id)), pl.coderstrust.database.mongo.User.class);
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during checking if user exists.", e);
        }
    }

    @Override
    public Optional<User> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            pl.coderstrust.database.mongo.User user = getUserById(id);
            if (user != null) {
                return Optional.of(modelMapper.mapToUser(user));
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during getting user by id.", e);
        }
    }

    @Override
    public Optional<User> getByEmail(String email) throws DatabaseOperationException, ServiceOperationException {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null.");
        }
        try {
            pl.coderstrust.database.mongo.User user = mongoTemplate.findOne(Query.query(Criteria.where("email").is(email)), pl.coderstrust.database.mongo.User.class);
            if (user != null) {
                return Optional.of(modelMapper.mapToUser(user));
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during getting user by number.", e);
        }
    }

    @Override
    public Collection<User> getAll() throws DatabaseOperationException {
        try {
            return modelMapper.mapToUsers(mongoTemplate.findAll(pl.coderstrust.database.mongo.User.class));
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during getting all users.", e);
        }
    }

    @Override
    public User save(User user) throws DatabaseOperationException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        try {
            pl.coderstrust.database.mongo.User userInDatabase = getUserById(user.getId());
            if (userInDatabase == null) {
                return insertUser(user);
            }
            return updateUser(user, userInDatabase.getMongoId());
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during saving user.", e);
        }
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            pl.coderstrust.database.mongo.User user = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), pl.coderstrust.database.mongo.User.class);
            if (user == null) {
                throw new DatabaseOperationException(String.format("There is no user with id: %s", id));
            }
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during deleting user.", e);
        }
    }

    @Override
    public void deleteByEmail(String email) throws DatabaseOperationException, ServiceOperationException {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null.");
        }
        try {
            pl.coderstrust.database.mongo.User user = mongoTemplate.findAndRemove(Query.query(Criteria.where("email").is(email)), pl.coderstrust.database.mongo.User.class);
            if (user == null) {
                throw new DatabaseOperationException(String.format("There is no user with email: %s", email));
            }
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during deleting user by email.", e);
        }
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {
        try {
            mongoTemplate.dropCollection(pl.coderstrust.database.mongo.User.class);
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during deleting all users.", e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return mongoTemplate.count(new Query(), pl.coderstrust.database.mongo.User.class);
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during getting number of users.", e);
        }
    }

    private User insertUser(User user) {
        User userToBeInserted = User.builder()
            .withUser(user)
            .withId(lastId.incrementAndGet())
            .build();
        return modelMapper.mapToUser(mongoTemplate.insert(modelMapper.mapToMongoUser(userToBeInserted)));
    }

    private User updateUser(User user, String mongoId) {
        pl.coderstrust.database.mongo.User updatedUser = pl.coderstrust.database.mongo.User.builder()
            .withMongoId(mongoId)
            .withId(user.getId())
            .withName(user.getName())
            .withActive(user.getActive())
            .withEmail(user.getEmail())
            .withPassword(user.getPassword())
            .withLastName(user.getLastName())
            .withRoles(user.getRoles().stream().map(s -> convertToMongoRoles(s)).collect(Collectors.toSet()))
            .build();
        return modelMapper.mapToUser(mongoTemplate.save(updatedUser));
    }

    private pl.coderstrust.database.mongo.Role convertToMongoRoles(Role role) {
        return pl.coderstrust.database.mongo.Role.builder()
            .withId(role.getId())
            .withRoleName(role.getRoleName())
            .build();
    }

    private pl.coderstrust.database.mongo.User getUserById(Long id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), pl.coderstrust.database.mongo.User.class);
    }
}
