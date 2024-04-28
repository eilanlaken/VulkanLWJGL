package org.example.engine.core.physics2d_new;

import org.example.engine.core.async.AsyncUtils;
import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.collections.CollectionsArrayConcurrent;
import org.example.engine.core.input.InputKeyboard;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape.Shape2D;

import java.util.HashSet;
import java.util.Set;

// this should be multithreaded
public final class Physics2DWorldPhaseCBroad implements Physics2DWorldPhase {

    private final MemoryPool<Cell> cellMemoryPool = new MemoryPool<>(Cell.class, 1024);
    private final int              processors     = AsyncUtils.getAvailableProcessorsNum();

    @Override
    public void update(Physics2DWorld world, float delta) {
        cellMemoryPool.freeAll(world.spacePartition);
        world.spacePartition.clear();
        if (world.allBodies.isEmpty()) return;
        //if (world.allBodies.size == 1) return; // TODO: put back.

        // data from previous phase
        final float minX = world.worldMinX;
        final float minY = world.worldMinY;
        final float cellWidth  = world.cellWidth;
        final float cellHeight = world.cellHeight;
        final int rows = world.rows;
        final int cols = world.cols;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = cellMemoryPool.allocate();
                cell.x = minX + (j + 0.5f) * cellWidth;
                cell.y = minY + (i + 0.5f) * cellHeight;
                world.spacePartition.add(cell);
            }
        }

        for (Physics2DBody body : world.allBodies) {
            int startCol = Math.max(0, (int) ((body.shape.getMinExtentX() - minX) / cellWidth));
            int endCol   = Math.min(cols - 1, (int) ((body.shape.getMaxExtentX() - minX) / cellWidth));
            int startRow = Math.max(0, (int) ((body.shape.getMinExtentY() - minY) / cellHeight));
            int endRow   = Math.min(rows - 1, (int) ((body.shape.getMaxExtentY() - minY) / cellHeight));

            for (int row = startRow; row <= endRow; row++) {
                for (int col = startCol; col <= endCol; col++) {
                    int cellIndex = row * cols + col;
                    Cell cell = world.spacePartition.get(cellIndex);
                    cell.bodies.add(body);
                    world.activeCells.add(cell);
                }
            }
        }

        // run broad phase. Use async tasks.
        // TODO

        // merge all collision candidates.
        CollectionsArray<Physics2DBody> collisionCandidates = world.collisionCandidates;

    }

    public static final class Cell implements MemoryPool.Reset {

        private final CollectionsArrayConcurrent<Physics2DBody> bodies     = new CollectionsArrayConcurrent<>(false, 2);
        private final CollectionsArrayConcurrent<Physics2DBody> candidates = new CollectionsArrayConcurrent<>(false, 2);

        float x;
        float y;

        public Cell() {}

        private boolean areBoundingCirclesCollide(final Shape2D a, final Shape2D b) {
            final float dx  = b.x() - a.x();
            final float dy  = b.y() - a.y();
            final float sum = a.getBoundingRadius() + b.getBoundingRadius();
            return dx * dx + dy * dy < sum * sum;
        }

        @Override
        public void reset() {
            bodies.clear();
            candidates.clear();
        }

    }

}


//TODO: remove
/*
int min_i_index  = (int) Math.floor((max_body_y - maxY) / cellHeight);
            int max_i_index  = (int) Math.ceil((min_body_y - maxY) / cellHeight);
            int min_j_index  = (int) Math.floor((min_body_x - minX) / cellWidth);
            int max_j_index  = (int) Math.ceil((max_body_x - minX) / cellWidth);
            int i = min_i_index;
            while (i < max_i_index) {
                int j = min_j_index;
                while (j < max_j_index) {
                    Cell cell = spacePartition.get(i * cols + j);
                    cell.bodies.add(body);
                    cell.x = minX + j * cellWidth;
                    cell.y = maxY - i * cellHeight;
                    world.activeCells.add(cell);
                    j += cellWidth;
                }
                i += cellHeight;
            }


 */

// TODO: remove
/**
 *
 // build partition
 for (Physics2DBody body : world.allBodies) {
 float min_body_x = body.shape.getMinExtentX();
 float max_body_x = body.shape.getMaxExtentX();
 float min_body_y = body.shape.getMinExtentY();
 float max_body_y = body.shape.getMaxExtentY();
 int min_i_index  = (int) Math.floor((min_body_y - minY) / cellHeight);
 int max_i_index  = (int) Math.floor((max_body_y - minY) / cellHeight);
 int min_j_index  = (int) Math.floor((min_body_x - minX) / cellWidth);
 int max_j_index  = (int) Math.floor((max_body_x - minX) / cellWidth);
 int i = min_i_index;
 while (i < max_i_index) {
 int j = min_j_index;
 while (j < max_j_index) {
 //Cell cell = partition[i * cellCount + j];
 //cell.bodies.add(body);
 //activeCells.add(cell);
 j++;
 }
 i++;
 }
 }


 */