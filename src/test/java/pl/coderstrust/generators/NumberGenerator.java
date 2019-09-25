package pl.coderstrust.generators;

import java.util.concurrent.ThreadLocalRandom;

public class NumberGenerator {


    public static int generateRandomNumber(int length) {
        int size = (int) Math.pow(10, length - 1);
        return ThreadLocalRandom.current().nextInt(1,size);
    }
}
