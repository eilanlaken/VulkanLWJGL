package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.TuplePair;
import org.example.engine.core.math.Shape2D;

public class Physics2DWorldCollisionPhaseBroad {

    private Array<TuplePair<Physics2DBody, Physics2DBody>> candidates; // update the candidates every frame.
    private static final int GRID_EXTENT = 64;
    private Cell[] partition = new Cell[64 * 64]; // 4096
    private Array<Cell> activeCells = new Array<>(false, 4096);

    protected Physics2DWorldCollisionPhaseBroad() {
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                final int index = i * 64 + j;
                partition[index] = new Cell(index);
            }
        }
    }

    public void insertBodyToGridCells(Shape2D shape) {
        float minXExtent = shape.x() - shape.getBoundingRadius();
        float maxXExtent = shape.x() + shape.getBoundingRadius();
        float minYExtent = shape.y() - shape.getBoundingRadius();
        float maxYExtent = shape.y() + shape.getBoundingRadius();
        // calculate the min and max indices

    }

    public void updateCells() {

    }

    public void performBroadPhase(Array<Physics2DBody> allBodies) {

    }

    private static class Cell {

        public static float CELL_SIZE = 2.0f;

        public Array<Physics2DBody> bodies = new Array<>();
        public final int index;

        Cell(int index) {
            this.index = index;
        }

        public static int hash(float x, float y) {
            int i = (int) Math.floor(x / CELL_SIZE) % GRID_EXTENT;
            if (i < 0) i += GRID_EXTENT;
            int j = (int) Math.floor(y / CELL_SIZE) % GRID_EXTENT;
            if (j < 0) j += GRID_EXTENT;
            return i * 64 + j;
        }

    }

}
