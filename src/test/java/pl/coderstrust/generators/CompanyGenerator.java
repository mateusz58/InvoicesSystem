package pl.coderstrust.generators;

import pl.coderstrust.model.Company;

public class CompanyGenerator {

    public static Company generateRandomCompany() {
        return Company.builder()
                .withId(IdGenerator.getId())
                .withAccountNumber(Generator.generateRandomWord())
                .withAddress(Generator.generateRandomWord())
                .withAccountNumber(Generator.generateRandomAccountNumber())
                .withName(Generator.generateRandomWord())
                .withPhoneNumber(Generator.generateRandomPhoneNumber())
                .withTaxId(Generator.generateRandomWord())
                .build();
    }

}
