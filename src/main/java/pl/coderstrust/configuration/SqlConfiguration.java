package pl.coderstrust.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:postgresql.properties")
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "postgresql")
public class SqlConfiguration {
}
