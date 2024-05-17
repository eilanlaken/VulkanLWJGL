package org.example.engine.core.physics2d;

import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

// TODO: continue.
public final class Physics2DBodyFactory {

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
        body.inertiaInv = 1.0f / calculateMomentOfInertia(body.shape, density);
        body.staticFriction = friction;
        body.restitution = MathUtils.clampFloat(restitution, 0, 1.0f);
        body.ghost = ghost;
        body.bitmask = bitmask;
        return body;
    }

    @Contract(pure = true)
    @NotNull Physics2DBody createBodyCircle(Object owner,
                                            Physics2DBody.MotionType motionType,
                                            float density, float friction, float restitution,
                                            boolean ghost, int bitmask,
                                            float radius, float offsetX, float offsetY) {
        Physics2DBody body = bodyMemoryPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;
        body.density = density;
        body.shape = new Shape2DCircle(radius, offsetX, offsetY);
        body.massInv = 1.0f / (body.shape.getArea() * density);
        body.inertiaInv = 1.0f / calculateMomentOfInertia(body.shape, density);
        body.staticFriction = friction;
        body.restitution = MathUtils.clampFloat(restitution, 0, 1.0f);
        body.ghost = ghost;
        body.bitmask = bitmask;
        return body;
    }

    @Contract(pure = true)
    @NotNull Physics2DBody createBodyRectangle(Object owner,
                                      Physics2DBody.MotionType motionType,
                                      float density, float friction, float restitution,
                                      boolean ghost, int bitmask,
                                      float width, float height, float angle) {
        Physics2DBody body = bodyMemoryPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;
        body.density = density;
        body.shape = new Shape2DRectangle(width, height, angle);
        body.massInv = 1.0f / (body.shape.getArea() * density);
        body.inertiaInv = 1.0f / calculateMomentOfInertia(body.shape, density);
        body.staticFriction = friction;
        body.restitution = MathUtils.clampFloat(restitution, 0, 1.0f);
        body.ghost = ghost;
        body.bitmask = bitmask;
        return body;
    }

    @Contract(pure = true)
    @NotNull Physics2DBody createBodyRectangle(Object owner,
                                               Physics2DBody.MotionType motionType,
                                               float density, float friction, float restitution,
                                               boolean ghost, int bitmask,
                                               float width, float height, float offsetX, float offsetY, float angle) {
        Physics2DBody body = bodyMemoryPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;
        body.density = density;
        body.shape = new Shape2DRectangle(offsetX, offsetY, width, height, angle);
        body.massInv = 1.0f / (body.shape.getArea() * density);
        body.inertiaInv = 1.0f / calculateMomentOfInertia(body.shape, density);
        body.staticFriction = friction;
        body.restitution = MathUtils.clampFloat(restitution, 0, 1.0f);
        body.ghost = ghost;
        body.bitmask = bitmask;
        return body;
    }

    @Contract(pure = true)
    @NotNull Physics2DBody createBodyPolygon(Object owner,
                                             Physics2DBody.MotionType motionType,
                                             float density, float friction, float restitution,
                                             boolean ghost, int bitmask,
                                             float[] vertices) {
        Physics2DBody body = bodyMemoryPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;
        body.density = density;

        final boolean isConvex = ShapeUtils.isPolygonConvex(vertices);
        if (isConvex) {
            body.shape = new Shape2DPolygon(vertices);
        } else {
            // TODO: create union of triangles.
        }
        //body.shape = new Shape2DRectangle(width, height, angle);

        body.massInv = 1.0f / (body.shape.getArea() * density);
        body.inertiaInv = 1.0f / calculateMomentOfInertia(body.shape, density);
        body.staticFriction = friction;
        body.restitution = MathUtils.clampFloat(restitution, 0, 1.0f);
        body.ghost = ghost;
        body.bitmask = bitmask;
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

    public static float calculateMomentOfInertia(final Shape2D shape, float density) {
        return 1;
    }

}
