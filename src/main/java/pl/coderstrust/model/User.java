package pl.coderstrust.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@JsonDeserialize(builder = User.UserBuilder.class)
@ApiModel(value = "User", description = "user")
@Data
@Builder(builderClassName = "UserBuilder", toBuilder = true)
public class User implements Serializable{

    private static final long serialVersionUID = 1L;

    private String id;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    @JsonPOJOBuilder(withPrefix = "")
    public static class UserBuilder {
    }

}
