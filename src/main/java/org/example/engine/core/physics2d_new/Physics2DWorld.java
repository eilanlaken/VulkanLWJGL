package org.example.engine.core.physics2d_new;

import org.example.engine.core.async.AsyncUtils;
import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.graphics.GraphicsRenderer2D;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.physics2d.Physics2DWorldPhaseC;
import org.example.engine.core.physics2d_new.Physics2DBody;
import org.example.engine.core.physics2d_new.Physics2DBodyFactory;
import org.example.engine.core.physics2d_new.Physics2DCollisionListener;
import org.example.engine.core.physics2d_new.*;
import org.example.engine.core.shape.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// https://github.com/RandyGaul/ImpulseEngine/blob/master/Manifold.h
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-basics-and-impulse-resolution--gamedev-6331t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-core-engine--gamedev-7493t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-friction-scene-and-jump-table--gamedev-7756t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-oriented-rigid-bodies--gamedev-8032t
public class Physics2DWorld {

    // bodies, joints, constraints and manifolds
    public MemoryPool<Physics2DBody>       bodyMemoryPool     = new MemoryPool<>(Physics2DBody.class,     10);
    public MemoryPool<CollisionManifold>   manifoldMemoryPool = new MemoryPool<>(CollisionManifold.class, 10);
    public CollectionsArray<Physics2DBody> allBodies          = new CollectionsArray<>(false, 500);
    public CollectionsArray<Physics2DBody> bodiesToAdd        = new CollectionsArray<>(false, 100);
    public CollectionsArray<Physics2DBody> bodiesToRemove     = new CollectionsArray<>(false, 500);

    protected final CollectionsArray<Cell> spacePartition = new CollectionsArray<>(false, 1024);
    protected final CollectionsArray<Cell> activeCells    = new CollectionsArray<>();
    private   final MemoryPool<Cell>       cellMemoryPool = new MemoryPool<>(Cell.class,1024);
    protected float worldWidth    = 0;
    protected float worldMinX     = 0;
    protected float worldMaxX     = 0;
    protected float worldMinY     = 0;
    protected float worldMaxY     = 0;
    protected float worldMaxR     = 0;
    protected float worldHeight   = 0;
    protected int   rows          = 0;
    protected int   cols          = 0;
    protected float cellWidth     = 0;
    protected float cellHeight    = 0;
    protected int   bodiesCreated = 0;

    protected final Set<CollisionPair>                  collisionCandidates = new HashSet<>();
    protected final CollectionsArray<CollisionManifold> collisionManifolds  = new CollectionsArray<>(false, 200);
    protected final Physics2DWorldRenderer              debugRenderer       = new Physics2DWorldRenderer(this);
    protected final Physics2DBodyFactory                bodyFactory         = new Physics2DBodyFactory(this);

    protected Physics2DCollisionListener collisionListener;

    // debugger options
    public boolean renderBroadPhase = false;
    public boolean renderManifolds  = true;
    public boolean renderVelocities = false;
    public boolean renderBodies     = true;

    public Physics2DWorld(Physics2DCollisionListener collisionListener) {
        this.collisionListener = collisionListener != null ? collisionListener : new Physics2DCollisionListener() {};
    }

    public Physics2DWorld() {
        this(null);
    }

    public void update(final float delta) {
        // add and remove bodies.


    }

    private void phase_prepare_a() {
        for (Physics2DBody body : bodiesToRemove) {
            allBodies.removeValue(body, true);
            bodyMemoryPool.free(body);
        }
        for (Physics2DBody body : bodiesToAdd) {
            allBodies.add(body);
            body.created = true;
            body.index = bodiesCreated;
            bodiesCreated++;
        }
        bodiesToRemove.clear();
        bodiesToAdd.clear();
    }

    private void updateBroadPhase_old(float deltaTime) {
        worldMinX = Float.POSITIVE_INFINITY;
        worldMaxX = Float.NEGATIVE_INFINITY;
        worldMinY = Float.POSITIVE_INFINITY;
        worldMaxY = Float.NEGATIVE_INFINITY;
        worldMaxR = Float.NEGATIVE_INFINITY;

        for (Physics2DBody body : allBodies) {
            if (body.off) continue;
            if (body.motionType == Physics2DBody.MotionType.NEWTONIAN) {
                body.velocity.add(body.massInv * deltaTime * body.netForce.x, body.massInv * deltaTime * body.netForce.y);
                body.omega += body.netTorque * (body.inertiaInv) * deltaTime;
            }
            if (body.motionType != Physics2DBody.MotionType.STATIC) {
                body.shape.dx_dy_rot(deltaTime * body.velocity.x, deltaTime * body.velocity.y, deltaTime * body.omega);
            }
            body.shape.update();
            body.netForce.set(0, 0);
            body.netTorque = 0;
            body.collidesWith.clear();

            worldMinX = Math.min(worldMinX, body.shape.getMinExtentX());
            worldMaxX = Math.max(worldMaxX, body.shape.getMaxExtentX());
            worldMinY = Math.min(worldMinY, body.shape.getMinExtentY());
            worldMaxY = Math.max(worldMaxY, body.shape.getMaxExtentY());
            worldMaxR = Math.max(worldMaxR, body.shape.getBoundingRadius());
        }

        final float maxDiameter = 2 * worldMaxR;
        worldWidth  = Math.abs(worldMaxX - worldMinX);
        worldHeight = Math.abs(worldMaxY - worldMinY);
        rows = Math.min((int) Math.ceil(worldHeight  / maxDiameter), 32);
        cols = Math.min((int) Math.ceil(worldWidth   / maxDiameter), 32);
        cellWidth  = worldWidth  / cols;
        cellHeight = worldHeight / rows;
    }

    @Deprecated public Physics2DBody createBodyCircle(Object owner, Physics2DBody.MotionType motionType,
                                          float x, float y, float angleDeg,
                                          float velX, float velY, float velAngleDeg,
                                          float density, float friction, float restitution,
                                          boolean ghost, int bitmask,
                                          float r) {
        Physics2DBody body = bodyFactory.createBodyCircle(owner, motionType, density, friction, restitution, ghost, bitmask, Math.abs(r));
        body.setMotionState(x, y, angleDeg, velX, velY, velAngleDeg);
        bodiesToAdd.add(body);
        return body;
    }

    @Deprecated public Physics2DBody createBodyCircle(Object owner, Physics2DBody.MotionType motionType,
                                          float x, float y, float angleDeg,
                                          float velX, float velY, float velAngleDeg,
                                          float density, float friction, float restitution,
                                          boolean ghost, int bitmask,
                                          float r, float offsetX, float offsetY) {
        Physics2DBody body = bodyFactory.createBodyCircle(owner, motionType, density, friction, restitution, ghost, bitmask, Math.abs(r), offsetX, offsetY);
        body.setMotionState(x, y, angleDeg, velX, velY, velAngleDeg);
        bodiesToAdd.add(body);
        return body;
    }

    @Deprecated public Physics2DBody createBodyRectangle(Object owner, Physics2DBody.MotionType motionType,
                                             float x, float y, float angleDeg,
                                             float velX, float velY, float velAngleDeg,
                                             float density, float friction, float restitution,
                                             boolean ghost, int bitmask,
                                             float width, float height, float angle) {
        Physics2DBody body = bodyFactory.createBodyRectangle(owner, motionType, density, friction, restitution, ghost, bitmask, Math.abs(width), Math.abs(height), angle);
        body.setMotionState(x, y, angleDeg, velX, velY, velAngleDeg);
        bodiesToAdd.add(body);
        return body;
    }

    @Deprecated public Physics2DBody createBodyRectangle(Object owner, Physics2DBody.MotionType motionType,
                                             float x, float y, float angleDeg,
                                             float velX, float velY, float velAngleDeg,
                                             float density, float friction, float restitution,
                                             boolean ghost, int bitmask,
                                             float width, float height, float offsetX, float offsetY, float angle) {
        Physics2DBody body = bodyFactory.createBodyRectangle(owner, motionType, density, friction, restitution, ghost, bitmask, Math.abs(width), Math.abs(height), offsetX, offsetY, angle);
        body.setMotionState(x, y, angleDeg, velX, velY, velAngleDeg);
        bodiesToAdd.add(body);
        return body;
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
                                           Shape2D...shapes) {
        Physics2DBody body = bodyMemoryPool.allocate();
        body.owner = owner;
        body.off = sleeping;
        body.motionType = motionType;

        return body;
    }

    public static float calculateMomentOfInertia(final Shape2D shape, float density) {
        return 1;
    }

    public void destroyBody(final Physics2DBody body) {
        bodiesToRemove.add(body);
    }

    public void createJoint() {

    }

    public void destroyJoint() {

    }

    public void castRay() {

    }

    public void render(GraphicsRenderer2D renderer) {
        debugRenderer.render(renderer);
    }

    public static final class CollisionManifold implements MemoryPool.Reset {

        public Physics2DBody a               = null;
        public Physics2DBody b               = null;
        public int           contacts        = 0;
        public float         depth           = 0;
        public MathVector2   normal          = new MathVector2();
        public MathVector2   contactPoint1   = new MathVector2();
        public MathVector2   contactPoint2   = new MathVector2();
        public float         staticFriction  = 0;
        public float         dynamicFriction = 0;

        @Override
        public void reset() {
            this.a = null;
            this.b = null;
            this.contacts = 0;
        }

    }

    public static final class Cell implements MemoryPool.Reset {

        private final CollectionsArray<Physics2DBody> bodies = new CollectionsArray<>(false, 2);
        private final CollectionsArray<CollisionPair> pairs  = new CollectionsArray<>(false, 2);

        private boolean active = false;

        // TODO: remove, just for debug rendering
        float x;
        float y;

        public Cell() {}

        @Override
        public void reset() {
            pairs.clear();
            bodies.clear();
            active = false;
        }

    }

    public static class CollisionPair implements MemoryPool.Reset {

        public Physics2DBody a;
        public Physics2DBody b;

        public CollisionPair() {}

        public CollisionPair(Physics2DBody a, Physics2DBody b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CollisionPair that = (CollisionPair) o;
            if (Objects.equals(a, that.a) && Objects.equals(b, that.b)) return true;
            if (Objects.equals(b, that.a) && Objects.equals(a, that.b)) return true;
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(a) + Objects.hashCode(b); // A commutative operation to ensure symmetry
        }

        @Override
        public void reset() {
            a = null;
            b = null;
        }

    }
}
