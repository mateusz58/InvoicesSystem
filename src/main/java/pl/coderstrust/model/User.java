package pl.coderstrust.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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

    public User() {
    }

}
