package pl.coderstrust.generators;

import pl.coderstrust.model.Company;

public class CompanyGenerator {

    public static Company generateRandomCompany() {
        return Company.builder()
            .withId(IdGenerator.getRandomId())
            .withEmail(WordGenerator.generateRandomWord() + "@mail.com")
            .withAddress(WordGenerator.generateRandomWord())
            .withAccountNumber(String.valueOf(NumberGenerator.generateRandomNumber(11)))
            .withName(WordGenerator.generateRandomWord())
            .withPhoneNumber(String.valueOf(NumberGenerator.generateRandomNumber(9)))
            .withTaxId(WordGenerator.generateRandomWord())
            .build();
    }
}
