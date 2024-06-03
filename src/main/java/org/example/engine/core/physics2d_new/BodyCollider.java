package org.example.engine.core.physics2d_new;

import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape2d.Shape2D;

public class BodyCollider implements MemoryPool.Reset {

    protected Body body;
    private Shape2D shape;
    public float   density;
    public float   staticFriction;
    public float   dynamicFriction;
    public float   restitution;
    public boolean ghost;
    public int     bitmask;

    public BodyCollider(Shape2D shape) {
        this.shape = shape;
    }

    @Override
    public void reset() {
        this.body = null;
        this.shape = null;
    }

}
