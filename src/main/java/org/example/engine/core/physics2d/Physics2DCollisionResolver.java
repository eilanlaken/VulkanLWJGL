package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.ArrayFloat;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.shape.Shape2D;

public class Physics2DCollisionResolver {

    protected void beginContact(Physics2DWorld.CollisionManifold manifold) {

    }

    protected void resolve(Physics2DWorld.CollisionManifold manifold) {
        // separate bodies
        Physics2DBody body_a = manifold.body_a;
        Physics2DBody body_b = manifold.body_b;
        Shape2D shape_a      = manifold.shape_a;
        Shape2D shape_b      = manifold.shape_b;

        Vector2 aCenter = shape_a.geometryCenter();
        Vector2 bCenter = shape_b.geometryCenter();
        Vector2 a_b     = new Vector2(bCenter.x - aCenter.x, bCenter.y - aCenter.y);

        if (a_b.dot(manifold.normal) < 0) manifold.normal.flip();

        final float aMassInv   = body_a.motionType == Physics2DBody.MotionType.STATIC ? 0 : body_a.massInv;
        final float bMassInv   = body_b.motionType == Physics2DBody.MotionType.STATIC ? 0 : body_b.massInv;
        final float percent    = 0.7f;
        final float threshold  = 0.001f;
        final float invMassSum = aMassInv + bMassInv;
        final float pushBack   = Math.max(manifold.depth - threshold, 0.0f);

        Vector2 correction = new Vector2(manifold.normal).scl(pushBack * percent).scl(1 / invMassSum);
        shape_a.dx_dy(-aMassInv * correction.x, -aMassInv * correction.y);
        shape_b.dx_dy(bMassInv  * correction.x, bMassInv  * correction.y);

        // collision response
        final float e           = Math.min(body_a.restitution, body_b.restitution);
        final float sf          = (body_a.staticFriction + body_b.staticFriction) * 0.5f;
        final float df          = (body_a.dynamicFriction + body_b.dynamicFriction) * 0.5f;
        final float aInertiaInv = body_a.motionType == Physics2DBody.MotionType.STATIC ? 0 : body_a.inertiaInv;
        final float bInertiaInv = body_b.motionType == Physics2DBody.MotionType.STATIC ? 0 : body_b.inertiaInv;

        Array<Vector2> contacts  = new Array<>();
        Array<Vector2> impulses  = new Array<>();
        Array<Vector2> frictions = new Array<>();
        Array<Vector2> ra_list   = new Array<>();
        Array<Vector2> rb_list   = new Array<>();
        ArrayFloat j_list    = new ArrayFloat();

        if (manifold.contacts == 1) {
            contacts.add(manifold.contactPoint1);
        } else if (manifold.contacts == 2) {
            contacts.add(manifold.contactPoint1);
            contacts.add(manifold.contactPoint2);
        }

        for (int i = 0; i < manifold.contacts; i++) {
            impulses.add(new Vector2());
            frictions.add(new Vector2());
            ra_list.add(new Vector2());
            rb_list.add(new Vector2());
            j_list.add(0);
        }

        for (int i = 0; i < manifold.contacts; i++) {
            ra_list.get(i).set(contacts.get(i).x - body_a.shape.x(), contacts.get(i).y - body_a.shape.y());
            rb_list.get(i).set(contacts.get(i).x - body_b.shape.x(), contacts.get(i).y - body_b.shape.y());

            Vector2 raRot90 = new Vector2(ra_list.get(i)).rotate90(1);
            Vector2 rbRot90 = new Vector2(rb_list.get(i)).rotate90(1);

            Vector2 angularLinearVelA = new Vector2(raRot90).scl(body_a.omegaDeg * MathUtils.degreesToRadians);
            Vector2 angularLinearVelB = new Vector2(rbRot90).scl(body_b.omegaDeg * MathUtils.degreesToRadians);

            Vector2 relativeVelocity = new Vector2();
            relativeVelocity.add(body_b.velocity).add(angularLinearVelB);
            relativeVelocity.sub(body_a.velocity).sub(angularLinearVelA);

            float contactVelocityMag = Vector2.dot(relativeVelocity, manifold.normal);

            if (contactVelocityMag < 0) {
                float raRot90DotN = Vector2.dot(raRot90, manifold.normal);
                float rbRot90DotN = Vector2.dot(rbRot90, manifold.normal);
                float d = invMassSum + (raRot90DotN * raRot90DotN) * aInertiaInv + (rbRot90DotN * rbRot90DotN) * bInertiaInv;
                float j = -(1f + e) * contactVelocityMag / (d * manifold.contacts);
                j_list.set(i, j);
                impulses.get(i).set(manifold.normal).scl(j);
            }

            Vector2 tangent = new Vector2(relativeVelocity); // tangent = v_rel - <v_rel, normal> * normal
            Vector2 v = new Vector2(manifold.normal).scl(Vector2.dot(relativeVelocity, manifold.normal));
            tangent.sub(v);

            if (!Vector2.nearlyEqual(tangent, Vector2.Zero, 0.0005f)) {
                tangent.nor();
                float raRot90DotT = Vector2.dot(raRot90, tangent);
                float rbRot90DotT = Vector2.dot(rbRot90, tangent);
                float d = invMassSum + (raRot90DotT * raRot90DotT) * body_a.inertiaInv + (rbRot90DotT * rbRot90DotT) * body_b.inertiaInv;
                float jt = -Vector2.dot(relativeVelocity, tangent);
                jt /= d;
                jt /= (float) manifold.contacts;
                Vector2 frictionImpulse = new Vector2(tangent);
                float j = j_list.get(i);
                if (Math.abs(jt) <= j * sf) frictionImpulse.scl(jt);
                else frictionImpulse.scl(-j * df);
                frictions.get(i).set(frictionImpulse);
            }
        }

        for (int i = 0; i < manifold.contacts; i++) {
            Vector2 impulse = impulses.get(i);
            Vector2 ra = ra_list.get(i);
            Vector2 rb = rb_list.get(i);

            Vector2 deltaVelA = new Vector2(impulse).scl(aMassInv);
            Vector2 deltaVelB = new Vector2(impulse).scl(bMassInv);

            body_a.velocity.sub(deltaVelA);
            body_b.velocity.add(deltaVelB);

            body_a.omegaDeg += -Vector2.crs(ra, impulse) * aInertiaInv * MathUtils.radiansToDegrees;
            body_b.omegaDeg +=  Vector2.crs(rb, impulse) * bInertiaInv * MathUtils.radiansToDegrees;
        }

        for (int i = 0; i < manifold.contacts; i++) {
            Vector2 impulse = frictions.get(i);
            Vector2 ra = ra_list.get(i);
            Vector2 rb = rb_list.get(i);

            Vector2 deltaVelA = new Vector2(impulse).scl(aMassInv);
            Vector2 deltaVelB = new Vector2(impulse).scl(bMassInv);

            body_a.velocity.sub(deltaVelA);
            body_b.velocity.add(deltaVelB);

            body_a.omegaDeg += -Vector2.crs(ra, impulse) * aInertiaInv * MathUtils.radiansToDegrees;
            body_b.omegaDeg +=  Vector2.crs(rb, impulse) * bInertiaInv * MathUtils.radiansToDegrees;
        }

    }

    protected void endContact(Physics2DWorld.CollisionManifold manifold) {

    }

}
