package org.example.engine.core.math;

import java.util.Random;

public final class MathUtils {

    public static final double NANO_SECOND = 1.0 / 1000000000L;

    private static final Random random = new Random();

    public static int random(final int range) {
        return random.nextInt(range);
    }

    public static int clamp(int min, int max, int value) {
        return Math.max(min, Math.min(max, value));
    }

}
