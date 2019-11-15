package pl.coderstrust.database;

import java.util.Collection;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import pl.coderstrust.model.User;
import pl.coderstrust.service.ServiceOperationException;

@ConditionalOnExpression("'${pl.coderstrust.database}' == 'hibernate' || '${pl.coderstrust.database}' == 'mongo'")
public interface UserDatabase {

    boolean userExistsByEmail(String email) throws DatabaseOperationException, ServiceOperationException;

    boolean userExistsById(Long id) throws DatabaseOperationException, ServiceOperationException;

    Optional<User> getById(Long  id) throws DatabaseOperationException, ServiceOperationException;

    Optional<User> getByEmail(String email) throws DatabaseOperationException, ServiceOperationException;

    Collection<User> getAll() throws DatabaseOperationException, ServiceOperationException;

    User save(User user) throws DatabaseOperationException, ServiceOperationException;

    void delete(Long id) throws DatabaseOperationException, ServiceOperationException;

    void deleteByEmail(String email) throws DatabaseOperationException, ServiceOperationException;

    void deleteAll() throws DatabaseOperationException, ServiceOperationException;

    long count() throws DatabaseOperationException, ServiceOperationException;
}
