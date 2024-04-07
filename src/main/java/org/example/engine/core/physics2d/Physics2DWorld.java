package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.memory.MemoryPool;

// https://github.com/RandyGaul/ImpulseEngine/blob/master/Manifold.h
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-basics-and-impulse-resolution--gamedev-6331t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-core-engine--gamedev-7493t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-friction-scene-and-jump-table--gamedev-7756t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-oriented-rigid-bodies--gamedev-8032t
public class Physics2DWorld {

    private static final int IMPULSE_RESOLUTION_ITERATIONS = 4;
    private final MemoryPool<Physics2DBody> bodyMemoryPool = new MemoryPool<>(Physics2DBody.class, 300);

    private Array<Physics2DBody> allBodies = new Array<>(false, 500);
    private Array<Physics2DBody> bodiesToAdd = new Array<>(false, 100);
    private Array<Physics2DBody> bodiesToRemove = new Array<>(false, 500);

    private Physics2DWorldCollisionPhaseBroad broadPhase;

    public Physics2DWorld() {

    }

    public void update(final float delta) {

    }

    public void createBody() {

    }

    public void destroyBody() {

    }

    public void createJoint() {

    }

    public void destroyJoint() {

    }

}
