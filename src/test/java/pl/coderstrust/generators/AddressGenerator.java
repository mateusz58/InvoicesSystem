package pl.coderstrust.generators;

import com.github.javafaker.Faker;
import java.util.Locale;

public class AddressGenerator {

    private static Faker faker = new Faker(new Locale("pl"));

    public static String generateRandomAddress() {
        String streetAddress = faker.address().streetAddress();
        String postCode = faker.address().zipCode();
        String city = faker.address().city();
        return String.format("%s\n%s %s",
            streetAddress,
            postCode,
            city);
    }
}
