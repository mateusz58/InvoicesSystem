package pl.coderstrust.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import java.util.Set;

@JsonDeserialize(builder = User.Builder.class)
@ApiModel(value = "User", description = "Users")
public class User {

    @ApiModelProperty(value = "The unique identifier of the user", position = -1, dataType = "Long")
    private final Long id;
    @ApiModelProperty(value = "User email", example = "sample@mail.com")
    private final String email;
    @ApiModelProperty(value = "User password", example = "admin")
    private final String password;
    @ApiModelProperty(value = "User first name", example = "John")
    private final String name;
    @ApiModelProperty(value = "User last name", example = "Doe")
    private final String lastName;
    @ApiModelProperty(value = "Information whether user is active", example = "1", dataType = "Integer")
    private final Integer active;
    @ApiModelProperty(value = "Assigned roles")
    private final Set<Role> roles;

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
            + '}';
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
            && Objects.equals(email,user.email)
            && Objects.equals(password,user.password)
            && Objects.equals(name, user.name)
            && Objects.equals(lastName, user.lastName)
            && Objects.equals(active, user.active)
            && roles.stream().allMatch(s -> s.getRoleName().equals(user.getRoles().iterator().next().getRoleName()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, name, lastName, active, roles);
    }

    @JsonPOJOBuilder
    public static class Builder {

        private Long id;
        private String email;
        private String password;
        private String name;
        private String lastName;
        private Integer active;
        private Set<Role> roles;

        public User.Builder withUser(User user) {
            this.id = user.id;
            this.name = user.name;
            this.lastName = user.lastName;
            this.active = user.active;
            this.roles = user.roles;
            this.password = user.password;
            this.email = user.email;
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
