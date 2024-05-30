package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;

public class Physics2DConstraintSpring implements Physics2DConstraint {

    public Physics2DBody body_a;
    public Physics2DBody body_b;

    public float k;
    public float l;

    Physics2DConstraintSpring(Physics2DBody body_a, Physics2DBody body_b, float k, float l) {
        this.body_a = body_a;
        this.body_b = body_b;
        this.k = k;
        this.l = l;
    }

    @Override
    public void getBodies(CollectionsArray<Physics2DBody> out) {
        out.clear();
        out.add(body_a);
        out.add(body_b);
    }

    @Override
    public void update(float delta) {

    }

}
