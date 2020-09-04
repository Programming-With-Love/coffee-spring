package site.zido.coffee.core.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomUtils {

    private RandomUtils() {
    }

    private static Random getRandom() {
        return ThreadLocalRandom.current();
    }

    public static String lowerStr(int len) {
        return str(len, 'a', 'z');
    }

    public static String upperStr(int len) {
        return str(len, 'A', 'Z');
    }

    public static String numeric(int len) {
        return str(len, '0', '9');
    }

    public static String ascii(int len) {
        return str(len, 32, 126);
    }

    public static String str(int len, int leftLimit, int rightLimit) {
        Random random = getRandom();
        return random.ints(leftLimit, rightLimit + 1)
                .limit(len)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
