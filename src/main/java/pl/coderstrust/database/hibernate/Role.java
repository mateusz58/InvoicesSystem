package pl.coderstrust.database.hibernate;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Long id;

    private final String roleName;

    private Role() {
        id = null;
        roleName = null;
    }

    private Role(Role.Builder builder) {
        id = builder.id;
        roleName = builder.roleName;
    }

    public static Role.Builder builder() {
        return new Role.Builder();
    }

    public Long getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Role role = (Role) o;
        return Objects.equals(id, role.id)
            && Objects.equals(roleName, role.roleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roleName);
    }

    public static class Builder {

        public String roleName;
        private Long id;

        public Role.Builder withId(Long id) {
            this.id = id;
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
