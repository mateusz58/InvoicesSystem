package pl.coderstrust.generators;

import com.github.javafaker.Faker;
import java.util.Locale;
import java.util.Set;
import pl.coderstrust.model.Role;
import pl.coderstrust.model.User;

public class UserGenerator {

    private static Faker faker = new Faker(new Locale("pl"));

    private static User generateRandomUser(Long id) {
        return User.builder()
            .withId(id)
            .withEmail(faker.internet().emailAddress())
            .withName(faker.name().firstName())
            .withLastName(faker.name().lastName())
            .withPassword(faker.superhero().name())
            .withActive(1)
            .withRoles(Set.of(Role.builder().withId(1L).withRoleName("ADMIN").build()))
            .build();
    }

    public static User getRandomUserWithSpecificId(Long id) {
        return generateRandomUser(id);
    }

    public static User getRandomUserWithNullId() {
        return generateRandomUser(null);
    }

    public static User getRandomUser() {
        return generateRandomUser(IdGenerator.getRandomId());
    }
}
