package org.example.engine.core.physics2d_new;

import org.example.engine.core.math.MathVector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

// TODO: continue.
public final class Physics2DWorldBodyFactory {

    public static final Class<? extends Shape2D>[] supportedShapeType = new Class[] {Shape2DAABB.class, Shape2DCircle.class, Shape2DRectangle.class, Shape2DPolygon.class};
    private final MemoryPool<Physics2DBody> bodyMemoryPool;

    Physics2DWorldBodyFactory(Physics2DWorld world) {
        this.bodyMemoryPool = world.bodyMemoryPool;
    }

    @Contract(pure = true)
    @NotNull Physics2DBody createBody(Object owner,
                                      boolean sleeping, Physics2DBody.Type type, Shape2D shape,
                                      MathVector2 velocity, float angularVelocity,
                                      float massInv, float density, float friction, float restitution,
                                      boolean ghost, int bitmask) {
        Physics2DBody body = bodyMemoryPool.allocate();
        body.owner = owner;
        body.sleeping = sleeping;
        body.type = type;

        return body;
    }

    @Contract(pure = true)
    @NotNull Physics2DBody createRectangularBody(Object owner,
                                      boolean sleeping, Physics2DBody.Type type,
                                      float width, float height,
                                      MathVector2 velocity, float angularVelocity,
                                      float massInv, float density, float friction, float restitution,
                                      boolean ghost, int bitmask) {
        Physics2DBody body = bodyMemoryPool.allocate();
        body.owner = owner;
        body.sleeping = sleeping;
        body.type = type;

        return body;
    }

}
