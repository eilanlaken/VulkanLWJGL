package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.memory.MemoryPool;

// https://github.com/RandyGaul/ImpulseEngine/blob/master/Manifold.h
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-basics-and-impulse-resolution--gamedev-6331t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-core-engine--gamedev-7493t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-friction-scene-and-jump-table--gamedev-7756t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-oriented-rigid-bodies--gamedev-8032t
public class Physics2DWorld {

    private Array<Physics2DBody> allBodies = new Array<>(false, 500);
    private Array<Physics2DBody> bodiesToAdd = new Array<>(false, 100);
    private Array<Physics2DBody> bodiesToRemove = new Array<>(false, 500);


    public Physics2DWorld() {

    }

    public void update(final float delta) {
        allBodies.removeAll(bodiesToRemove, true);
        allBodies.addAll(bodiesToAdd);

        bodiesToAdd.clear();
        bodiesToRemove.clear();

        for (Physics2DBody body : allBodies) {
            if (!body.active) continue;
            if (body.type == Physics2DBody.Type.STATIC) continue;
            Array<Vector2> forces = body.forces;
            for (Vector2 force : forces) {
                body.velocity.add(body.massInv * delta * force.x, body.massInv * delta * force.y);
            }
            body.shape.dx_dy_da(delta * body.velocity.x, delta * body.velocity.y, delta * body.angularVelocity);
            body.shape.update();
        }

        // broad phase

        // narrow phase

        // resolution

    }

    public void createBody() {

    }

    public void destroyBody() {

    }

    public void createJoint() {

    }

    public void destroyJoint() {

    }

    // TODO
    public void castRay() {

    }

}
