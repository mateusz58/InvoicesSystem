package pl.coderstrust.generators;

import com.github.javafaker.Faker;

public class NumberGenerator {

    private static Faker random = new Faker();

    public static long generateRandomNumber(int length) {
        return Long.valueOf(random.number().digits(length));
    }
}
