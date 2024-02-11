package org.example.engine.core.math;

/**
 * A pyramid with it's top sliced off. Mainly used by a camera.
 *      ___
 *    /    \
 *  /_______\
 *  (this, but in 3D)
 *
 */
public class Shape3DFrustum implements Shape3D {

    @Override
    public boolean contains(float x, float y, float z) {
        return false;
    }
}
