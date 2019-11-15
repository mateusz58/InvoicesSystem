package pl.coderstrust.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.coderstrust.model.User;
import pl.coderstrust.service.UserService;

@RestController
@RequestMapping("/users/")
@Api(value = "/users/")
@ConditionalOnExpression("'${pl.coderstrust.database}' == 'hibernate' || '${pl.coderstrust.database}' == 'mongo'")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Add new user", notes = "Add new user to database", response = User.class)
    @ApiResponses({
        @ApiResponse(code = 201, message = "Created", response = User.class),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 406, message = "Not acceptable format"),
        @ApiResponse(code = 409, message = "User with given email exist im database"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @ApiImplicitParam(required = true, name = "user", value = "New user data", dataType = "User")
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> add(@RequestBody(required = false) User user) {
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            if (user.getId() != null && userService.userExistsByEmail(user.getEmail())) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            User addedUser = userService.add(user);
            return new ResponseEntity<>(addedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Update user", notes = "Update user with provided id", response = User.class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "Updated", response = User.class),
        @ApiResponse(code = 404, message = "User not found"),
        @ApiResponse(code = 406, message = "Not acceptable format"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @ApiImplicitParams({
        @ApiImplicitParam(required = true, name = "id", value = "Id of user to update", dataType = "Long"),
        @ApiImplicitParam(required = true, name = "email", value = "email of user to update", dataType = "String"),
        @ApiImplicitParam(required = true, name = "password", value = "password of user to update", dataType = "String"),
        @ApiImplicitParam(required = true, name = "user", value = "User with updated data", dataType = "User")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody(required = false) User user) {
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            if (! id.equals(user.getId())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (! userService.userExistsById(id)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(userService.update(user), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Get all users", notes = "Retrieving the collection of all users in database", response = User[].class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "OK", response = User[].class),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        try {
            return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Find by email", notes = "Finds user by given email", response = User.class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "OK", response = User.class),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 404, message = "User not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @ApiImplicitParam(required = true, name = "email", value = "Email of the user to get", dataType = "String")
    @GetMapping(value = "/byEmail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getByEmail(@RequestParam(required = false) String email) {
        if (email == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Optional<User> user = userService.getByEmail(email);
            if (user.isPresent()) {
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Find by Id", notes = "Finds User by given Id", response = User.class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "OK", response = User.class),
        @ApiResponse(code = 404, message = "User not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @ApiImplicitParam(required = true, name = "id", value = "Id of the user to get", dataType = "Long")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(@PathVariable("id") long id) {
        try {
            Optional<User> user = userService.getById(id);
            if (user.isPresent()) {
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Delete by Id", notes = "Deletes User with specific Id")
    @ApiResponses({
        @ApiResponse(code = 204, message = "Removed"),
        @ApiResponse(code = 404, message = "User not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        try {
            if (userService.userExistsById(id)) {
                userService.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Delete all users", notes = "Erases all users in database")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Deleted all", response = User.class),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping
    public ResponseEntity<?> deleteAll() {
        try {
            userService.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
