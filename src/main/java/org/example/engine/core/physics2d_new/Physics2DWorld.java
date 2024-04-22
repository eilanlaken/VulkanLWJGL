package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape.Shape2D;

// https://github.com/RandyGaul/ImpulseEngine/blob/master/Manifold.h
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-basics-and-impulse-resolution--gamedev-6331t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-core-engine--gamedev-7493t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-friction-scene-and-jump-table--gamedev-7756t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-oriented-rigid-bodies--gamedev-8032t
public class Physics2DWorld {

    private static final short PHASE_A_PREPARATION = 0;
    private static final short PHASE_B_INTEGRATION = 1;
    private static final short PHASE_C_BROAD       = 2;
    private static final short PHASE_D_NARROW      = 3;
    private static final short PHASE_E_RESOLUTION  = 4;

    public MemoryPool<Physics2DBody>       bodyMemoryPool      = new MemoryPool<>(Physics2DBody.class,     10);
    public MemoryPool<CollisionManifold>   manifoldMemoryPool  = new MemoryPool<>(CollisionManifold.class, 10);
    public CollectionsArray<Physics2DBody> allBodies           = new CollectionsArray<>(false, 500);
    public CollectionsArray<Physics2DBody> bodiesToAdd         = new CollectionsArray<>(false, 100);
    public CollectionsArray<Physics2DBody> bodiesToRemove      = new CollectionsArray<>(false, 500);

    // [0, 1], [2, 3], [4, 5], ... are collision candidates.
    public final CollectionsArray<Physics2DBody>     collisionCandidates = new CollectionsArray<>(false, 400);
    public final CollectionsArray<CollisionManifold> collisionManifolds  = new CollectionsArray<>(false, 200);

    // phases
    private final Physics2DWorldPhase[] phases = new Physics2DWorldPhase[5];

    public Physics2DWorld() {
        this.phases[PHASE_A_PREPARATION] = new Physics2DWorldPhaseAPreparation();
        this.phases[PHASE_B_INTEGRATION] = new Physics2DWorldPhaseBIntegration();
        this.phases[PHASE_C_BROAD]       = new Physics2DWorldPhaseCBroad();
        this.phases[PHASE_D_NARROW]      = new Physics2DWorldPhaseDNarrow();
        this.phases[PHASE_E_RESOLUTION]  = new Physics2DWorldPhaseEResolution();
    }

    public void update(final float delta) {
        this.phases[PHASE_A_PREPARATION].update(this, delta);
        this.phases[PHASE_B_INTEGRATION].update(this, delta);
        this.phases[PHASE_C_BROAD]      .update(this, delta);
        this.phases[PHASE_D_NARROW]     .update(this, delta);
        this.phases[PHASE_E_RESOLUTION] .update(this, delta);
    }

    public Physics2DBody createBody(Shape2D shape, MathVector2 position, float angle, MathVector2 velocity) {

        return null;
    }

    public void destroyBody(final Physics2DBody body) {

    }

    public void createJoint() {

    }

    public void destroyJoint() {

    }

    public void castRay() {

    }

    public static final class CollisionManifold implements MemoryPool.Reset {

        public Physics2DBody a;
        public Physics2DBody b;

        public float depth;
        public MathVector2 normal;

        public int contactsCount;
        public MathVector2 contactPoint1;
        public MathVector2 contactPoint2;

        public float mixedRestitution;
        public float mixedDynamicFriction;
        public float mixedStaticFriction;

        @Override
        public void reset() {
            this.a = null;
            this.b = null;
            this.contactsCount = 0;
        }
    }

}
