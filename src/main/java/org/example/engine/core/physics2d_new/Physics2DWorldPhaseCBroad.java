package org.example.engine.core.physics2d_new;

import org.example.engine.core.async.AsyncUtils;
import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.collections.CollectionsArrayConcurrent;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape.Shape2D;

import java.util.HashSet;
import java.util.Set;

// this should be multithreaded
public final class Physics2DWorldPhaseCBroad implements Physics2DWorldPhase {

    private final MemoryPool<Cell> cellPool    = new MemoryPool<>(Cell.class, 1024);
    private final Set<Cell>        activeCells = new HashSet<>();
    private final int              processors  = AsyncUtils.getAvailableProcessorsNum();

    float worldWidth    = 0;
    float worldHeight   = 0;
    int horizontalCells = 0;
    int verticalCells   = 0;
    float cellWidth     = 0;
    float cellHeight    = 0;

    Physics2DWorldPhaseCBroad() {

    }

    @Override
    public void update(Physics2DWorld world, float delta) {
        cellPool.freeAll(activeCells);
        activeCells.clear();
        if (world.allBodies.isEmpty()) return;
        if (world.allBodies.size == 1) return;
        // calculate world x and y extents
        float minX =  Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float minY =  Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        float maxR = -Float.MAX_VALUE;
        for (Physics2DBody body : world.allBodies) {
            float min_body_x = body.shape.getMinExtentX();
            float max_body_x = body.shape.getMaxExtentX();
            float min_body_y = body.shape.getMinExtentY();
            float max_body_y = body.shape.getMaxExtentY();
            float bounding_r = body.shape.getBoundingRadius();
            minX = Math.min(minX, min_body_x);
            maxX = Math.max(maxX, max_body_x);
            minY = Math.min(minY, min_body_y);
            maxY = Math.max(maxY, max_body_y);
            maxR = Math.max(maxR, bounding_r);
        }
        // calculate the cell size of the grid partition
        worldWidth  = Math.abs(maxX - minX);
        worldHeight = Math.abs(maxY - minY);
        float maxDiameter = 2 * maxR;
        horizontalCells = Math.min((int) Math.ceil(worldWidth  / maxDiameter), 32);
        verticalCells   = Math.min((int) Math.ceil(worldHeight / maxDiameter), 32);
        cellWidth = worldWidth / horizontalCells;
        cellHeight = worldHeight / verticalCells;
        // run broad phase. use async tasks.

        // merge all collision candidates.
        CollectionsArray<Physics2DBody> collisionCandidates = world.collisionCandidates;

    }

    protected static final class Cell implements MemoryPool.Reset {

        private int   index = -1;
        private final CollectionsArrayConcurrent<Physics2DBody> bodies     = new CollectionsArrayConcurrent<>(false, 2);
        private final CollectionsArrayConcurrent<Physics2DBody> candidates = new CollectionsArrayConcurrent<>(false, 2);

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
            index = -1;
        }
    }

}

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