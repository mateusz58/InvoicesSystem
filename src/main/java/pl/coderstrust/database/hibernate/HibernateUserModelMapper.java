package pl.coderstrust.database.hibernate;

import java.util.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HibernateUserModelMapper {

    Collection<pl.coderstrust.model.User> mapToUsers(Collection<pl.coderstrust.database.hibernate.User> users);

    Collection<pl.coderstrust.database.hibernate.User> mapToHibernateUsers(Collection<pl.coderstrust.model.User> users);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withEmail", source = "email")
    @Mapping(target = "withPassword", source = "password")
    @Mapping(target = "withName", source = "name")
    @Mapping(target = "withLastName", source = "lastName")
    @Mapping(target = "withActive", source = "active")
    @Mapping(target = "withRoles", source = "roles")
    pl.coderstrust.database.hibernate.User mapToHibernateUser(pl.coderstrust.model.User user);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withEmail", source = "email")
    @Mapping(target = "withPassword", source = "password")
    @Mapping(target = "withName", source = "name")
    @Mapping(target = "withLastName", source = "lastName")
    @Mapping(target = "withActive", source = "active")
    @Mapping(target = "withRoles", source = "roles")
    pl.coderstrust.model.User mapToUser(pl.coderstrust.database.hibernate.User user);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withRoleName", source = "roleName")
    pl.coderstrust.database.hibernate.Role mapToHibernateRole(pl.coderstrust.model.Role role);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withRoleName", source = "roleName")
    pl.coderstrust.model.Role mapToRole(pl.coderstrust.database.hibernate.Role role);
}
