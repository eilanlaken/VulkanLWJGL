package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathVector2;

public class Physics2DConstraintWeld implements Physics2DConstraint {

    public Physics2DBody body_a;
    public Physics2DBody body_b;

    public MathVector2 difference;
    public float       relativeAngle;

    Physics2DConstraintWeld(Physics2DBody body_a, Physics2DBody body_b) {
        this.body_a = body_a;
        this.body_b = body_b;
        this.difference = new MathVector2(body_a.shape.x(), body_a.shape.y()).sub(body_b.shape.x(), body_b.shape.y());
        this.relativeAngle = body_a.shape.angle() - body_b.shape.angle();
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
