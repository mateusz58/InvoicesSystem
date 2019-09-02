package pl.coderstrust.generators;

import pl.coderstrust.model.Company;

public class CompanyGenerator {

    public static Company generateRandomCompany() {
        return Company.builder()
                .withId(IdGenerator.getId())
                .withEmail(Generator.generateRandomWord() + "@mail.com")
                .withAddress(Generator.generateRandomWord())
                .withAccountNumber(String.valueOf(Generator.generateRandomNumber(11)))
                .withName(Generator.generateRandomWord())
                .withPhoneNumber(String.valueOf(Generator.generateRandomNumber(9)))
                .withTaxId(Generator.generateRandomWord())
                .build();
    }
}
