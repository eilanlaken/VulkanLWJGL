package org.example.engine.core.physics2d_new;

import org.example.engine.core.math.Vector2;

public final class Physics2DUtils {

    private Physics2DUtils() {}

    public static void calculateCenterOfMass(final Iterable<Body> bodies, Vector2 out) {
        float totalMass = 0;
        out.set(0,0);
        for (Body body : bodies) {
            totalMass += body.mass;
            out.x += body.mass * body.x;
            out.y += body.mass * body.y;
        }
        out.scl(1.0f / totalMass);
    }



}
