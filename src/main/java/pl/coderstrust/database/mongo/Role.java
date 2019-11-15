package pl.coderstrust.database.mongo;

import java.util.Objects;
import org.springframework.data.annotation.PersistenceConstructor;

public class Role {

    private final String roleName;

    @PersistenceConstructor
    public Role(String roleName) {
        this.roleName = roleName;
    }

    private Role(Builder builder) {
        roleName = builder.roleName;
    }

    public static Role.Builder builder() {
        return new Role.Builder();
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Role role = (Role) o;
        return this.roleName.contains(role.getRoleName());
    }

    public String getRoleName() {
        return roleName;
    }

    @Override
    public String toString() {
        return "Role{"
            + "roleName='" + roleName + '\''
            + '}';
    }

    public static class Builder {

        private String roleName;

        public Role.Builder withId(Long id) {
            return this;
        }

        public Role.Builder withRoleName(String roleName) {
            this.roleName = roleName;
            return this;
        }

        public Role build() {
            return new Role(this);
        }
    }
}
