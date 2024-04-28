package org.example.engine.core.physics2d_new;

import org.example.engine.core.async.AsyncUtils;
import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.collections.CollectionsArrayConcurrent;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape.Shape2D;

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

        // TODO: run broad phase. Use async tasks for multithreading
        for (Cell cell : world.activeCells) {
            for (int i = 0; i < cell.bodies.size - 1; i++) {
                for (int j = i + 1; j < cell.bodies.size; j++) {
                    Physics2DBody a = cell.bodies.get(i);
                    Physics2DBody b = cell.bodies.get(j);
                    if (a.off) continue;
                    if (b.off) continue;
                    if (a.motionType == Physics2DBody.MotionType.FIXED && b.motionType == Physics2DBody.MotionType.FIXED) continue;
                    if (cell.boundingCirclesCollide(a.shape, b.shape)) cell.candidates.add(a, b);
                }
            }
        }

        // merge all collision candidates.
        for (Cell cell : world.activeCells) {
            for (Physics2DBody body : cell.candidates) {
                world.collisionCandidates.add(body);
            }
        }

        System.out.println(world.collisionCandidates.size);
    }

    public static final class Cell implements MemoryPool.Reset {

        private final CollectionsArrayConcurrent<Physics2DBody> bodies     = new CollectionsArrayConcurrent<>(false, 2);
        private final CollectionsArrayConcurrent<Physics2DBody> candidates = new CollectionsArrayConcurrent<>(false, 2);

        float x;
        float y;

        public Cell() {}

        private boolean boundingCirclesCollide(final Shape2D a, final Shape2D b) {
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