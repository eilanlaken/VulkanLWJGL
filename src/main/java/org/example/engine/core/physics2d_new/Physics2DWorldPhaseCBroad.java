package org.example.engine.core.physics2d_new;

import org.example.engine.core.async.AsyncUtils;
import org.example.engine.core.collections.CollectionsArray;
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

    Physics2DWorldPhaseCBroad() {
        for (int i = 0; i < partition.length; i++) {
            this.partition[i] = new Cell();
        }
    }

    @Override
    public void update(Physics2DWorld world, float delta) {
        for (Cell cell : activeCells) {
            cell.bodies.clear();
            cell.collisionCandidates.clear();
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
        final float world_width  = Math.abs(maxX - minX);
        final float world_height = Math.abs(maxY - minY);
        final float cellWidth    = world_width  / PARTITION_SIDE;
        final float cellHeight   = world_height / PARTITION_SIDE;
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

    // TODO: yank the code only.
    public static boolean broadPhaseCollision(final Shape2D a, final Shape2D b) {
        final float dx = b.x() - a.x();
        final float dy = b.y() - a.y();
        final float sum = a.getBoundingRadius() + b.getBoundingRadius();
        return dx * dx + dy * dy < sum * sum;
    }

    private static final class Cell {

        private CollectionsArray<Physics2DBody> bodies = new CollectionsArray<>(false, 2);
        private CollectionsArray<Physics2DBody> collisionCandidates = new CollectionsArray<>(false, 2);

    }

}
