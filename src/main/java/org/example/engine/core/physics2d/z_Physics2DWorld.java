package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.ArrayInt;
import org.example.engine.core.collections.TuplePair;
import org.example.engine.core.math.Shape2D;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.game.ScenePhysics2D_2;

// https://github.com/RandyGaul/ImpulseEngine/blob/master/Manifold.h
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-basics-and-impulse-resolution--gamedev-6331t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-core-engine--gamedev-7493t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-friction-scene-and-jump-table--gamedev-7756t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-oriented-rigid-bodies--gamedev-8032t
public class z_Physics2DWorld {

    public static final float MAX_BODY_SIZE = 100;
    public Cell[] spacialPartition = new Cell[4096];
    public Array<Cell> activeCells = new Array<>(false, 100);

    public Array<Shape2D> allShapes = new Array<>();
    public Array<Shape2D> toAdd = new Array<>();

    private ArrayInt indices = new ArrayInt(false, 10);

    private Array<TuplePair<Shape2D, Shape2D>> broadPhaseResult = new Array<>();


    public z_Physics2DWorld() {
        for (int i = 0; i < spacialPartition.length; i++) {
            spacialPartition[i] = new Cell();
        }
    }

    public void update(final float delta) {
        // todo: integrate - before or after collision resolution?

        for (Shape2D shape2D : toAdd) allShapes.add(shape2D);
        toAdd.clear();

        /* space partitioning */
        activeCells.clear();
        for (Shape2D shape2D : allShapes) {
            ArrayInt indices = getIndices(shape2D);
            for (int i = 0; i < indices.size; i++) {
                activeCells.add(spacialPartition[indices.items[i]]);
                spacialPartition[indices.items[i]].shapes.add(shape2D);
            }
        }

        /* broad phase */
        // TODO: make multithreaded
        // TODO: consider skipping static bodies
        for (Cell cell : activeCells) {
            // TODO: free pool-tuples
            cell.collisionCandidates.clear();

            for (int i = 0; i < cell.shapes.size - 1; i++) {
                for (int j = i + 1; j < cell.shapes.size; j++) {
                    Shape2D a = cell.shapes.items[i];
                    Shape2D b = cell.shapes.items[j];
                    boolean boundsCollide = Physics2DWorldCollisionDetection.boundingCirclesCollide(a, b);
                    // TODO: grab from pool
                    if (boundsCollide) cell.collisionCandidates.add(new TuplePair<>(a, b));
                }
            }
        }

        broadPhaseResult.clear();
        for (Cell cell : activeCells) {
            mergeBroadPhaseCollisionTestResults(cell);
        }

        /* narrow phase */

        /* resolution */

    }

    // TODO: check correctness and optimize.
    private void mergeBroadPhaseCollisionTestResults(Cell cell) {
        for (TuplePair<Shape2D, Shape2D> cellCandidates : cell.collisionCandidates) {
            for (TuplePair<Shape2D, Shape2D> candidates : broadPhaseResult) {
                if (candidates.first == cellCandidates.first && candidates.second == cellCandidates.second) break;
                if (candidates.first == cellCandidates.second && candidates.second == cellCandidates.first) break;
            }
            broadPhaseResult.add(cellCandidates);
        }
    }

    private ArrayInt getIndices(Shape2D shape) {
        indices.clear();
        float x = shape.x();
        float y = shape.y();
        int index = hash(x, y);
        indices.add(index);
        return indices;
    }

    public static int hash(float x, float y) {
        int i = (int) Math.floor(x / Cell.CELL_SIZE) % 64; // table size = 4096, 64 * 64
        if (i < 0) i += 64;
        int j = (int) Math.floor(y / Cell.CELL_SIZE) % 64;
        if (j < 0) j += 64;
        return i * 64 + j;
    }

    public void createBody(Shape2D shape) {
        toAdd.add(shape);
    }

    public void destroyBody() {

    }

    public void createJoint() {

    }

    public void destroyJoint() {

    }

    public static class Cell {

        public static final float CELL_SIZE = 10;
        Array<Shape2D> shapes = new Array<>();
        private Array<TuplePair<Shape2D, Shape2D>> collisionCandidates = new Array<>(false, 100);

    }

}
