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

    private static final int PARTITION_SIDE = 32;
    private static final int PARTITION_SIZE = PARTITION_SIDE * PARTITION_SIDE; // 1024

    private final Cell[]     partition   = new Cell[PARTITION_SIZE]; // 1024
    private final Set<Cell>  activeCells = new HashSet<>();
    private final int        processors  = AsyncUtils.getAvailableProcessors();

    protected float worldWidth  = 0;
    protected float worldHeight = 0;
    protected float cellWidth   = 0;
    protected float cellHeight  = 0;

    Physics2DWorldPhaseCBroad() {
        for (int i = 0; i < partition.length; i++) {
            this.partition[i] = new Cell();
        }
    }

    @Override
    public void update(Physics2DWorld world, float delta) {
        for (Cell cell : activeCells) {
            cell.bodies.clear();
            cell.candidates.clear();
        }
        activeCells.clear();
        // calculate world x and y extents
        float minX =  Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float minY =  Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        for (Physics2DBody body : world.allBodies) {
            float min_body_x = body.shape.getMinExtentX();
            float max_body_x = body.shape.getMaxExtentX();
            float min_body_y = body.shape.getMinExtentY();
            float max_body_y = body.shape.getMaxExtentY();
            minX = Math.min(minX, min_body_x);
            maxX = Math.max(maxX, max_body_x);
            minY = Math.min(minY, min_body_y);
            maxY = Math.max(maxY, max_body_y);
        }
        // calculate the cell size of the grid partition
        worldWidth  = Math.abs(maxX - minX);
        worldHeight = Math.abs(maxY - minY);
        cellWidth   = worldWidth  / PARTITION_SIDE;
        cellHeight  = worldHeight / PARTITION_SIDE;
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
                    Cell cell = partition[i * PARTITION_SIDE + j];
                    cell.bodies.add(body);
                    activeCells.add(cell);
                    j++;
                }
                i++;
            }
        }
        // run broad phase. use async tasks.

        // merge all collision candidates.
        CollectionsArray<Physics2DBody> collisionCandidates = world.collisionCandidates;

    }

    protected static final class Cell implements MemoryPool.Reset {

        private int index = -1;
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
