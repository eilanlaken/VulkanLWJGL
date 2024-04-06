package org.example.engine.core.physics2d;

import org.example.engine.core.math.Vector2;

//https://github.com/RandyGaul/ImpulseEngine/blob/master/Manifold.h
public class Physics2DWorldCollisionPhaseResolution {

    private final Vector2 tmp = new Vector2();

    public void resolveCollision(Physics2DBody a, Physics2DBody b) {
        tmp.set(b.velocity).sub(a.velocity); // relative velocity

    }

}
