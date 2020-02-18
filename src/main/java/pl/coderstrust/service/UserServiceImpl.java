package pl.coderstrust.service;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.coderstrust.model.User;

@Component
@PropertySource("classpath:keycloak.properties")
public class UserServiceImpl{

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    public UserServiceImpl() {
    }

    public List<User> getByUsername(String username, KeycloakPrincipal<KeycloakSecurityContext> principal) throws ServiceOperationException {
        KeycloakSecurityContext context = principal.getKeycloakSecurityContext();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + context.getTokenString());

        StringBuilder sb = new StringBuilder(keycloakServerUrl);
        sb.append("/admin/realms/").append(keycloakRealm).append("/users");
        sb.append("?username=").append(username);

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        RestTemplate restTemplate = new RestTemplate();
        User[] users = restTemplate
            .exchange(URI.create(sb.toString()), HttpMethod.GET, entity, User[].class).getBody();
        if (users.length == 0) {
            throw new ServiceOperationException("User does not exist");
        }

        return Arrays.asList(Arrays.stream(users).toArray(User[]::new));
    }

    public List<User> getUsers(KeycloakPrincipal<KeycloakSecurityContext> principal) {
        KeycloakSecurityContext context = principal.getKeycloakSecurityContext();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + context.getTokenString());

        StringBuilder sb = new StringBuilder(keycloakServerUrl);
        sb.append("/admin/realms/").append(keycloakRealm).append("/users");

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        RestTemplate restTemplate = new RestTemplate();
        List<User> users = restTemplate.exchange(URI.create(sb.toString()), HttpMethod.GET, entity,
            new ParameterizedTypeReference<List<User>>() {
            }).getBody();

        return users;
    }

}