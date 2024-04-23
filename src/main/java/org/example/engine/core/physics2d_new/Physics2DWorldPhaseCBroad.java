package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.shape.Shape2D;

public final class Physics2DWorldPhaseCBroad implements Physics2DWorldPhase {

    private final Physics2DWorld world;

    Physics2DWorldPhaseCBroad(final Physics2DWorld world) {
        this.world = world;
    }

    @Override
    public void update(Physics2DWorld world, float delta) {

    }


    // TODO: yank the code only.
    public static boolean broadPhaseCollision(final Shape2D a, final Shape2D b) {
        final float dx = b.x() - a.x();
        final float dy = b.y() - a.y();
        final float sum = a.getBoundingRadius() + b.getBoundingRadius();
        return dx * dx + dy * dy < sum * sum;
    }

    private static final class Cell {

        private float size;
        private CollectionsArray<Physics2DBody> bodies = new CollectionsArray<>();

    }

}
