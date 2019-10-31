package pl.coderstrust.generators;

import com.github.javafaker.Faker;
import java.util.Locale;
import pl.coderstrust.model.Company;

public class CompanyGenerator {

    private static Faker faker = new Faker(new Locale("pl"));

    public static Company generateRandomCompany() {
        return Company.builder()
            .withId(IdGenerator.getRandomId())
            .withEmail(faker.internet().emailAddress())
            .withAddress(AddressGenerator.generateRandomAddress())
            .withAccountNumber(String.valueOf(NumberGenerator.generateRandomNumber(11)))
            .withName(faker.company().name())
            .withPhoneNumber(faker.phoneNumber().phoneNumber())
            .withTaxId(faker.regexify("[0-9]{3}-[0-9]{3}-[0-9]{2}-[0-9]{2}"))
            .build();
    }
}
