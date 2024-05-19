package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.collections.CollectionsUtils;
import org.example.engine.core.graphics.GraphicsRenderer2D;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.physics2d.Physics2DCollisionListener;
import org.example.engine.core.shape.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

// https://github.com/RandyGaul/ImpulseEngine/blob/master/Manifold.h
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-basics-and-impulse-resolution--gamedev-6331t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-core-engine--gamedev-7493t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-friction-scene-and-jump-table--gamedev-7756t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-oriented-rigid-bodies--gamedev-8032t
public class Physics2DWorld {

    // bodies, joints, constraints and manifolds
    public MemoryPool<Physics2DBody>       bodyMemoryPool     = new MemoryPool<>(Physics2DBody.class,     10);
    public MemoryPool<CollisionManifold>   manifoldMemoryPool = new MemoryPool<>(CollisionManifold.class, 10);
    public CollectionsArray<Physics2DBody> allBodies          = new CollectionsArray<>(false, 500);
    public CollectionsArray<Physics2DBody> bodiesToAdd        = new CollectionsArray<>(false, 100);
    public CollectionsArray<Physics2DBody> bodiesToRemove     = new CollectionsArray<>(false, 500);

    protected final CollectionsArray<Cell> spacePartition = new CollectionsArray<>(false, 1024);
    protected final CollectionsArray<Cell> activeCells    = new CollectionsArray<>();
    private   final MemoryPool<Cell>       cellMemoryPool = new MemoryPool<>(Cell.class,1024);
    protected float worldWidth    = 0;
    protected float worldHeight   = 0;
    protected float worldMinX     = 0;
    protected float worldMaxX     = 0;
    protected float worldMinY     = 0;
    protected float worldMaxY     = 0;
    protected float worldMaxR     = 0;
    protected int   rows          = 0;
    protected int   cols          = 0;
    protected float cellWidth     = 0;
    protected float cellHeight    = 0;
    protected int   bodiesCreated = 0;

    private   final Physics2DCollisionDetection collisionDetection  = new Physics2DCollisionDetection(this);
    protected final Physics2DWorldRenderer      debugRenderer       = new Physics2DWorldRenderer(this);
    protected       Physics2DCollisionResolver  collisionResolver;

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
        // add and remove bodies.
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

        worldMinX = Float.POSITIVE_INFINITY;
        worldMaxX = Float.NEGATIVE_INFINITY;
        worldMinY = Float.POSITIVE_INFINITY;
        worldMaxY = Float.NEGATIVE_INFINITY;
        worldMaxR = Float.NEGATIVE_INFINITY;

        // integration
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

        final float maxDiameter = 2 * worldMaxR;
        worldWidth  = Math.abs(worldMaxX - worldMinX);
        worldHeight = Math.abs(worldMaxY - worldMinY);
        rows = Math.min((int) Math.ceil(worldHeight / maxDiameter), 32);
        cols = Math.min((int) Math.ceil(worldWidth  / maxDiameter), 32);
        cellWidth  = worldWidth  / cols;
        cellHeight = worldHeight / rows;

        cellMemoryPool.freeAll(spacePartition);
        spacePartition.clear();
        activeCells.clear();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = cellMemoryPool.allocate();
                spacePartition.add(cell);
            }
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

        for (Cell cell : activeCells) {
            for (int i = 0; i < cell.bodies.size - 1; i++) {
                for (int j = i + 1; j < cell.bodies.size; j++) {
                    Physics2DBody a = cell.bodies.get(i);
                    Physics2DBody b = cell.bodies.get(j);
                    if (a.off) continue;
                    if (b.off) continue;
                    if (a.motionType == Physics2DBody.MotionType.STATIC && b.motionType == Physics2DBody.MotionType.STATIC) continue;
                    if (a == b) continue;
                    final float dx  = b.shape.x() - a.shape.x();
                    final float dy  = b.shape.y() - a.shape.y();
                    final float sum = a.shape.getBoundingRadius() + b.shape.getBoundingRadius();
                    boolean boundingCirclesCollide = dx * dx + dy * dy < sum * sum;

                    if (!boundingCirclesCollide) continue;

                    CollisionManifold manifold = collisionDetection.detectCollision(a, b);
                    if (manifold == null) continue;

                    a.collidesWith.add(b);
                    b.collidesWith.add(a);
                    collisionResolver.beginContact(a, b);
                    collisionResolver.preSolve(manifold);
                    collisionResolver.solve(a, b, manifold);
                    collisionResolver.postSolve(manifold);
                    collisionResolver.endContact(a, b);
                }
            }
        }

    }


    private void updateBroadPhase_old(float deltaTime) {
        worldMinX = Float.POSITIVE_INFINITY;
        worldMaxX = Float.NEGATIVE_INFINITY;
        worldMinY = Float.POSITIVE_INFINITY;
        worldMaxY = Float.NEGATIVE_INFINITY;
        worldMaxR = Float.NEGATIVE_INFINITY;

        for (Physics2DBody body : allBodies) {
            if (body.off) continue;
            if (body.motionType == Physics2DBody.MotionType.NEWTONIAN) {
                body.velocity.add(body.massInv * deltaTime * body.netForce.x, body.massInv * deltaTime * body.netForce.y);
                body.omega += body.netTorque * (body.inertiaInv) * deltaTime;
            }
            if (body.motionType != Physics2DBody.MotionType.STATIC) {
                body.shape.dx_dy_rot(deltaTime * body.velocity.x, deltaTime * body.velocity.y, deltaTime * body.omega);
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

        final float maxDiameter = 2 * worldMaxR;
        worldWidth  = Math.abs(worldMaxX - worldMinX);
        worldHeight = Math.abs(worldMaxY - worldMinY);
        rows = Math.min((int) Math.ceil(worldHeight  / maxDiameter), 32);
        cols = Math.min((int) Math.ceil(worldWidth   / maxDiameter), 32);
        cellWidth  = worldWidth  / cols;
        cellHeight = worldHeight / rows;

        cellMemoryPool.freeAll(spacePartition);
        activeCells.clear();
        spacePartition.clear();

        // data from previous phase
        final float minX = worldMinX;
        final float minY = worldMinY;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = cellMemoryPool.allocate();
                spacePartition.add(cell);
            }
        }

        for (Physics2DBody body : allBodies) {
            int startCol = Math.max(0, (int) ((body.shape.getMinExtentX() - minX) / cellWidth));
            int endCol   = Math.min(cols - 1, (int) ((body.shape.getMaxExtentX() - minX) / cellWidth));
            int startRow = Math.max(0, (int) ((body.shape.getMinExtentY() - minY) / cellHeight));
            int endRow   = Math.min(rows - 1, (int) ((body.shape.getMaxExtentY() - minY) / cellHeight));

            for (int row = startRow; row <= endRow; row++) {
                for (int col = startCol; col <= endCol; col++) {
                    int cellIndex = row * cols + col;
                    Cell cell = spacePartition.get(cellIndex);
                    cell.bodies.add(body);
                    if (!cell.active) {
                        cell.active = true;
                        activeCells.add(cell);
                    }
                }
            }
        }

        for (Cell cell : activeCells) {
            for (int i = 0; i < cell.bodies.size - 1; i++) {
                for (int j = i + 1; j < cell.bodies.size; j++) {
                    Physics2DBody a = cell.bodies.get(i);
                    Physics2DBody b = cell.bodies.get(j);
                    if (a.off) continue;
                    if (b.off) continue;
                    if (a.motionType == Physics2DBody.MotionType.STATIC && b.motionType == Physics2DBody.MotionType.STATIC) continue;
                    if (a == b) continue;
                    final float dx  = b.shape.x() - a.shape.x();
                    final float dy  = b.shape.y() - a.shape.y();
                    final float sum = a.shape.getBoundingRadius() + b.shape.getBoundingRadius();
                    boolean boundingCirclesCollide = dx * dx + dy * dy < sum * sum;

                    if (!boundingCirclesCollide) continue;

                    CollisionManifold manifold = collisionDetection.detectCollision(a, b);

                }
            }
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
                                           Shape2D...shapes) {
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

    public static final class CollisionManifold implements MemoryPool.Reset {

        public Shape2D     shape_a         = null;
        public Shape2D     shape_b         = null;
        public int         contacts        = 0;
        public float       depth           = 0;
        public MathVector2 normal          = new MathVector2();
        public MathVector2 contactPoint1   = new MathVector2();
        public MathVector2 contactPoint2   = new MathVector2();
        public float       staticFriction  = 0;
        public float       dynamicFriction = 0;

        @Override
        public void reset() {
            this.shape_a = null;
            this.shape_b = null;
            this.contacts = 0;
        }

    }

    public static final class Cell implements MemoryPool.Reset {

        private final CollectionsArray<Physics2DBody> bodies = new CollectionsArray<>(false, 2);

        private boolean active = false;

        public Cell() {}

        @Override
        public void reset() {
            bodies.clear();
            active = false;
        }

    }

}
