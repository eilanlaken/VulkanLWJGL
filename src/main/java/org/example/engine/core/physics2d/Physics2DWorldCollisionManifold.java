package org.example.engine.core.physics2d;

import org.example.engine.core.math.Vector2;

public final class Physics2DWorldCollisionManifold {

    public Physics2DBody a;
    public Physics2DBody b;

    public float depth;
    public Vector2 normal;

    public int contactsCount;
    public Vector2 contactPoint1;
    public Vector2 contactPoint2;

    public float mixedRestitution;
    public float mixedDynamicFriction;
    public float mixedStaticFriction;

}
