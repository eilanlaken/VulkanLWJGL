package org.example.engine.core.shape;

import org.example.engine.core.math.Matrix4x4;
import org.example.engine.core.math.Vector3;

@Deprecated
public interface Shape3D_old {

    default boolean contains(final Vector3 point) {
        return contains(point.x, point.y, point.z);
    }
    boolean contains(float x, float y, float z);
    float getVolume();
    float getSurfaceArea();
    void update(Matrix4x4 m);

}
