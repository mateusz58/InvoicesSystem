package pl.coderstrust.generators;

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

    public static LocalDate generateRandomLocalDate() {
        int year = ThreadLocalRandom.current().nextInt(1976, 2019);
        int month = ThreadLocalRandom.current().nextInt(1, 12);
        int day = ThreadLocalRandom.current().nextInt(1, 28);

        return LocalDate.of(year, month, day);
    }

    public static int generateRandomNumber(int length) {
        int size = (int) Math.pow(10, length - 1);
        return random.nextInt() + size;
    }

}
