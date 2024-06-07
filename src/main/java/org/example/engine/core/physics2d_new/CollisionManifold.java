package org.example.engine.core.physics2d_new;

import org.example.engine.core.math.Vector2;
import org.example.engine.core.memory.MemoryPool;

// TODO: use box2d manifold - to resolve contact constraints.
public final class CollisionManifold implements MemoryPool.Reset {

    public Type         type          = null;
    public BodyCollider collider_a    = null;
    public BodyCollider collider_b    = null;
    public int          contacts      = 0;
    public Vector2      normal        = new Vector2();
    public Vector2      contactPoint1 = new Vector2();
    public Vector2      contactPoint2 = new Vector2();

    public CollisionManifold() {}

    @Override
    public void reset() {
        this.contacts = 0;
    }

    protected enum Type {
        CIRCLE_VS_CIRCLE,
        EDGE_A, // TODO: rename after you understand
        EDGE_B, // TODO: rename after you understand
    }

}
