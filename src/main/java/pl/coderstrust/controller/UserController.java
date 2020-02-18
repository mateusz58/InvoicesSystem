package pl.coderstrust.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Optional;
import org.apache.catalina.UserDatabase;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.coderstrust.model.User;
import pl.coderstrust.service.ServiceOperationException;
import pl.coderstrust.service.UserServiceImpl;

@RestController
@RequestMapping("api/users/")
@Api(value = "api/users/")
public class UserController{

    private Logger log = LoggerFactory.getLogger(UserController.class);

    private UserServiceImpl userDatabase;

    public UserController(UserServiceImpl userDatabase) {
        this.userDatabase = userDatabase;
    }

    @ApiOperation(value = "Find by username", notes = "Finds user by given login", response = User.class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "OK", response = User.class),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 404, message = "User not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @ApiImplicitParam(required = true, name = "number", value = "Number of the user to get", dataType = "String")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getByLogin(@RequestParam(required = false) String username, KeycloakPrincipal<KeycloakSecurityContext> principal) throws ServiceOperationException {
        try {
        if (username == null) {
            log.error("Attempt to get user with null login.");
            return new ResponseEntity<>(userDatabase.getByUsername(username,principal), HttpStatus.OK);
        }
        return new ResponseEntity<>(userDatabase.getUsers(principal), HttpStatus.OK);

        } catch (Exception e) {
            log.error("An error occured during getting user by number.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
