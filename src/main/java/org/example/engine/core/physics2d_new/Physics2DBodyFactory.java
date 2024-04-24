package org.example.engine.core.physics2d_new;

import org.example.engine.core.math.MathVector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

// TODO: continue.
public final class Physics2DBodyFactory {

    public static final Class<? extends Shape2D>[] supportedShapeType = new Class[] {Shape2DAABB.class, Shape2DCircle.class, Shape2DRectangle.class, Shape2DPolygon.class, Shape2DUnion.class};
    private final MemoryPool<Physics2DBody> bodyMemoryPool;

    Physics2DBodyFactory(Physics2DWorld world) {
        this.bodyMemoryPool = world.bodyMemoryPool;
    }

    @Contract(pure = true)
    @NotNull Physics2DBody createBodyCircle(Object owner,
                                                 Physics2DBody.MotionType motionType,
                                                 float density, float friction, float restitution,
                                                 boolean ghost, int bitmask,
                                                 float radius) {
        Physics2DBody body = bodyMemoryPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;
        body.density = density;
        body.shape = new Shape2DCircle(radius);
        body.massInv = 1.0f / (body.shape.getArea() * density);
        System.out.println(body.massInv);
        body.friction = friction;
        body.restitution = restitution;
        body.ghost = ghost;
        body.bitmask = bitmask;
        return body;
    }

    // TODO
    @Contract(pure = true)
    @NotNull Physics2DBody createBodyRectangle(Object owner,
                                      boolean sleeping, Physics2DBody.MotionType motionType,
                                      MathVector2 velocity, float angularVelocity,
                                      float massInv, float density, float friction, float restitution,
                                      boolean ghost, int bitmask,
                                      float width, float height, float angle) {
        Physics2DBody body = bodyMemoryPool.allocate();
        body.owner = owner;
        body.off = sleeping;
        body.motionType = motionType;

        return body;
    }

    // TODO
    @Contract(pure = true)
    @NotNull Physics2DBody createBodyPolygon(Object owner,
                                               boolean sleeping, Physics2DBody.MotionType motionType,
                                               MathVector2 velocity, float angularVelocity,
                                               float massInv, float density, float friction, float restitution,
                                               boolean ghost, int bitmask,
                                               float[] vertices, int[] holes) {
        Physics2DBody body = bodyMemoryPool.allocate();
        body.owner = owner;
        body.off = sleeping;
        body.motionType = motionType;

        return body;
    }

    // TODO
    @Contract(pure = true)
    @NotNull Physics2DBody createBodyUnion(Object owner,
                                             boolean sleeping, Physics2DBody.MotionType motionType,
                                             MathVector2 velocity, float angularVelocity,
                                             float massInv, float density, float friction, float restitution,
                                             boolean ghost, int bitmask,
                                             Shape2D ...shapes) {
        Physics2DBody body = bodyMemoryPool.allocate();
        body.owner = owner;
        body.off = sleeping;
        body.motionType = motionType;

        return body;
    }

}
