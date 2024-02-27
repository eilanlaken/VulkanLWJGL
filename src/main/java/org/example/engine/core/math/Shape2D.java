package org.example.engine.core.math;

public interface Shape2D {

    default boolean contains(final Vector2 point) {
        return contains(point.x, point.y);
    }
    boolean contains(float x, float y);
    float getArea();
    float getPerimeter();

}
