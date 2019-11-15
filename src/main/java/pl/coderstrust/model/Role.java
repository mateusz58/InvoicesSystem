package pl.coderstrust.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

@JsonDeserialize(builder = Role.Builder.class)
@ApiModel(value = "Role", description = "Roles that user is assigned to")
public class Role {

    @ApiModelProperty(value = "The unique identifier of the Role", position = -1, dataType = "Long")
    private final Long id;
    @ApiModelProperty(value = "Role name that user is assigned to", example = "ADMIN")
    private final String roleName;

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
        if (this.roleName.equals(role.getRoleName())) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roleName);
    }

    @Override
    public String toString() {
        return "Role{"
            + "id=" + id
            + ", roleName='" + roleName + '\''
            + '}';
    }

    @JsonPOJOBuilder
    public static class Builder {

        private String roleName;
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
