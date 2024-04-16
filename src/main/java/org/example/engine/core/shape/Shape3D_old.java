package org.example.engine.core.shape;

import org.example.engine.core.math.MathMatrix4;
import org.example.engine.core.math.MathVector3;

@Deprecated
public interface Shape3D_old {

    default boolean contains(final MathVector3 point) {
        return contains(point.x, point.y, point.z);
    }
    boolean contains(float x, float y, float z);
    float getVolume();
    float getSurfaceArea();
    void update(MathMatrix4 m);

}
