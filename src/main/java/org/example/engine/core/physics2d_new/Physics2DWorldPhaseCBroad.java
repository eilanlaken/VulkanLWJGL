package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.shape.Shape2D;

public final class Physics2DWorldPhaseCBroad implements Physics2DWorldPhase {

    private final Cell[] partition  = new Cell[256]; // 16 x 16
    private       int    cellWidth  = 0;             // will be set dynamically
    private       int    cellHeight = 0;             // will be set dynamically

    @Override
    public void update(Physics2DWorld world, float delta) {
        CollectionsArray<Physics2DBody> collisionCandidates = world.collisionCandidates;

    }


    private int getPartitionIndex() {
        return 0;
    }

    // TODO: yank the code only.
    public static boolean broadPhaseCollision(final Shape2D a, final Shape2D b) {
        final float dx = b.x() - a.x();
        final float dy = b.y() - a.y();
        final float sum = a.getBoundingRadius() + b.getBoundingRadius();
        return dx * dx + dy * dy < sum * sum;
    }

    private static final class Cell {

        private CollectionsArray<Physics2DBody> bodies = new CollectionsArray<>();

    }

}
