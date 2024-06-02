package org.example.engine.core.physics2d;

import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;

public final class Physics2DUtils {

    private Physics2DUtils() {}

    /**
     * Inverse transforms the given {@link MathVector2} and returns the result in the destination {@link MathVector2}.
     * @param vector the {@link MathVector2} to transform
     * @param out the {@link MathVector2} containing the result
     */
    public static void getInverseTransform(final Physics2DBody body, final MathVector2 vector, MathVector2 out) {
        float tx = vector.x - body.x();
        float ty = vector.y - body.y();
        float sin = MathUtils.sinDeg(body.angle());
        float cos = MathUtils.cosDeg(body.angle());
        out.x =  cos * tx + sin * ty;
        out.y = -sin * tx + cos * ty;
    }

    public static MathVector2 getInverseTransform(final Physics2DBody body, final MathVector2 vector) {
        MathVector2 out = new MathVector2();
        float tx = vector.x - body.x();
        float ty = vector.y - body.y();
        float sin = MathUtils.sinDeg(body.angle());
        float cos = MathUtils.cosDeg(body.angle());
        out.x =  cos * tx + sin * ty;
        out.y = -sin * tx + cos * ty;
        return out;
    }

    /**
     * Returns the relative angle between the two bodies given the reference angle.
     * @return double
     */
    public static float getRelativeRotationRad(Physics2DBody body_1, Physics2DBody body_2, float referenceAngleRad) {
        float rr = (body_1.angle() - body_2.angle()) * MathUtils.degreesToRadians - referenceAngleRad;
        if (rr < -MathUtils.PI) rr += MathUtils.PI2;
        if (rr >  MathUtils.PI) rr -= MathUtils.PI2;
        return rr;
    }

}
