package org.example.engine.core.physics2d_new;

import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape2d.Shape2D;

public class BodyCollider {

    public Body    body;
    public Shape shape;
    public float   density;
    public float   staticFriction;
    public float   dynamicFriction;
    public float   restitution;
    public boolean ghost;
    public int     bitmask;

    public BodyCollider() {}

    public BodyCollider(Body body, Shape shape, float density, float staticFriction, float dynamicFriction, float restitution, boolean ghost, int bitmask) {
        this.body = body;
        this.shape = shape;
        this.density = density;
        this.staticFriction = staticFriction;
        this.dynamicFriction = dynamicFriction;
        this.restitution = restitution;
        this.ghost = ghost;
        this.bitmask = bitmask;
    }

}
