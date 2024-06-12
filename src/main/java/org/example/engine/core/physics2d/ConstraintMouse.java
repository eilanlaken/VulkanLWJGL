package org.example.engine.core.physics2d;

import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Matrix2x2;
import org.example.engine.core.math.Vector2;

import static org.example.engine.core.math.Matrix2x2.*;

public class ConstraintMouse extends Constraint {

    private final Vector2 m_localAnchorB = new Vector2();
    private final Vector2 m_targetA = new Vector2();
    private float m_frequencyHz;
    private float m_dampingRatio;
    private float m_beta;

    // Solver shared
    private final Vector2 m_impulse = new Vector2();
    private float m_maxForce;
    private float m_gamma;

    // Solver temp
    private int m_indexB;
    private final Vector2 m_rB = new Vector2();
    private final Vector2 m_localCenterB = new Vector2();
    private float m_invMassB;
    private float m_invIB;
    private final Matrix2x2 m_mass = new Matrix2x2();
    private final Vector2 m_C = new Vector2();

    public ConstraintMouse(final Body body, Vector2 target) {
        super(body);
        m_targetA.set(target);


        final float px = target.x - body.x;
        final float py = target.y - body.y;
        final float s = MathUtils.sinRad(body.aRad);
        final float c = MathUtils.cosRad(body.aRad);
        m_localAnchorB.x = (c * px + s * py);
        m_localAnchorB.y = (-s * px + c * py);

        m_maxForce = 500;//def.maxForce;
        m_impulse.zero();

        m_frequencyHz = 1;//def.frequencyHz;
        m_dampingRatio = 1;// def.dampingRatio;

        m_beta = 0;
        m_gamma = 0;
    }

    @Override
    void prepare(float delta) {
        m_localCenterB.set(body1.x, body1.y);
        m_invMassB = body1.invM;
        m_invIB = body1.invI;

        Vector2 cB = new Vector2(body1.x, body1.y);
        float aB = body1.aRad;
        Vector2 vB = new Vector2(body1.vx, body1.vy);
        float wB = body1.wRad;

        final Vector2 qB = new Vector2(MathUtils.sinRad(body1.aRad), MathUtils.cosRad(body1.aRad));

        float mass = body1.M;

        // Frequency
        float omega = 2.0f * MathUtils.PI * m_frequencyHz;

        // Damping coefficient
        float d = 2.0f * mass * m_dampingRatio * omega;

        // Spring stiffness
        float k = mass * (omega * omega);

        // magic formulas
        // gamma has units of inverse mass.
        // beta has units of inverse time.
        float h = delta;
        m_gamma = h * (d + h * k);
        if (m_gamma != 0.0f) {
            m_gamma = 1.0f / m_gamma;
        }
        m_beta = h * k * m_gamma;

        Vector2 temp = new Vector2();

        // Compute the effective mass matrix.
        Vector2 tmp = temp.set(m_localAnchorB).sub(m_localCenterB);

        m_rB.x = qB.x * tmp.x - qB.x * tmp.y;
        m_rB.y = qB.y * tmp.x + qB.y * tmp.y;

        // K = [(1/m1 + 1/m2) * eye(2) - skew(r1) * invI1 * skew(r1) - skew(r2) * invI2 * skew(r2)]
        // = [1/m1+1/m2 0 ] + invI1 * [r1.y*r1.y -r1.x*r1.y] + invI2 * [r1.y*r1.y -r1.x*r1.y]
        // [ 0 1/m1+1/m2] [-r1.x*r1.y r1.x*r1.x] [-r1.x*r1.y r1.x*r1.x]
        final Matrix2x2 K = new Matrix2x2();
        K.val[M00] = m_invMassB + m_invIB * m_rB.y * m_rB.y + m_gamma;
        K.val[M10] = -m_invIB * m_rB.x * m_rB.y;
        K.val[M01] = K.val[M10];
        K.val[M11] = m_invMassB + m_invIB * m_rB.x * m_rB.x + m_gamma;

        m_mass.set(K.inv());

        m_C.set(cB).add(m_rB).sub(m_targetA);
        m_C.scl(m_beta);

        // Cheat with some damping
        wB *= 0.98f;

        m_impulse.scl(1);
        vB.x += m_invMassB * m_impulse.x;
        vB.y += m_invMassB * m_impulse.y;
        wB += m_invIB * Vector2.crs(m_rB, m_impulse);

        body1.wRad = wB;
    }

    @Override
    void solveVelocity(float delta) {
        Vector2 vB = new Vector2(body1.vx, body1.vy);
        float wB = body1.wRad;

        // Cdot = v + cross(w, r)
        Vector2 Cdot = Vector2.crs(wB, m_rB);
        Cdot.add(vB);

        final Vector2 impulse = new Vector2();
        final Vector2 temp = new Vector2();

        temp.set(m_impulse).scl(m_gamma).add(m_C).add(Cdot).negate();

        impulse.x = m_mass.val[M00] * temp.x + m_mass.val[M01] * temp.y;
        impulse.y = m_mass.val[M10] * temp.x + m_mass.val[M11] * temp.y;

        temp.set(m_impulse);
        m_impulse.add(impulse);
        float maxImpulse = delta * m_maxForce;
        if (m_impulse.len2() > maxImpulse * maxImpulse) {
            m_impulse.scl(maxImpulse / m_impulse.len());
        }
        impulse.set(m_impulse).sub(temp);

        vB.x += m_invMassB * impulse.x;
        vB.y += m_invMassB * impulse.y;
        wB += m_invIB * Vector2.crs(m_rB, impulse);

        body1.wRad = wB;
    }

    @Override
    boolean solvePosition(float delta) {
        return true;
    }

}
