package org.example.engine.core.physics2d_new;

import org.example.engine.core.math.Vector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape2d.Shape2D;

public final class CollisionManifold implements MemoryPool.Reset {

    public BodyCollider collider_a    = null;
    public BodyCollider collider_b    = null;
    public int          contacts      = 0;
    public float        depth         = 0;
    public Vector2      normal        = new Vector2();
    public Vector2      contactPoint1 = new Vector2();
    public Vector2      contactPoint2 = new Vector2();

    public CollisionManifold() {}

    @Override
    public void reset() {
        this.contacts = 0;
    }

}
