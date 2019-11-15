package pl.coderstrust.database.mongo;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

public class User {

    @Id
    private final String mongoId;

    @Indexed(unique = true)
    private final Long id;
    private final String email;
    private final String password;
    private final String name;
    private final String lastName;
    private final Integer active;
    private final Set<Role> roles;


    @PersistenceConstructor
    private User(String mongoId, Long id, String email, String password, String name, String lastName, Integer active, Set<Role> roles) {
        this.mongoId = mongoId;
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
        this.active = active;
        this.roles = roles;
    }

    private User(User.Builder builder) {
        mongoId = builder.mongoId;
        id = builder.id;
        email = builder.email;
        password = builder.password;
        name = builder.name;
        lastName = builder.lastName;
        active = builder.active;
        roles = builder.roles;
    }

    public static User.Builder builder() {
        return new User.Builder();
    }

    public String getMongoId() {
        return mongoId;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getActive() {
        return active;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mongoId, id, email, password, name, lastName, active, roles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(mongoId, user.mongoId)
            && Objects.equals(id, user.id)
            && Objects.equals(email, user.email)
            && Objects.equals(password, user.password)
            && Objects.equals(name, user.name)
            && Objects.equals(lastName, user.lastName)
            && Objects.equals(active, user.active)
            && roles.stream().allMatch(s -> s.getRoleName().equals(user.getRoles().iterator().next().getRoleName()));
    }

    public Set<Role> getRoles() {
        return roles != null ? new HashSet<>(roles) : new HashSet<>();
    }

    @Override
    public String toString() {
        return "User{"
            + "mongoId='" + mongoId + '\''
            + ", id=" + id
            + ", email='" + email + '\''
            + ", password='" + password + '\''
            + ", name='" + name + '\''
            + ", lastName='" + lastName + '\''
            + ", active=" + active
            + ", roles=" + roles
            + '}';
    }

    public static class Builder {

        private String mongoId;
        private Long id;
        private String email;
        private String password;
        private String name;
        private String lastName;
        private Integer active;
        private Set<Role> roles;

        public User.Builder withMongoId(String mongoId) {
            this.mongoId = mongoId;
            return this;
        }

        public User.Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public User.Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public User.Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public User.Builder withName(String name) {
            this.name = name;
            return this;
        }

        public User.Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public User.Builder withActive(Integer active) {
            this.active = active;
            return this;
        }

        public User.Builder withRoles(Set<Role> roles) {
            this.roles = roles;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
