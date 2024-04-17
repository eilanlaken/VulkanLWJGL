package org.example.engine.core.physics2d;

import org.example.engine.core.math.MathVector2;

public final class Physics2DCollisionManifold {

    public Physics2DBody a;
    public Physics2DBody b;

    public float depth;
    public MathVector2 normal;

    public int contactsCount;
    public MathVector2 contactPoint1;
    public MathVector2 contactPoint2;

    public float mixedRestitution;
    public float mixedDynamicFriction;
    public float mixedStaticFriction;

}
