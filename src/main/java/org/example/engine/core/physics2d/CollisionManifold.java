package org.example.engine.core.physics2d;

import org.example.engine.core.math.Vector2;
import org.example.engine.core.memory.MemoryPool;

public final class CollisionManifold implements MemoryPool.Reset {

    public BodyCollider collider_a = null;
    public BodyCollider collider_b = null;
    public float        depth     = 0;
    public Vector2      normal    = new Vector2();
    public int          contacts  = 0;
    public Vector2      contact_a = new Vector2();
    public Vector2      contact_b = new Vector2();

    public CollisionManifold() {}

    @Override
    public void reset() {
        this.contacts = 0;
    }

}
