package org.example.engine.core.math;

public class AlgorithmsCollisions {

    private AlgorithmsCollisions() {}

    public static boolean doBoundingSpheresCollide(final Shape2D a, final Shape2D b) {
        final float sum = a.getBoundingRadius() + b.getBoundingRadius();
        float dc2 = Vector2.dst2(a.x, a.y, b.x, b.y);
        return dc2 < sum * sum;
    }

}