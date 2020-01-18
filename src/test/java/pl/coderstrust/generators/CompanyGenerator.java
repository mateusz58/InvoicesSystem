package pl.coderstrust.generators;

import com.github.javafaker.Faker;
import java.util.Locale;
import pl.coderstrust.model.Company;

public class CompanyGenerator {

    private static Faker faker = new Faker(new Locale("pl"));

    public static Company generateRandomCompany() {
        return Company.builder()
            .id(IdGenerator.getRandomId())
            .email(faker.internet().emailAddress())
            .address(AddressGenerator.generateRandomAddress())
            .accountNumber(String.valueOf(NumberGenerator.generateRandomNumber(11)))
            .name(faker.company().name())
            .phoneNumber(faker.phoneNumber().phoneNumber())
            .taxId(faker.regexify("[0-9]{3}-[0-9]{3}-[0-9]{2}-[0-9]{2}"))
            .build();
    }

    public static Company generateRandomCompanyWithSpecificId(Long id) {
        return Company.builder()
            .id(id)
            .email(faker.internet().emailAddress())
            .address(AddressGenerator.generateRandomAddress())
            .accountNumber(String.valueOf(NumberGenerator.generateRandomNumber(11)))
            .name(faker.company().name())
            .phoneNumber(faker.phoneNumber().phoneNumber())
            .taxId(faker.regexify("[0-9]{3}-[0-9]{3}-[0-9]{2}-[0-9]{2}"))
            .build();
    }
}
