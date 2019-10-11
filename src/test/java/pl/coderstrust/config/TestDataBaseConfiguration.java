package pl.coderstrust.config;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
@EnableAutoConfiguration(exclude = {
    HibernateJpaAutoConfiguration.class, MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class TestDataBaseConfiguration {
    @Bean
    public PostgreSQLContainer postgresqlContainer() {
        PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.5");
        postgreSQLContainer.start();
        return postgreSQLContainer;
    }

    @Bean
    public DataSource postgresqlContainerDataSource(final PostgreSQLContainer postgreSQLContainer) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(postgreSQLContainer.getJdbcUrl());
        dataSource.setUsername(postgreSQLContainer.getUsername());
        dataSource.setPassword(postgreSQLContainer.getPassword());
        dataSource.setDriverClassName(postgreSQLContainer.getDriverClassName());
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        return jdbcTemplate;
    }
}
