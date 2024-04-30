package org.example.engine.core.physics2d_new;

import org.example.engine.core.async.AsyncTask;
import org.example.engine.core.async.AsyncTaskRunner;
import org.example.engine.core.async.AsyncUtils;
import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape.Shape2D;

// this should be multithreaded
public final class Physics2DWorldPhaseCBroad implements Physics2DWorldPhase {

    private final int                      processors     = AsyncUtils.getAvailableProcessorsNum();
    private final MemoryPool<Cell>         cellMemoryPool = new MemoryPool<>(Cell.class, 1024);
    private final MemoryPool<ProcessCells> taskMemoryPool = new MemoryPool<>(ProcessCells.class, processors);

    private final CollectionsArray<ProcessCells> broadPhaseTasks = new CollectionsArray<>();

    @Override
    public void update(Physics2DWorld world, float delta) {
        taskMemoryPool.freeAll(broadPhaseTasks);
        broadPhaseTasks.clear();
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
                    if (!cell.active) {
                        world.activeCells.add(cell);
                        cell.active = true;
                    }
                }
            }
        }

        for (int i = 0; i < processors; i++) {
            ProcessCells task = taskMemoryPool.allocate();
            broadPhaseTasks.add(task);
        }

        for (int i = 0; i < world.activeCells.size; i++) {
            broadPhaseTasks.getCircular(i).cellsToProcess.add(world.activeCells.get(i));
        }

        AsyncTaskRunner.await(AsyncTaskRunner.async(broadPhaseTasks));

        // merge all collision candidates.
        // TODO: merge correctly. Don't merge existing pairs or their symmetries.
        for (Cell cell : world.activeCells) {
            for (Physics2DBody body : cell.candidates) {
                world.collisionCandidates.add(body);
            }
        }

        System.out.println(world.collisionCandidates.size);
    }

    public static final class ProcessCells extends AsyncTask {

        private final CollectionsArray<Cell> cellsToProcess = new CollectionsArray<>();

        public ProcessCells() {}

        @Override
        public void task() {
            for (Cell cell : cellsToProcess) {
                for (int i = 0; i < cell.bodies.size - 1; i++) {
                    for (int j = i + 1; j < cell.bodies.size; j++) {
                        Physics2DBody a = cell.bodies.get(i);
                        Physics2DBody b = cell.bodies.get(j);
                        if (a.off) continue;
                        if (b.off) continue;
                        if (a.motionType == Physics2DBody.MotionType.STATIC && b.motionType == Physics2DBody.MotionType.STATIC) continue;
                        if (boundingCirclesCollide(a.shape, b.shape)) cell.candidates.add(a, b);
                    }
                }
            }
        }

        private boolean boundingCirclesCollide(final Shape2D a, final Shape2D b) {
            final float dx  = b.x() - a.x();
            final float dy  = b.y() - a.y();
            final float sum = a.getBoundingRadius() + b.getBoundingRadius();
            return dx * dx + dy * dy < sum * sum;
        }

        @Override
        public void reset() {
            super.reset();
            cellsToProcess.clear();
        }

    }

    public static final class Cell implements MemoryPool.Reset {

        private final CollectionsArray<Physics2DBody> candidates = new CollectionsArray<>(false, 2);
        private final CollectionsArray<Physics2DBody> bodies     = new CollectionsArray<>(false, 2);

        private boolean active = false;

        // TODO: remove, just for debug rendering
        float x;
        float y;

        public Cell() {}

        @Override
        public void reset() {
            candidates.clear();
            bodies.clear();
            active = false;
        }

    }

}