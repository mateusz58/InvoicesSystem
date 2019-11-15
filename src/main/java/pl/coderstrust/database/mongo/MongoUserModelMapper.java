package pl.coderstrust.database.mongo;

import java.util.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface MongoUserModelMapper {

    Collection<pl.coderstrust.database.mongo.User> mapToMongoUsers(Collection<pl.coderstrust.model.User> users);

    Collection<pl.coderstrust.model.User> mapToUsers(Collection<pl.coderstrust.database.mongo.User> users);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withEmail", source = "email")
    @Mapping(target = "withPassword", source = "password")
    @Mapping(target = "withName", source = "name")
    @Mapping(target = "withLastName", source = "lastName")
    @Mapping(target = "withActive", source = "active")
    @Mapping(target = "withRoles", source = "roles")
    pl.coderstrust.database.mongo.User mapToMongoUser(pl.coderstrust.model.User user);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withEmail", source = "email")
    @Mapping(target = "withPassword", source = "password")
    @Mapping(target = "withName", source = "name")
    @Mapping(target = "withLastName", source = "lastName")
    @Mapping(target = "withActive", source = "active")
    @Mapping(target = "withRoles", source = "roles")
    pl.coderstrust.model.User mapToUser(pl.coderstrust.database.mongo.User user);

    @Mapping(target = "withRoleName", source = "roleName")
    pl.coderstrust.database.mongo.Role mapToMongoRole(pl.coderstrust.model.Role role);

    @Mapping(target = "withRoleName", source = "roleName")
    pl.coderstrust.model.Role mapToRole(pl.coderstrust.database.mongo.Role role);
}
