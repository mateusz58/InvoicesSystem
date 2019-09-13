package pl.coderstrust.database.mongo;

import com.mongodb.MongoClient;
import java.util.Arrays;
import java.util.Collection;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "mongo";
    }

    @Override
    public MongoClient mongoClient() {
        return new MongoClient("localhost");
    }

    @Override
    protected Collection<String> getMappingBasePackages() {
        return Arrays.asList("com.invoices-andrzej-jakub-mateusz");
    }
}
