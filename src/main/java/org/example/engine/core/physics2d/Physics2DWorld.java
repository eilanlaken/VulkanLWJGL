package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;

// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-basics-and-impulse-resolution--gamedev-6331t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-core-engine--gamedev-7493t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-friction-scene-and-jump-table--gamedev-7756t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-oriented-rigid-bodies--gamedev-8032t
public class Physics2DWorld {

    private static final int IMPULSE_RESOLUTION_ITERATIONS = 4;

    private Array<Physics2DBody> bodies;
    private Array<Physics2DJoint> joints;

    public Physics2DWorld() {
        this.bodies = new Array<>(false,100);
        this.joints = new Array<>(false, 100);
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
