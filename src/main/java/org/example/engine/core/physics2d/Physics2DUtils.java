package org.example.engine.core.physics2d;

import org.example.engine.core.math.Shape2D;

public class Physics2DUtils {

    private Physics2DUtils() {}

    public static float calculateMass(final Shape2D shape, float density) {
        return shape.getArea() * density;
    }

}
