package org.example.engine.core.math;

public interface Shape3D {

    default boolean contains(final Vector3 point) {
        return contains(point.x, point.y, point.z);
    }
    boolean contains(float x, float y, float z);

}
