package org.example.engine.core.physics2d_new;

import org.example.engine.core.async.AsyncTask;
import org.example.engine.core.async.AsyncUtils;
import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.graphics.GraphicsRenderer2D;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.physics2d.Physics2DWorldPhaseC;
import org.example.engine.core.shape.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// https://github.com/RandyGaul/ImpulseEngine/blob/master/Manifold.h
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-basics-and-impulse-resolution--gamedev-6331t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-core-engine--gamedev-7493t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-friction-scene-and-jump-table--gamedev-7756t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-oriented-rigid-bodies--gamedev-8032t
public class Physics2DWorld {

    // bodies, joints, constraints and manifolds
    MemoryPool<Physics2DBody>       bodyMemoryPool     = new MemoryPool<>(Physics2DBody.class,     10);
    MemoryPool<CollisionManifold>   manifoldMemoryPool = new MemoryPool<>(CollisionManifold.class, 10);
    //private MemoryPool<CollisionPair>       pairMemoryPool     = new MemoryPool<>(CollisionPair.class, 5);
    public  CollectionsArray<Physics2DBody> allBodies          = new CollectionsArray<>(false, 500);
    public  CollectionsArray<Physics2DBody> bodiesToAdd        = new CollectionsArray<>(false, 100);
    public  CollectionsArray<Physics2DBody> bodiesToRemove     = new CollectionsArray<>(false, 500);

    // multi-threading
    private final int                              processors     = AsyncUtils.getAvailableProcessorsNumber();
    private final MemoryPool<CellsProcessor>       taskMemoryPool = new MemoryPool<>(CellsProcessor.class, processors);
    private final ExecutorService                  executor       = Executors.newFixedThreadPool(processors);
    private final CollectionsArray<CellsProcessor> tasks          = new CollectionsArray<>();


    protected final CollectionsArray<Cell> spacePartition = new CollectionsArray<>(false, 1024);
    protected final CollectionsArray<Cell> activeCells    = new CollectionsArray<>();
    private   final MemoryPool<Cell>       cellMemoryPool = new MemoryPool<>(Cell.class,1024);
    protected int   bodiesCreated = 0;

    protected final Set<CollisionPair>                  collisionCandidates = new HashSet<>();
    private   final Physics2DCollisionDetection         collisionDetection  = new Physics2DCollisionDetection(this);
    protected final Physics2DWorldRenderer              debugRenderer       = new Physics2DWorldRenderer(this);
    protected final CollectionsArray<CollisionManifold> manifolds           = new CollectionsArray<>(false, 20);
    protected       Physics2DCollisionResolver          collisionResolver;

    // debugger options
    public boolean renderManifolds  = true;
    public boolean renderVelocities = false;
    public boolean renderBodies     = true;

    public Physics2DWorld(Physics2DCollisionResolver collisionResolver) {
        this.collisionResolver = collisionResolver != null ? collisionResolver : new Physics2DCollisionResolver() {};
    }

    public Physics2DWorld() {
        this(null);
    }

    public void update(final float delta) {
        /* preparation: add and remove bodies */

        for (Physics2DBody body : bodiesToRemove) {
            allBodies.removeValue(body, true);
            bodyMemoryPool.free(body);
        }
        for (Physics2DBody body : bodiesToAdd) {
            allBodies.add(body);
            body.created = true;
            body.index = bodiesCreated;
            bodiesCreated++;
        }
        bodiesToRemove.clear();
        bodiesToAdd.clear();

        /* integration: update velocities, clear forces and move bodies. */

        float worldMinX = Float.POSITIVE_INFINITY;
        float worldMaxX = Float.NEGATIVE_INFINITY;
        float worldMinY = Float.POSITIVE_INFINITY;
        float worldMaxY = Float.NEGATIVE_INFINITY;
        float worldMaxR = Float.NEGATIVE_INFINITY;

        for (Physics2DBody body : allBodies) {
            if (body.off) continue;
            if (body.motionType == Physics2DBody.MotionType.NEWTONIAN) {
                body.velocity.add(body.massInv * delta * body.netForce.x, body.massInv * delta * body.netForce.y);
                body.omega += body.netTorque * (body.inertiaInv) * delta;
            }
            if (body.motionType != Physics2DBody.MotionType.STATIC) {
                body.shape.dx_dy_rot(delta * body.velocity.x, delta * body.velocity.y, delta * body.omega);
            }
            body.shape.update();
            body.netForce.set(0, 0);
            body.netTorque = 0;
            body.collidesWith.clear();

            worldMinX = Math.min(worldMinX, body.shape.getMinExtentX());
            worldMaxX = Math.max(worldMaxX, body.shape.getMaxExtentX());
            worldMinY = Math.min(worldMinY, body.shape.getMinExtentY());
            worldMaxY = Math.max(worldMaxY, body.shape.getMaxExtentY());
            worldMaxR = Math.max(worldMaxR, body.shape.getBoundingRadius());
        }

        float maxDiameter = 2 * worldMaxR;
        float worldWidth  = Math.abs(worldMaxX - worldMinX);
        float worldHeight = Math.abs(worldMaxY - worldMinY);
        int   rows        = Math.min((int) Math.ceil(worldHeight / maxDiameter), 32);
        int   cols        = Math.min((int) Math.ceil(worldWidth  / maxDiameter), 32);
        float cellWidth   = worldWidth  / cols;
        float cellHeight  = worldHeight / rows;

        /* collision detection - broad phase */
        cellMemoryPool.freeAll(spacePartition);
        spacePartition.clear();
        activeCells.clear();
        for (int i = 0; i < rows * cols; i++) {
            spacePartition.add(cellMemoryPool.allocate());
        }

        for (Physics2DBody body : allBodies) {
            int startCol = Math.max(0, (int) ((body.shape.getMinExtentX() - worldMinX) / cellWidth));
            int endCol   = Math.min(cols - 1, (int) ((body.shape.getMaxExtentX() - worldMinX) / cellWidth));
            int startRow = Math.max(0, (int) ((body.shape.getMinExtentY() - worldMinY) / cellHeight));
            int endRow   = Math.min(rows - 1, (int) ((body.shape.getMaxExtentY() - worldMinY) / cellHeight));

            for (int row = startRow; row <= endRow; row++) {
                for (int col = startCol; col <= endCol; col++) {
                    Cell cell = spacePartition.get(row * cols + col);
                    cell.bodies.add(body);
                    if (!cell.active) {
                        cell.active = true;
                        activeCells.add(cell);
                    }
                }
            }
        }

        taskMemoryPool.freeAll(tasks);
        tasks.clear();
        for (int i = 0; i < processors; i++) {
            tasks.add(taskMemoryPool.allocate());
        }
        for (int i = 0; i < activeCells.size; i++) {
            tasks.getCyclic(i).cellsToProcess.add(activeCells.get(i));
        }

        collisionCandidates.clear();
        final List<Callable<Void>> taskList = new LinkedList<>();
        for (Callable<Void> callable : tasks) {
            taskList.add(callable);
        }
        try {
            executor.invokeAll(taskList);
        } catch (InterruptedException e) {
            throw new Physics2DException("Update thread interrupted with Exception: " + e);
        }
        for (Cell cell : activeCells) {
            for (CollisionPair pair : cell.pairs) {
                collisionCandidates.add(pair);
            }
        }
        //pairMemoryPool.freeAll(collisionCandidates);
        //collisionCandidates.clear();
//        for (Cell cell : activeCells) {
//            for (int i = 0; i < cell.bodies.size - 1; i++) {
//                for (int j = i + 1; j < cell.bodies.size; j++) {
//                    Physics2DBody body_a = cell.bodies.get(i);
//                    Physics2DBody body_b = cell.bodies.get(j);
//                    if (body_a.off) continue;
//                    if (body_b.off) continue;
//                    if (body_a.motionType == Physics2DBody.MotionType.STATIC && body_b.motionType == Physics2DBody.MotionType.STATIC) continue;
//                    final float dx  = body_b.shape.x() - body_a.shape.x();
//                    final float dy  = body_b.shape.y() - body_a.shape.y();
//                    final float sum = body_a.shape.getBoundingRadius() + body_b.shape.getBoundingRadius();
//                    boolean boundingCirclesCollide = dx * dx + dy * dy < sum * sum;
//                    if (!boundingCirclesCollide) continue;
//
//                    CollisionPair pair = pairMemoryPool.allocate();
//                    pair.a = body_a;
//                    pair.b = body_b;
//                    collisionCandidates.add(pair);
//                }
//            }
//        }


        /* collision detection - narrow phase */
        manifoldMemoryPool.freeAll(manifolds);
        manifolds.clear();
        for (CollisionPair pair : collisionCandidates) {
            Physics2DBody body_a = pair.a;
            Physics2DBody body_b = pair.b;
            CollisionManifold manifold = collisionDetection.detectCollision(body_a, body_b);
            if (manifold == null) continue;
            manifold.body_a = body_a;
            manifold.body_b = body_b;
            manifolds.add(manifold);
        }

        /* collision resolution */
        manifolds.sort(); // to achieve deterministic behaviour, we should resolve the collisions in consistent order. We resolve collisions with greater penetration depth first.
        for (CollisionManifold manifold : manifolds) {
            Physics2DBody body_a = manifold.body_a;
            Physics2DBody body_b = manifold.body_b;

            body_a.collidesWith.add(body_b);
            body_b.collidesWith.add(body_a);
            collisionResolver.beginContact(body_a, body_b);
            collisionResolver.preSolve(manifold);
            collisionResolver.solve(body_a, body_b, manifold);
            collisionResolver.postSolve(manifold);
            collisionResolver.endContact(body_a, body_b);
        }

    }

    @Contract(pure = true)
    @NotNull public Physics2DBody createBodyCircle(Object owner,
                                            Physics2DBody.MotionType motionType,
                                            float x, float y, float angle,
                                            float velX, float velY, float velAngleDeg,
                                            float density, float friction, float restitution,
                                            boolean ghost, int bitmask,
                                            float radius) {
        Physics2DBody body = bodyMemoryPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;
        body.density = density;
        body.shape = new Shape2DCircle(radius);
        body.massInv = 1.0f / (body.shape.getArea() * density);
        body.inertiaInv = 1.0f / calculateMomentOfInertia(body.shape, density);
        body.staticFriction = friction;
        body.restitution = MathUtils.clampFloat(restitution, 0, 1.0f);
        body.ghost = ghost;
        body.bitmask = bitmask;
        body.setMotionState(x, y, angle, velX, velY, velAngleDeg);
        bodiesToAdd.add(body);
        return body;
    }

    @Contract(pure = true)
    @NotNull public Physics2DBody createBodyCircle(Object owner,
                                            Physics2DBody.MotionType motionType,
                                            float x, float y, float angleDeg,
                                            float velX, float velY, float velAngleDeg,
                                            float density, float friction, float restitution,
                                            boolean ghost, int bitmask,
                                            float radius, float offsetX, float offsetY) {
        Physics2DBody body = bodyMemoryPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;
        body.density = density;
        body.shape = new Shape2DCircle(radius, offsetX, offsetY);
        body.massInv = 1.0f / (body.shape.getArea() * density);
        body.inertiaInv = 1.0f / calculateMomentOfInertia(body.shape, density);
        body.staticFriction = friction;
        body.restitution = MathUtils.clampFloat(restitution, 0, 1.0f);
        body.ghost = ghost;
        body.bitmask = bitmask;
        body.setMotionState(x, y, angleDeg, velX, velY, velAngleDeg);
        bodiesToAdd.add(body);
        return body;
    }

    @Contract(pure = true)
    @NotNull public Physics2DBody createBodyRectangle(Object owner,
                                               Physics2DBody.MotionType motionType,
                                               float x, float y, float angleDeg,
                                               float velX, float velY, float velAngleDeg,
                                               float density, float friction, float restitution,
                                               boolean ghost, int bitmask,
                                               float width, float height, float rot) {
        Physics2DBody body = bodyMemoryPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;
        body.density = density;
        body.shape = new Shape2DRectangle(width, height, rot);
        body.massInv = 1.0f / (body.shape.getArea() * density);
        body.inertiaInv = 1.0f / calculateMomentOfInertia(body.shape, density);
        body.staticFriction = friction;
        body.restitution = MathUtils.clampFloat(restitution, 0, 1.0f);
        body.ghost = ghost;
        body.bitmask = bitmask;
        body.setMotionState(x, y, angleDeg, velX, velY, velAngleDeg);
        bodiesToAdd.add(body);
        return body;
    }

    @Contract(pure = true)
    @NotNull public Physics2DBody createBodyRectangle(Object owner,
                                               Physics2DBody.MotionType motionType,
                                               float x, float y, float angleDeg,
                                               float velX, float velY, float velAngleDeg,
                                               float density, float friction, float restitution,
                                               boolean ghost, int bitmask,
                                               float width, float height, float offsetX, float offsetY, float rot) {
        Physics2DBody body = bodyMemoryPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;
        body.density = density;
        body.shape = new Shape2DRectangle(offsetX, offsetY, width, height, rot);
        body.massInv = 1.0f / (body.shape.getArea() * density);
        body.inertiaInv = 1.0f / calculateMomentOfInertia(body.shape, density);
        body.staticFriction = friction;
        body.restitution = MathUtils.clampFloat(restitution, 0, 1.0f);
        body.ghost = ghost;
        body.bitmask = bitmask;
        body.setMotionState(x, y, angleDeg, velX, velY, velAngleDeg);
        bodiesToAdd.add(body);
        return body;
    }

    @Contract(pure = true)
    @NotNull public Physics2DBody createBodyPolygon(Object owner,
                                             Physics2DBody.MotionType motionType,
                                             float x, float y, float angleDeg,
                                             float velX, float velY, float velAngleDeg,
                                             float density, float friction, float restitution,
                                             boolean ghost, int bitmask,
                                             float[] vertices) {
        Physics2DBody body = bodyMemoryPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;
        body.density = density;

        final boolean isConvex = ShapeUtils.isPolygonConvex(vertices);
        if (isConvex) {
            body.shape = new Shape2DPolygon(vertices);
        } else {
            // TODO: create union of triangles.
        }
        //body.shape = new Shape2DRectangle(width, height, angle);

        body.massInv = 1.0f / (body.shape.getArea() * density);
        body.inertiaInv = 1.0f / calculateMomentOfInertia(body.shape, density);
        body.staticFriction = friction;
        body.restitution = MathUtils.clampFloat(restitution, 0, 1.0f);
        body.ghost = ghost;
        body.bitmask = bitmask;
        body.setMotionState(x, y, angleDeg, velX, velY, velAngleDeg);
        bodiesToAdd.add(body);
        return body;
    }

    // TODO
    @Contract(pure = true)
    @NotNull Physics2DBody createBodyUnion(Object owner,
                                           boolean sleeping, Physics2DBody.MotionType motionType,
                                           float x, float y, float angleDeg,
                                           float velX, float velY, float velAngleDeg,
                                           float massInv, float density, float friction, float restitution,
                                           boolean ghost, int bitmask,
                                           Shape2D... shapes) {
        Physics2DBody body = bodyMemoryPool.allocate();
        body.owner = owner;
        body.off = sleeping;
        body.motionType = motionType;

        body.setMotionState(x, y, angleDeg, velX, velY, velAngleDeg);
        bodiesToAdd.add(body);
        return body;
    }

    public static float calculateMomentOfInertia(final Shape2D shape, float density) {
        return 1;
    }

    public void destroyBody(final Physics2DBody body) {
        bodiesToRemove.add(body);
    }

    public void createJoint() {

    }

    public void destroyJoint() {

    }

    public void castRay() {

    }

    public void render(GraphicsRenderer2D renderer) {
        debugRenderer.render(renderer);
    }

    // TODO: find where to use
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static final class CollisionManifold implements MemoryPool.Reset, Comparable<CollisionManifold> {

        public Physics2DBody body_a        = null;
        public Physics2DBody body_b        = null;
        public Shape2D       shape_a       = null;
        public Shape2D       shape_b       = null;
        public int           contacts      = 0;
        public float         depth         = 0;
        public MathVector2   normal        = new MathVector2();
        public MathVector2   contactPoint1 = new MathVector2();
        public MathVector2   contactPoint2 = new MathVector2();

        // TODO: remove these - we will calculate them on resolution. No need to waste memory space.
        public float mixedRestitution = 0;
        public float staticFriction   = 0;
        public float dynamicFriction  = 0;

        @Override
        public void reset() {
            this.contacts = 0;
        }

        @Override
        public String toString() {
            return "CollisionManifold{" +
                    "body_a=" + body_a +
                    ", body_b=" + body_b +
                    ", contacts=" + contacts +
                    ", depth=" + depth +
                    ", normal=" + normal +
                    '}';
        }

        @Override
        public int compareTo(@NotNull CollisionManifold other) {
            // Compare by depth
            int depthCompare = Float.compare(other.depth, this.depth);
            if (depthCompare != 0) {
                return depthCompare;
            }

            // Compare by body_a
            int bodyACompare = this.body_a.compareTo(other.body_a);
            if (bodyACompare != 0) {
                return bodyACompare;
            }

            // Compare by body_b
            return this.body_b.compareTo(other.body_b);
        }

    }

    public static final class Cell implements MemoryPool.Reset {

        private final CollectionsArray<Physics2DBody> bodies = new CollectionsArray<>(false, 2);
        private final CollectionsArray<CollisionPair> pairs  = new CollectionsArray<>(false, 2);

        private boolean active = false;

        public Cell() {}

        @Override
        public void reset() {
            bodies.clear();
            pairs.clear();
            active = false;
        }

    }

    public static final class CellsProcessor implements MemoryPool.Reset, Callable<Void> {

        private final CollectionsArray<Cell>    cellsToProcess = new CollectionsArray<>();
        private final MemoryPool<CollisionPair> pairMemoryPool = new MemoryPool<>(CollisionPair.class, 4);

        public CellsProcessor() {}

        @Override
        public Void call() {
            for (Cell cell : cellsToProcess) {
                for (int i = 0; i < cell.bodies.size - 1; i++) {
                    for (int j = i + 1; j < cell.bodies.size; j++) {
                        Physics2DBody a = cell.bodies.get(i);
                        Physics2DBody b = cell.bodies.get(j);
                        if (a.off) continue;
                        if (b.off) continue;
                        if (a.motionType == Physics2DBody.MotionType.STATIC && b.motionType == Physics2DBody.MotionType.STATIC) continue;
                        if (a == b) continue;
                        if (!boundingCirclesCollide(a.shape, b.shape)) continue;

                        CollisionPair pair = pairMemoryPool.allocate();
                        pair.a = a;
                        pair.b = b;
                        cell.pairs.add(pair);
                    }
                }
            }
            return null;
        }

        private boolean boundingCirclesCollide(final Shape2D a, final Shape2D b) {
            final float dx  = b.x() - a.x();
            final float dy  = b.y() - a.y();
            final float sum = a.getBoundingRadius() + b.getBoundingRadius();
            return dx * dx + dy * dy < sum * sum;
        }

        @Override
        public void reset() {
            for (Cell cell : cellsToProcess) {
                pairMemoryPool.freeAll(cell.pairs);
            }
            cellsToProcess.clear();
        }

    }

    public static class CollisionPair implements MemoryPool.Reset {

        public Physics2DBody a;
        public Physics2DBody b;

        public CollisionPair() {}

        public CollisionPair(Physics2DBody a, Physics2DBody b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CollisionPair that = (CollisionPair) o;
            if (Objects.equals(a, that.a) && Objects.equals(b, that.b)) return true;
            if (Objects.equals(b, that.a) && Objects.equals(a, that.b)) return true;
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(a) + Objects.hashCode(b); // A commutative operation to ensure symmetry
        }

        @Override
        public void reset() {}

    }

}
