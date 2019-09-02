package pl.coderstrust.generators;

import pl.coderstrust.model.Company;

public class CompanyGenerator {

    public static Company generateRandomCompany() {
        return Company.builder()
            .withId(IdGenerator.getRandomId())
            .withEmail(RandomWordGenerator.generateRandomWord() + "@mail.com")
            .withAddress(RandomWordGenerator.generateRandomWord())
            .withAccountNumber(String.valueOf(RandomNumberGenerator.generateRandomNumber(11)))
            .withName(RandomWordGenerator.generateRandomWord())
            .withPhoneNumber(String.valueOf(RandomNumberGenerator.generateRandomNumber(9)))
            .withTaxId(RandomWordGenerator.generateRandomWord())
            .build();
    }
}
