package org.example.engine.core.physics2d_new;

import org.example.engine.core.math.Vector2;

public final class Physics2DUtils {

    private Physics2DUtils() {}



    public static float calculateTotalMass(final BodyCollider...collider) {
        return 0;
    }

    public static Vector2 calculateCenterOfMass(final BodyCollider collider) {
        return collider.worldCenter();
    }

    public static Vector2 calculateCenterOfMass(final BodyCollider...colliders) {

        return null;
    }

    public static void setCenterOfMassToOrigin(final Body body) {

    }

    public static float calculateMomentOfInertia(final Body body) {

        return -1;
    }

}
