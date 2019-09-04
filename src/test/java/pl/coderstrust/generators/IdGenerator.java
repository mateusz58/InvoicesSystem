package pl.coderstrust.generators;

import java.util.concurrent.ThreadLocalRandom;

public class IdGenerator {

    public static Long getRandomId() {
        return ThreadLocalRandom.current().nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
    }
}
