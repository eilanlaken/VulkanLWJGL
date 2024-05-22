package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;
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

        MathVector2 aCenter = shape_a.geometryCenter();
        MathVector2 bCenter = shape_b.geometryCenter();
        MathVector2 a_b     = new MathVector2(bCenter.x - aCenter.x, bCenter.y - aCenter.y);
        float dot           = a_b.dot(manifold.normal);
        if (dot < 0) manifold.normal.flip();

        final float aMassInv     = body_a.motionType == Physics2DBody.MotionType.STATIC ? 0 : body_a.massInv;
        final float bMassInv     = body_b.motionType == Physics2DBody.MotionType.STATIC ? 0 : body_b.massInv;
        final float percent      = 0.7f;
        final float threshold    = 0.001f;
        final float invMassTotal = aMassInv + bMassInv;
        final float pushBack     = Math.max(manifold.depth - threshold, 0.0f);
        MathVector2 correction   = new MathVector2(manifold.normal).scl(pushBack * percent).scl(1 / invMassTotal);

        shape_a.dx_dy(-aMassInv * correction.x, -aMassInv * correction.y);
        shape_b.dx_dy(bMassInv * correction.x, bMassInv * correction.y);

        // collision response
        final float e           = Math.min(body_a.restitution, body_b.restitution);
        final float aInertiaInv = body_a.motionType == Physics2DBody.MotionType.STATIC ? 0 : body_a.inertiaInv;
        final float bInertiaInv = body_b.motionType == Physics2DBody.MotionType.STATIC ? 0 : body_b.inertiaInv;

        CollectionsArray<MathVector2> contacts = new CollectionsArray<>();
        CollectionsArray<MathVector2> impulses = new CollectionsArray<>();
        CollectionsArray<MathVector2> ra_list  = new CollectionsArray<>();
        CollectionsArray<MathVector2> rb_list  = new CollectionsArray<>();

        if (manifold.contacts == 1) {
            contacts.add(manifold.contactPoint1);
        } else if (manifold.contacts == 2) {
            contacts.add(manifold.contactPoint1);
            contacts.add(manifold.contactPoint2);
        }

        for (int i = 0; i < manifold.contacts; i++) {
            impulses.add(new MathVector2());
            ra_list.add(new MathVector2());
            rb_list.add(new MathVector2());
        }

        for (int i = 0; i < manifold.contacts; i++) {
            ra_list.get(i).set(contacts.get(i).x - body_a.shape.x(), contacts.get(i).y - body_a.shape.y());
            rb_list.get(i).set(contacts.get(i).x - body_b.shape.x(), contacts.get(i).y - body_b.shape.y());

            MathVector2 raRot90 = new MathVector2(ra_list.get(i)).rotate90(1);
            MathVector2 rbRot90 = new MathVector2(rb_list.get(i)).rotate90(1);

            MathVector2 angularLinearVelA = new MathVector2(raRot90).scl(body_a.omegaDeg * MathUtils.degreesToRadians);
            MathVector2 angularLinearVelB = new MathVector2(rbRot90).scl(body_b.omegaDeg * MathUtils.degreesToRadians);

            MathVector2 relativeVelocity  = new MathVector2();
            relativeVelocity.add(body_b.velocity);
            relativeVelocity.add(angularLinearVelB);
            relativeVelocity.sub(body_a.velocity);
            relativeVelocity.sub(angularLinearVelA);

            float contactVelocityMag = MathVector2.dot(relativeVelocity, manifold.normal);
            if (contactVelocityMag > 0) continue;

            float raRot90DotN = MathVector2.dot(raRot90, manifold.normal);
            float rbRot90DotN = MathVector2.dot(rbRot90, manifold.normal);

            // TODO: calculate moment of inertia
            float d = body_a.massInv + body_b.massInv +
                    (raRot90DotN * raRot90DotN) * aInertiaInv +
                    (rbRot90DotN * rbRot90DotN) * bInertiaInv;

            float j = -(1f + e) * contactVelocityMag / (d * manifold.contacts);

            impulses.get(i).set(manifold.normal).scl(j);
        }

        for(int i = 0; i < manifold.contacts; i++)
        {
            MathVector2 impulse = impulses.get(i);
            MathVector2 ra = ra_list.get(i);
            MathVector2 rb = rb_list.get(i);

            MathVector2 deltaVelA = new MathVector2(impulse).scl(aMassInv);
            MathVector2 deltaVelB = new MathVector2(impulse).scl(bMassInv);

            body_a.velocity.sub(deltaVelA);
            body_b.velocity.add(deltaVelB);
            body_a.omegaDeg += -MathVector2.crs(ra, impulse) * aInertiaInv * MathUtils.radiansToDegrees;
            body_b.omegaDeg +=  MathVector2.crs(rb, impulse) * bInertiaInv * MathUtils.radiansToDegrees;
        }

    }

    protected void endContact(Physics2DWorld.CollisionManifold manifold) {

    }

}

/**
 *

 // collision response
 int contactCount = manifold.contacts;
 float e = Math.min(body_a.restitution, body_b.restitution);
 CollectionsArray<MathVector2> contacts = new CollectionsArray<>();
 if (contactCount == 1) {
 contacts.add(manifold.contactPoint1);
 } else if (contactCount == 2) {
 contacts.add(manifold.contactPoint1);
 contacts.add(manifold.contactPoint2);
 }


 MathVector2 vRel = new MathVector2(body_b.velocity).sub(body_a.velocity);
 float normalVelocity = vRel.dot(manifold.normal);
 if (normalVelocity > 0) return;



 float j = -(1 + e) * normalVelocity / (aMassInv + bMassInv);
 MathVector2 impulse = new MathVector2(manifold.normal).scl(j);
 MathVector2 deltaVelA = new MathVector2(impulse).scl(aMassInv);
 MathVector2 deltaVelB = new MathVector2(impulse).scl(bMassInv);
 body_a.velocity.sub(deltaVelA);
 body_b.velocity.add(deltaVelB);

 *
 */