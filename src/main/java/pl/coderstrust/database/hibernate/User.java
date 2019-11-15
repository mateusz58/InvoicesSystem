package pl.coderstrust.database.hibernate;

import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private final Long id;
    @Column(name = "email")
    @Email(message = "*Please provide a valid Email")
    @NotEmpty(message = "*Please provide an email")
    private final String email;
    @Column(name = "password")
    @Length(min = 5, message = "*Your password must have at least 5 characters")
    @NotEmpty(message = "*Please provide your password")
    private final String password;
    @Column(name = "name")
    @NotEmpty(message = "*Please provide your name")
    private final String name;
    @Column(name = "last_name")
    @NotEmpty(message = "*Please provide your last name")
    private final String lastName;
    @Column(name = "active")
    private final Integer active;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private final Set<Role> roles;

    private User() {
        id = null;
        email = null;
        password = null;
        name = null;
        lastName = null;
        active = null;
        roles = null;
    }

    private User(User.Builder builder) {
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

    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return "User{"
            + "id=" + id
            + ", email='" + email + '\''
            + ", password='" + password + '\''
            + ", name='" + name + '\''
            + ", lastName='" + lastName + '\''
            + ", active=" + active
            + ", roles=" + roles
            + "}";
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
        return Objects.equals(id, user.id)
            && Objects.equals(email, user.email)
            && Objects.equals(password, user.password)
            && Objects.equals(name, user.name)
            && Objects.equals(lastName, user.lastName)
            && Objects.equals(active, user.active)
            && Objects.equals(roles, user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, name, lastName, active, roles);
    }

    public static class Builder {

        private Long id;
        private String email;
        private String password;
        private String name;
        private String lastName;
        private Integer active;
        private Set<Role> roles;

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
