package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathVector2;

/*
TODO:

read here
https://dyn4j.org/2010/12/weld-constraint/
https://github.com/acrlw/Physics2D/blob/master/Physics2D/include/physics2d_weld_joint.h
 */
public class Physics2DConstraintWeld implements Physics2DConstraint {

    public final Physics2DBody body_a;
    public final Physics2DBody body_b;

    public final MathVector2 difference;
    public final float       relativeAngle;

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
        MathVector2 currentRelativePosition = new MathVector2(body_b.shape.x(), body_b.shape.y())
                .sub(body_a.shape.x(), body_a.shape.y());

        float currentRelativeAngle = body_b.shape.angle() - body_a.shape.angle();

        System.out.println(difference);
        // Calculate the impulse to correct the position
        MathVector2 positionError = currentRelativePosition.sub(difference);
        MathVector2 positionCorrection = positionError.scl(delta);

        System.out.println(positionCorrection);

        // Apply impulses to both bodies to correct the position
        body_a.applyLinearImpulseToCenter(positionCorrection.x * 0.5f, positionCorrection.y * 0.5f);
        body_b.applyLinearImpulseToCenter(positionCorrection.x * -0.5f, positionCorrection.y * -0.5f);

        // Calculate the angular impulse to correct the angle
        float angleError = currentRelativeAngle - relativeAngle;
        float angleCorrection = angleError / delta;

        // Apply angular impulses to both bodies to correct the angle
        //body_a.applyAngularImpulse(-angleCorrection * 0.5f);
        //body_b.applyAngularImpulse(angleCorrection * 0.5f);
    }

}
