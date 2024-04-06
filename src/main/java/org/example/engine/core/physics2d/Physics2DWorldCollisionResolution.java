package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.Vector2;

//https://github.com/RandyGaul/ImpulseEngine/blob/master/Manifold.h
public class Physics2DWorldCollisionResolution {

    private final Vector2 tmp = new Vector2();

    public void resolveCollision(Physics2DBody a, Physics2DBody b) {
        tmp.set(b.velocity).sub(a.velocity); // relative velocity

    }

    public static class Manifold {

        public Physics2DBody a;
        public Physics2DBody b;

        public float penetrationDepth;
        public Vector2 normal;
        public Array<Vector2> contacts;
        public int contactsCount;
        public float mixedRestitution;
        public float mixedDynamicFriction;
        public float mixedStaticFriction;

    }
}
