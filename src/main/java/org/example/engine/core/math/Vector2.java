package org.example.engine.core.math;

public class Vector2 {

    public float x;
    public float y;

    public Vector2() {

    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static float dist(float x1, float y1, float x2, float y2) {
        final float dx = x2 - x1;
        final float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static float dist(final Vector2 a, final Vector2 b) {
        final float dx = b.x - a.x;
        final float dy = b.y - a.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

}
