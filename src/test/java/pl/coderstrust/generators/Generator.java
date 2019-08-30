package pl.coderstrust.generators;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Generator {

    private static Random random = new Random();

    public static String generateRandomWord() {
        char[] word = new char[random.nextInt(8) + 3];
        for (int i = 0; i < word.length; i++) {
            word[i] = (char) ('a' + random.nextInt(26));
        }
        return new String(word);
    }

    @Test
    public static String generateRandomEmail() {

        return generateRandomWord() + "@mail.com";
    }


    public static String generateRandomPhoneNumber() {

        char[] word = new char[9];
        for (int i = 0; i < 9; i++) {
            word[i] = ((char) random.nextInt(9));
        }
        return new String(word);
    }

    public static String generateRandomAccountNumber() {

        char[] word = new char[11];
        for (int i = 0; i < 11; i++) {
            word[i] = ((char) random.nextInt(9));
        }
        return new String(word);
    }

    public static LocalDate generateRandomLocalDate() {
        int year = ThreadLocalRandom.current().nextInt(1976, 2019);
        int month = ThreadLocalRandom.current().nextInt(1, 12);
        int day = ThreadLocalRandom.current().nextInt(1, 28);

        return LocalDate.of(year, month, day);
    }

    public static Long generateRandomNumber() {
        return new Random().nextLong() + 1;
    }

    public static BigDecimal generateRandomPrice() {
        return BigDecimal.valueOf(new Random().nextLong() + 1);
    }

}
