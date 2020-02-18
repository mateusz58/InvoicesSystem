package pl.coderstrust.configuration;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:keycloak.properties")
class KeycloakConfig{

    @Bean
    public KeycloakSpringBootConfigResolver KeycloakConfigResolver() {

        return new KeycloakSpringBootConfigResolver();
    }
}
