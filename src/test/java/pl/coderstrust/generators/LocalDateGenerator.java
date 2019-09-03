package pl.coderstrust.generators;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

public class LocalDateGenerator {

    public static LocalDate generateRandomLocalDate() {
        int year = ThreadLocalRandom.current().nextInt(1976, 2019);
        int month = ThreadLocalRandom.current().nextInt(1, 12);
        int day = ThreadLocalRandom.current().nextInt(1, 28);
        return LocalDate.of(year, month, day);
    }
}
