package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.TuplePair;

public class Physics2DWorldCollisionBroadPhase {

    private Array<TuplePair<Physics2DBody, Physics2DBody>> candidates;
    private Array<Cell> spacePartition;
    private Array<Cell> activeCells;

    public void updateCells() {

    }

    public void performBroadPhase() {

    }

    private static class Cell {

        public static float CELL_SIZE = 2.0f;

        public Array<Physics2DBody> bodies = new Array<>();
        public final float x;
        public final float y;

        Cell(float x, float y) {
            this.x = x;
            this.y = y;
        }

    }

}
