package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.ArrayFloat;
import org.example.engine.core.math.Vector2;

public class CollisionSolver {

    protected void beginContact(CollisionManifold manifold) {

    }

    protected void resolve(CollisionManifold manifold) {
        // separate bodies
        BodyCollider collider_a = manifold.collider_a;
        BodyCollider collider_b = manifold.collider_b;
        Body body_a = collider_a.body;
        Body body_b = collider_b.body;

        Vector2 aCenter = collider_a.worldCenter;
        Vector2 bCenter = collider_b.worldCenter;
        Vector2 a_b     = new Vector2(bCenter.x - aCenter.x, bCenter.y - aCenter.y);

        if (a_b.dot(manifold.normal) < 0) manifold.normal.flip();

        final float aMassInv   = body_a.motionType == Body.MotionType.STATIC ? 0 : body_a.invM;
        final float bMassInv   = body_b.motionType == Body.MotionType.STATIC ? 0 : body_b.invM;
        final float percent    = 0.7f;
        final float threshold  = 0.005f;
        final float invMassSum = aMassInv + bMassInv;
        final float pushBack   = Math.max(manifold.depth - threshold, 0.0f);

        Vector2 correction = new Vector2(manifold.normal).scl(pushBack * percent).scl(1 / invMassSum);
        body_a.x += -aMassInv * correction.x;
        body_a.y += -aMassInv * correction.y;
        body_b.x +=  bMassInv * correction.x;
        body_b.y +=  bMassInv * correction.y;

        // collision response
        final float e           = Math.min(collider_a.restitution, collider_b.restitution);
        final float sf          = (collider_a.staticFriction + collider_b.staticFriction) * 0.5f;
        final float df          = (collider_a.dynamicFriction + collider_b.dynamicFriction) * 0.5f;
        final float aInertiaInv = body_a.motionType == Body.MotionType.STATIC ? 0 : body_a.invI;
        final float bInertiaInv = body_b.motionType == Body.MotionType.STATIC ? 0 : body_b.invI;

        Array<Vector2> contacts  = new Array<>();
        Array<Vector2> impulses  = new Array<>();
        Array<Vector2> frictions = new Array<>();
        Array<Vector2> ra_list   = new Array<>();
        Array<Vector2> rb_list   = new Array<>();
        ArrayFloat     j_list    = new ArrayFloat();

        if (manifold.contacts == 1) {
            contacts.add(manifold.contact_a);
        } else if (manifold.contacts == 2) {
            contacts.add(manifold.contact_a);
            contacts.add(manifold.contact_b);
        }

        for (int i = 0; i < manifold.contacts; i++) {
            impulses.add(new Vector2());
            frictions.add(new Vector2());
            ra_list.add(new Vector2());
            rb_list.add(new Vector2());
            j_list.add(0);
        }

        for (int i = 0; i < manifold.contacts; i++) {
            ra_list.get(i).set(contacts.get(i).x - body_a.cmX, contacts.get(i).y - body_a.cmY);
            rb_list.get(i).set(contacts.get(i).x - body_b.cmX, contacts.get(i).y - body_b.cmY);

            Vector2 raRot90 = new Vector2(ra_list.get(i)).rotate90(1);
            Vector2 rbRot90 = new Vector2(rb_list.get(i)).rotate90(1);

            Vector2 angularLinearVelA = new Vector2(raRot90).scl(body_a.wRad);
            Vector2 angularLinearVelB = new Vector2(rbRot90).scl(body_b.wRad);

            Vector2 relativeVelocity = new Vector2();
            relativeVelocity.add(body_b.vx, body_b.vy).add(angularLinearVelB);
            relativeVelocity.sub(body_a.vx, body_a.vy).sub(angularLinearVelA);

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
                float d = invMassSum + (raRot90DotT * raRot90DotT) * body_a.invI + (rbRot90DotT * rbRot90DotT) * body_b.invI;
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

            body_a.vx -= deltaVelA.x;
            body_a.vy -= deltaVelA.y;

            body_b.vx += deltaVelB.x;
            body_b.vy += deltaVelB.y;

            body_a.wRad += -Vector2.crs(ra, impulse) * aInertiaInv;
            body_b.wRad +=  Vector2.crs(rb, impulse) * bInertiaInv;
        }

        for (int i = 0; i < manifold.contacts; i++) {
            Vector2 impulse = frictions.get(i);
            Vector2 ra = ra_list.get(i);
            Vector2 rb = rb_list.get(i);

            Vector2 deltaVelA = new Vector2(impulse).scl(aMassInv);
            Vector2 deltaVelB = new Vector2(impulse).scl(bMassInv);

            body_a.vx -= deltaVelA.x;
            body_a.vy -= deltaVelA.y;

            body_b.vx += deltaVelB.x;
            body_b.vy += deltaVelB.y;

            body_a.wRad += -Vector2.crs(ra, impulse) * aInertiaInv;
            body_b.wRad +=  Vector2.crs(rb, impulse) * bInertiaInv;
        }

    }

    protected void endContact(CollisionManifold manifold) {

    }

}
