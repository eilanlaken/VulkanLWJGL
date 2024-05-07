package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.graphics.GraphicsRenderer2D;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.memory.MemoryPool;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// https://github.com/RandyGaul/ImpulseEngine/blob/master/Manifold.h
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-basics-and-impulse-resolution--gamedev-6331t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-core-engine--gamedev-7493t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-friction-scene-and-jump-table--gamedev-7756t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-oriented-rigid-bodies--gamedev-8032t
public class Physics2DWorld {

    // constants
    public static final short PHASE_A_PREPARATION = 0;
    public static final short PHASE_B_INTEGRATION = 1;
    public static final short PHASE_C_BROAD       = 2;
    public static final short PHASE_D_NARROW      = 3;
    public static final short PHASE_E_RESOLUTION  = 4;

    // bodies, joints, constraints and manifolds
    public MemoryPool<Physics2DBody>       bodyMemoryPool      = new MemoryPool<>(Physics2DBody.class,     10);
    public MemoryPool<CollisionManifold>   manifoldMemoryPool  = new MemoryPool<>(CollisionManifold.class, 10);
    public CollectionsArray<Physics2DBody> allBodies           = new CollectionsArray<>(false, 500);
    public CollectionsArray<Physics2DBody> bodiesToAdd         = new CollectionsArray<>(false, 100);
    public CollectionsArray<Physics2DBody> bodiesToRemove      = new CollectionsArray<>(false, 500);

    protected final CollectionsArray<Physics2DWorldPhaseC.Cell> spacePartition = new CollectionsArray<>(false, 1024);
    protected final CollectionsArray<Physics2DWorldPhaseC.Cell> activeCells    = new CollectionsArray<>();
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

    protected final Set<CollisionPair>                      collisionCandidates = new HashSet<>();
    protected final CollectionsArray<CollisionManifold>     collisionManifolds  = new CollectionsArray<>(false, 200);

    protected final Physics2DWorldPhase[]      phases        = new Physics2DWorldPhase[5];
    protected final Physics2DWorldRenderer     debugRenderer = new Physics2DWorldRenderer(this);
    protected final Physics2DBodyFactory       bodyFactory   = new Physics2DBodyFactory(this);
    protected       Physics2DCollisionListener collisionListener;

    public Physics2DWorld(Physics2DCollisionListener collisionListener) {
        this.collisionListener = collisionListener != null ? collisionListener : new Physics2DCollisionListener() {};
        this.phases[PHASE_A_PREPARATION] = new Physics2DWorldPhaseA();
        this.phases[PHASE_B_INTEGRATION] = new Physics2DWorldPhaseB();
        this.phases[PHASE_C_BROAD]       = new Physics2DWorldPhaseC();
        this.phases[PHASE_D_NARROW]      = new Physics2DWorldPhaseD();
        this.phases[PHASE_E_RESOLUTION]  = new Physics2DWorldPhaseE();
    }

    public Physics2DWorld() {
        this(null);
    }

    public void update(final float delta) {
        this.phases[PHASE_A_PREPARATION].update(this, delta);
        this.phases[PHASE_B_INTEGRATION].update(this, delta);
        this.phases[PHASE_C_BROAD]      .update(this, delta);
        this.phases[PHASE_D_NARROW]     .update(this, delta);
        this.phases[PHASE_E_RESOLUTION] .update(this, delta);
    }

    public Physics2DBody createBodyCircle(Object owner, Physics2DBody.MotionType motionType,
                                          float x, float y, float angleDeg,
                                          float velX, float velY, float velAngleDeg,
                                          float density, float friction, float restitution,
                                          boolean ghost, int bitmask,
                                          float r) {
        Physics2DBody body = bodyFactory.createBodyCircle(owner, motionType, density, friction, restitution, ghost, bitmask, r);
        body.setMotionState(x, y, angleDeg, velX, velY, velAngleDeg);
        bodiesToAdd.add(body);
        return body;
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
