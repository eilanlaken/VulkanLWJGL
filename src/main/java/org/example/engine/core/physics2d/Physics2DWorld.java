package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.TuplePair;
import org.example.engine.core.math.Shape2D;
import org.example.engine.core.math.Vector2;

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

    private Array<Physics2DBody> allBodies      = new Array<>(false, 500);
    private Array<Physics2DBody> bodiesToAdd    = new Array<>(false, 100);
    private Array<Physics2DBody> bodiesToRemove = new Array<>(false, 500);
    private short phase;

    private Array<TuplePair<Physics2DBody, Physics2DBody>> collisionCandidates = new Array<>(false, 200);

    public Physics2DWorld() {

    }

    public void update(final float delta) {
        this.phase = PHASE_PREPARATION;
        {
            allBodies.removeAll(bodiesToRemove, true);
            allBodies.addAll(bodiesToAdd);
            bodiesToAdd.clear();
            bodiesToRemove.clear();
            collisionCandidates.clear();
        }

        this.phase = PHASE_INTEGRATION;
        {
            for (Physics2DBody body : allBodies) {
                if (!body.active) continue;
                if (body.type == Physics2DBody.Type.STATIC) continue;
                Array<Vector2> forces = body.forces;
                for (Vector2 force : forces) {
                    body.velocity.add(body.massInv * delta * force.x, body.massInv * delta * force.y);
                }
                body.shape.dx_dy_rot(delta * body.velocity.x, delta * body.velocity.y, delta * body.angularVelocity);
                body.shape.update();

                // TODO: update broad phase cells.
            }
        }

        this.phase = PHASE_BROAD;
        {

        }

        this.phase = PHASE_NARROW;
        {

        }

        // resolution
        this.phase = PHASE_RESOLUTION;
        {

        }

    }

    // TODO: change to create using shapes.
    public Physics2DBody createBody(Shape2D shape, Vector2 position, Vector2 velocity) {
        Physics2DBody body = new Physics2DBody(shape, position, velocity);
        this.bodiesToAdd.add(body);
        return body;
    }

    public void destroyBody(int handle) {

    }

    public void createJoint() {

    }

    public void destroyJoint() {

    }

    // TODO
    public void castRay() {

    }

}
