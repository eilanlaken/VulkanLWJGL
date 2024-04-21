package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.physics2d_new.Physics2DBody;
import org.example.engine.core.shape.Shape2D;

// https://github.com/RandyGaul/ImpulseEngine/blob/master/Manifold.h
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-basics-and-impulse-resolution--gamedev-6331t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-core-engine--gamedev-7493t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-friction-scene-and-jump-table--gamedev-7756t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-oriented-rigid-bodies--gamedev-8032t
public class Physics2DWorld {

    private static final short PHASE_PREPARATION = 0;
    private static final short PHASE_INTEGRATION = 1;
    private static final short PHASE_BROAD       = 2;
    private static final short PHASE_NARROW      = 3;
    private static final short PHASE_RESOLUTION  = 4;

    private MemoryPool<Physics2DBody> bodyMemoryPool = new MemoryPool<>(Physics2DBody.class, 10);
    private MemoryPool<Physics2DWorldCollision.Manifold> manifoldMemoryPool = new MemoryPool<>(Physics2DWorldCollision.Manifold.class, 10);

    public CollectionsArray<Physics2DBody> allBodies      = new CollectionsArray<>(false, 500);
    public CollectionsArray<Physics2DBody> bodiesToAdd    = new CollectionsArray<>(false, 100);
    public CollectionsArray<Physics2DBody> bodiesToRemove = new CollectionsArray<>(false, 500);
    public short phase;

    // [0, 1], [2, 3], [4, 5], ... are collision candidates.
    public final CollectionsArray<Physics2DBody>                    collisionCandidates = new CollectionsArray<>(false, 400);
    public final CollectionsArray<Physics2DWorldCollision.Manifold> collisionManifolds  = new CollectionsArray<>(false, 200);

    public Physics2DWorld() {

    }

    public void update(final float delta) {
        this.phase = PHASE_PREPARATION;
        {

        }

        this.phase = PHASE_INTEGRATION;
        {

        }

        this.phase = PHASE_BROAD;
        {

        }

        this.phase = PHASE_NARROW;
        {

        }

        this.phase = PHASE_RESOLUTION;
        {

        }

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

}
