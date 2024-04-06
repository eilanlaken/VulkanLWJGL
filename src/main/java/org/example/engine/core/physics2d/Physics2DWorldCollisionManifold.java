package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.Vector2;

public class Physics2DWorldCollisionManifold {

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
