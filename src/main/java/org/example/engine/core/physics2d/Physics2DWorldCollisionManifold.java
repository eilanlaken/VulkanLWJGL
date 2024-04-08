package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.Shape2D;
import org.example.engine.core.math.Vector2;

public final class Physics2DWorldCollisionManifold {

    public Physics2DBody a;
    public Physics2DBody b;

    public float penetrationDepth;
    public Vector2 normal;

    public int contactsCount;
    public Vector2 contactPoint1;
    public Vector2 contactPoint2;

    public float mixedRestitution;
    public float mixedDynamicFriction;
    public float mixedStaticFriction;

    // TODO: remove
    @Override
    public String toString() {
        return "Physics2DWorldCollisionManifold{" +
                "penetrationDepth=" + penetrationDepth +
                ", normal=" + normal +
                ", contactsCount=" + contactsCount +
                ", contactPoint1=" + contactPoint1 +
                ", contactPoint2=" + contactPoint2 +
                '}';
    }
}
