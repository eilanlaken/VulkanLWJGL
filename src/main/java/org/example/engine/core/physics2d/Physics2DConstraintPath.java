package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathVector2;

public class Physics2DConstraintPath implements Physics2DConstraint {

    public final Physics2DBody body;
    public final float[]       pathPoints;
    public final boolean       bezier;

    Physics2DConstraintPath(Physics2DBody body, final float[] pathPoints, boolean bezier) {
        this.body = body;
        this.pathPoints = pathPoints;
        this.bezier = bezier;
    }

    @Override
    public void getBodies(CollectionsArray<Physics2DBody> out) {
        out.clear();
        out.add(body);
    }

    @Override
    public void update(float delta) {

    }

}
