package pl.coderstrust.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("postgresql")
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "postgresql")
public class SqlConfiguration {
}
