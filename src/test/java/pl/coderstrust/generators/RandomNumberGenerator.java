package pl.coderstrust.generators;

import java.util.Random;

public class NumberGenerator {

    private static Random random = new Random();

    public static int generateRandomNumber(int length) {
        int size = (int) Math.pow(10, length - 1);
        return random.nextInt() + size;
    }
}
