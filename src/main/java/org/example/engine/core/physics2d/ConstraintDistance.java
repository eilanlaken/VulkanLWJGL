package org.example.engine.core.physics2d;

import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;

public class ConstraintDistance extends Constraint {

    private float m_frequencyHz;
    private float m_dampingRatio;
    private float m_bias;

    // Solver shared
    private final Vector2 m_localAnchorA = new Vector2();
    private final Vector2 m_localAnchorB = new Vector2();
    private float m_gamma;
    private float m_impulse;
    private float m_length;

    // Solver temp
    private int m_indexA;
    private int m_indexB;
    private final Vector2 m_u = new Vector2();
    private final Vector2 m_rA = new Vector2();
    private final Vector2 m_rB = new Vector2();
    private final Vector2 m_localCenterA = new Vector2();
    private final Vector2 m_localCenterB = new Vector2();
    private float m_invMassA;
    private float m_invMassB;
    private float m_invIA;
    private float m_invIB;
    private float m_mass;

    // positions
    private Vector2 cA = new Vector2();
    private float aA;
    private Vector2 cB = new Vector2();
    private float aB;

    // velocities
    private Vector2 vA = new Vector2();
    private float wA;
    private Vector2 vB = new Vector2();
    private float wB;

    public ConstraintDistance(final Body body_a, final Body body_b, Vector2 m_localAnchorA, Vector2 m_localAnchorB, float length, float frequencyHz, float dampingRatio) {
        super(body_a, body_b);
        this.m_localAnchorA.set(m_localAnchorA);
        this.m_localAnchorB.set(m_localAnchorB);
        m_length = length;
        m_impulse = 0.0f;
        m_frequencyHz = frequencyHz;
        m_dampingRatio = dampingRatio;
        m_gamma = 0.0f;
        m_bias = 0.0f;
    }

    @Override
    void prepare(float delta) {
        m_localCenterA.set(body1.lcmX, body1.lcmY);
        m_localCenterB.set(body2.lcmX, body2.lcmY);
        m_invMassA = body1.invM;
        m_invMassB = body2.invM;
        m_invIA = body1.invI;
        m_invIB = body2.invI;

        cA.set(body1.x, body1.y);
        aA = body1.aRad;
        vA.set(body1.vx, body1.vy);
        wA = body1.wRad;

        cB.set(body2.x, body2.y);
        aB = body2.aRad;
        vB.set(body2.vx, body2.vy);
        wB = body2.wRad;

        // use m_u as temporary variable
        m_u.set(m_localAnchorA).sub(m_localCenterA).rotateRad(body1.aRad);
        m_rA.set(m_u);
        m_u.set(m_localAnchorB).sub(m_localCenterB).rotateRad(body2.aRad);
        m_rB.set(m_u);
        m_u.set(body2.x, body2.y).add(m_rB).sub(body1.x, body1.y).sub(m_rA);


        // Handle singularity.
        float length = m_u.len();
        if (length > 0.005f) {
            m_u.x *= 1.0f / length;
            m_u.y *= 1.0f / length;
        } else {
            m_u.set(0.0f, 0.0f);
        }


        float crAu = Vector2.crs(m_rA, m_u);
        float crBu = Vector2.crs(m_rB, m_u);
        float invMass = m_invMassA + m_invIA * crAu * crAu + m_invMassB + m_invIB * crBu * crBu;

        // Compute the effective mass matrix.
        m_mass = invMass != 0.0f ? 1.0f / invMass : 0.0f;

        if (m_frequencyHz > 0.0f) {
            float C = length - m_length;

            // Frequency
            float omega = 2.0f * MathUtils.PI * m_frequencyHz;

            // Damping coefficient
            float d = 2.0f * m_mass * m_dampingRatio * omega;

            // Spring stiffness
            float k = m_mass * omega * omega;

            // magic formulas
            float h = delta;
            m_gamma = h * (d + h * k);
            m_gamma = m_gamma != 0.0f ? 1.0f / m_gamma : 0.0f;
            m_bias = C * h * k * m_gamma;

            invMass += m_gamma;
            m_mass = invMass != 0.0f ? 1.0f / invMass : 0.0f;
        } else {
            m_gamma = 0.0f;
            m_bias = 0.0f;
        }


        // Scale the impulse to support a variable time step.
        m_impulse *= 1;// TODO data.step.dtRatio;

        Vector2 P = new Vector2();
        P.set(m_u).scl(m_impulse);

        vA.x -= m_invMassA * P.x;
        vA.y -= m_invMassA * P.y;
        wA -= m_invIA * Vector2.crs(m_rA, P);

        vB.x += m_invMassB * P.x;
        vB.y += m_invMassB * P.y;
        wB += m_invIB * Vector2.crs(m_rB, P);

        body1.vx = vA.x;
        body1.vy = vA.y;
        body1.wRad = wA;

        body2.vx = vB.x;
        body2.vy = vB.y;
        body2.wRad = wB;
    }

    @Override
    void solveVelocity(float delta) {
        final Vector2 vpA = new Vector2();
        final Vector2 vpB = new Vector2();

        // Cdot = dot(u, v + cross(w, r))
        Vector2.crs(wA, m_rA, vpA);
        vpA.add(vA);
        Vector2.crs(wB, m_rB, vpB);
        vpB.add(vB);
        float Cdot = Vector2.dot(m_u, vpB.sub(vpA));

        float impulse = -m_mass * (Cdot + m_bias + m_gamma * m_impulse);
        m_impulse += impulse;


        float Px = impulse * m_u.x;
        float Py = impulse * m_u.y;

        vA.x -= m_invMassA * Px;
        vA.y -= m_invMassA * Py;
        wA -= m_invIA * (m_rA.x * Py - m_rA.y * Px);
        vB.x += m_invMassB * Px;
        vB.y += m_invMassB * Py;
        wB += m_invIB * (m_rB.x * Py - m_rB.y * Px);

        body1.vx = vA.x;
        body1.vy = vA.y;
        body1.wRad = wA;

        body2.vx = vB.x;
        body2.vy = vB.y;
        body2.wRad = wB;
    }

    @Override
    boolean solvePosition(float delta) {
        return true;
    }

}
