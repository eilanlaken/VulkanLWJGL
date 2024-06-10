package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.a_old_Renderer2D;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;

// https://github.com/RandyGaul/ImpulseEngine/blob/master/Manifold.h
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-basics-and-impulse-resolution--gamedev-6331t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-core-engine--gamedev-7493t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-friction-scene-and-jump-table--gamedev-7756t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-oriented-rigid-bodies--gamedev-8032t

// TODO: RAY CASTING, optimized: https://theshoemaker.de/posts/ray-casting-in-2d-grids
// TODO:
/*

useful reference
https://github.com/acrlw/Physics2D/blob/master/Physics2D/source/dynamics/physics2d_system.cpp

Look up: TODO
https://box2d.org/documentation/b2__body_8h_source.html
 void ApplyForce(const b2Vec2& force, const b2Vec2& point, bool wake);
 void ApplyForceToCenter(const b2Vec2& force, bool wake);
 void ApplyTorque(float torque, bool wake);
 void ApplyLinearImpulse(const b2Vec2& impulse, const b2Vec2& point, bool wake);
 void ApplyLinearImpulseToCenter(const b2Vec2& impulse, bool wake);
 void ApplyAngularImpulse(float impulse, bool wake);

 */
public class Physics2DWorld {

    public static final int DEFAULT_VELOCITY_CONSTRAINT_SOLVER_ITERATIONS = 6;
    public static final int DEFAULT_POSITION_CONSTRAINT_SOLVER_ITERATIONS = 2;

    // memory pools
    private final MemoryPool<Physics2DBody>     bodiesPool         = new MemoryPool<>(Physics2DBody.class,     10);
    private final MemoryPool<CollisionManifold> manifoldsPool      = new MemoryPool<>(CollisionManifold.class, 10);
    private final MemoryPool<CollisionPair>     pairsPool          = new MemoryPool<>(CollisionPair.class, 5);
    private final MemoryPool<Cell>              cellsPool          = new MemoryPool<>(Cell.class,1024);
    private final MemoryPool<Ray>               raysPool           = new MemoryPool<>(Ray.class, 4);
    private final MemoryPool<Intersection>      intersectionsPool  = new MemoryPool<>(Intersection.class, 4);

    // bodies
    private int                             bodiesCreated      = 0;
    public  int                             positionIterations = 2;
    public Array<Physics2DBody> allBodies          = new Array<>(false, 500);
    public Array<Physics2DBody> bodiesToAdd        = new Array<>(false, 100);
    public Array<Physics2DBody> bodiesToRemove     = new Array<>(false, 500);

    // forces
    public Array<Physics2DForceField> allForceFields      = new Array<>(false, 4);
    public Array<Physics2DForceField> forceFieldsToAdd    = new Array<>(false, 2);
    public Array<Physics2DForceField> forceFieldsToRemove = new Array<>(false, 2);

    // constraints
    public int                                   velocityIterations  = 6;
    public Array<Physics2DConstraint> allConstraints      = new Array<>(false, 10);
    public Array<Physics2DConstraint> constraintsToAdd    = new Array<>(false, 5);
    public Array<Physics2DConstraint> constraintsToRemove = new Array<>(false, 5);

    // collision detection
    private final Array<Cell> spacePartition      = new Array<>(false, 1024);
    private final Array<Cell> activeCells         = new Array<>();
    private final Set<CollisionPair>          collisionCandidates = new HashSet<>();
    private final Physics2DCollisionDetection collisionDetection  = new Physics2DCollisionDetection(this);

    // collision resolution
    private final Array<CollisionManifold> manifolds = new Array<>(false, 20);
    private final Physics2DCollisionResolver          collisionResolver;

    // ray casting
    final Physics2DRayCasting            rayCasting    = new Physics2DRayCasting(this);
    final HashMap<Ray, RayHitCallback>   allRays       = new HashMap<>(4);
    final HashMap<Ray, RayHitCallback>   raysToAdd     = new HashMap<>(4);
    final HashMap<Ray, RayHitCallback>   raysToRemove  = new HashMap<>(4);
    final Array<Intersection> intersections = new Array<>(false, 10);

    // debugger options
    private final Physics2DWorldRenderer debugRenderer     = new Physics2DWorldRenderer(this);
    public        boolean                renderBodies      = true;
    public        boolean                renderVelocities  = false;
    public        boolean                renderConstraints = true;
    public        boolean                renderRays        = true;
    public        boolean                renderContacts    = true;

    public Physics2DWorld(Physics2DCollisionResolver collisionResolver) {
        this.collisionResolver = collisionResolver != null ? collisionResolver : new Physics2DCollisionResolver() {};
    }

    public Physics2DWorld() {
        this(null);
    }

    MemoryPool<CollisionManifold> getManifoldsPool() {
        return manifoldsPool;
    }

    MemoryPool<Intersection> getIntersectionsPool() {
        return intersectionsPool;
    }

    public void update(float delta) {
        /* add and remove bodies */
        {
            for (Physics2DBody body : bodiesToRemove) {
                for (Physics2DConstraint constraint : body.constraints) {
                    destroyConstraint(constraint);
                }
                allBodies.removeValue(body, true);
                bodiesPool.free(body);
            }
            for (Physics2DBody body : bodiesToAdd) {
                allBodies.add(body);
                body.inserted = true;
                body.index = bodiesCreated;
                bodiesCreated++;
            }
            bodiesToRemove.clear();
            bodiesToAdd.clear();
        }

        /* preparation: add and remove force fields */
        {
            for (Physics2DForceField forceField : forceFieldsToRemove) {
                allForceFields.removeValue(forceField, true);
            }
            for (Physics2DForceField forceField : forceFieldsToAdd) {
                allForceFields.add(forceField);
            }
            forceFieldsToRemove.clear();
            forceFieldsToAdd.clear();
        }

        /* preparation: add and remove constraints */
        {
            Array<Physics2DBody> constraintBodies = new Array<>();
            for (Physics2DConstraint constraint : constraintsToAdd) {
                constraint.getBodies(constraintBodies);
                boolean ready = true;
                for (Physics2DBody body : constraintBodies) {
                    if (!body.inserted || body.off) {
                        ready = false;
                        break;
                    }
                }
                if (!ready) continue;
                for (Physics2DBody body : constraintBodies) {
                    body.constraints.add(constraint);
                }
                allConstraints.add(constraint);
            }
            for (Physics2DConstraint joint : constraintsToRemove) {
                allConstraints.removeValue(joint, true);
            }
            constraintsToRemove.clear();
            constraintsToAdd.clear();
        }

        // TODO: apply damping (linear, angular)
        /* Euler integration: velocities and positions */
        {
            for (Physics2DBody body : allBodies) {
                if (body.off) continue;
                if (body.motionType == Physics2DBody.MotionType.NEWTONIAN) {
                    for (Physics2DForceField field : allForceFields) {
                        Vector2 force = new Vector2();
                        field.calculateForce(body, force);
                        body.netForce.add(force);
                    }
                    body.velocity.x += body.massInv * delta * body.netForce.x;
                    body.velocity.y += body.massInv * delta * body.netForce.y;
                    body.omegaDeg   += body.netTorque * (body.inertiaInv) * delta * MathUtils.radiansToDegrees;
                }
                if (body.motionType != Physics2DBody.MotionType.STATIC) {
                    body.shape.dx_dy_rot(delta * body.velocity.x, delta * body.velocity.y, delta * body.omegaDeg);
                }
                body.shape.update();
                body.netForce.set(0, 0);
                body.netTorque = 0;
                body.touching.clear();
            }
        }

        /* collision detection: broad phase */
        {
            float worldMinX = Float.POSITIVE_INFINITY;
            float worldMaxX = Float.NEGATIVE_INFINITY;
            float worldMinY = Float.POSITIVE_INFINITY;
            float worldMaxY = Float.NEGATIVE_INFINITY;
            float worldMaxR = Float.NEGATIVE_INFINITY;

            for (Physics2DBody body : allBodies) {
                worldMinX = Math.min(worldMinX, body.shape.getMinExtentX());
                worldMaxX = Math.max(worldMaxX, body.shape.getMaxExtentX());
                worldMinY = Math.min(worldMinY, body.shape.getMinExtentY());
                worldMaxY = Math.max(worldMaxY, body.shape.getMaxExtentY());
                worldMaxR = Math.max(worldMaxR, body.shape.getBoundingRadius());
            }

            float maxDiameter = 2 * worldMaxR;
            float worldWidth = Math.abs(worldMaxX - worldMinX);
            float worldHeight = Math.abs(worldMaxY - worldMinY);
            int rows = Math.min((int) Math.ceil(worldHeight / maxDiameter), 32);
            int cols = Math.min((int) Math.ceil(worldWidth / maxDiameter), 32);
            float cellWidth = worldWidth / cols;
            float cellHeight = worldHeight / rows;

            /* collision detection - broad phase */
            cellsPool.freeAll(spacePartition);
            spacePartition.clear();
            activeCells.clear();
            for (int i = 0; i < rows * cols; i++) {
                spacePartition.add(cellsPool.allocate());
            }

            for (Physics2DBody body : allBodies) {
                int startCol = Math.max(0, (int) ((body.shape.getMinExtentX() - worldMinX) / cellWidth));
                int endCol = Math.min(cols - 1, (int) ((body.shape.getMaxExtentX() - worldMinX) / cellWidth));
                int startRow = Math.max(0, (int) ((body.shape.getMinExtentY() - worldMinY) / cellHeight));
                int endRow = Math.min(rows - 1, (int) ((body.shape.getMaxExtentY() - worldMinY) / cellHeight));

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

            pairsPool.freeAll(collisionCandidates);
            collisionCandidates.clear();
            for (Cell cell : activeCells) {
                for (int i = 0; i < cell.bodies.size - 1; i++) {
                    for (int j = i + 1; j < cell.bodies.size; j++) {
                        Physics2DBody body_a = cell.bodies.get(i);
                        Physics2DBody body_b = cell.bodies.get(j);
                        if (body_a.off) continue;
                        if (body_b.off) continue;
                        if (body_a.motionType == Physics2DBody.MotionType.STATIC && body_b.motionType == Physics2DBody.MotionType.STATIC)
                            continue;
                        final float dx = body_b.shape.x() - body_a.shape.x();
                        final float dy = body_b.shape.y() - body_a.shape.y();
                        final float sum = body_a.shape.getBoundingRadius() + body_b.shape.getBoundingRadius();
                        boolean boundingCirclesCollide = dx * dx + dy * dy < sum * sum;
                        if (!boundingCirclesCollide) continue;

                        CollisionPair pair = pairsPool.allocate();
                        pair.a = body_a;
                        pair.b = body_b;
                        collisionCandidates.add(pair);
                    }
                }
            }
        }

        /* collision detection - narrow phase */
        {
            manifoldsPool.freeAll(manifolds);
            manifolds.clear();
            for (CollisionPair pair : collisionCandidates) {
                Physics2DBody body_a = pair.a;
                Physics2DBody body_b = pair.b;
                CollisionManifold manifold = collisionDetection.detectCollision(body_a, body_a.shape, body_b, body_b.shape);
                if (manifold == null) continue;
                manifolds.add(manifold);
            }
        }

        /* solve constraints */
        {
            for (Physics2DConstraint constraint : allConstraints) {
                constraint.prepare(delta);
            }

            // TODO: for (int i = 0; i < velocityIterations; i++) {
            for (int i = 0; i < 1; i++) {
                for (Physics2DConstraint constraint : allConstraints) {
                    constraint.solveVelocity(delta);
                }
            }

            boolean positionConstraintsSolved = false;
            // TODO: for (int i = 0; i < positionIterations; i++) {
            for (int i = 0; i < positionIterations; i++) {

                // solve the joint position constraints
                boolean allSolved = true;
                for (Physics2DConstraint constraint : allConstraints) {
                    boolean solved = constraint.solvePosition(delta);
                    allSolved = allSolved && solved;
                }
                if (allSolved) break;
            }
        }

        /* collision resolution */
        {
            // TODO: need to figure out how to properly set the Body's: justCollided, touching, justSeparated.
            for (CollisionManifold manifold : manifolds) {
                Physics2DBody body_a = manifold.body_a;
                Physics2DBody body_b = manifold.body_b;

                body_a.touching.add(body_b);
                body_b.touching.add(body_a);
                collisionResolver.beginContact(manifold);
                collisionResolver.resolve(manifold);
                collisionResolver.endContact(manifold);
            }
        }

        // TODO: remove
        /* position integration */
        {
//            for (Physics2DBody body : allBodies) {
//                if (body.motionType != Physics2DBody.MotionType.STATIC) {
//                    body.shape.dx_dy_rot(delta * body.velocity.x, delta * body.velocity.y, delta * body.omegaDeg);
//                }
//                body.shape.update();
//            }
        }

        /* ray casting */
        {
            for (Map.Entry<Ray, RayHitCallback> rayCallback : raysToRemove.entrySet()) {
                Ray ray = rayCallback.getKey();
                allRays.remove(ray);
                raysPool.free(ray);
            }
            allRays.putAll(raysToAdd);
            raysToRemove.clear();
            raysToAdd.clear();
            intersectionsPool.freeAll(intersections);
            intersections.clear();

            for (Map.Entry<Ray, RayHitCallback> rayCallback : allRays.entrySet()) {
                Ray ray = rayCallback.getKey();
                RayHitCallback callback = rayCallback.getValue();
                // set the distance for the ray based on world's extent
                if (ray.dst == Float.POSITIVE_INFINITY || Float.isNaN(ray.dst)) {

                }
                Array<Intersection> results = new Array<>();
                // TODO: optimize this using the cell grid.
                rayCasting.calculateIntersections(ray, allBodies, results);
                intersections.addAll(results);
                if (callback != null) callback.intersected(results);
                results.clear();
                raysToRemove.put(ray, callback);
            }
        }
    }

    @Contract(pure = true)
    @NotNull public Physics2DBody createBodyCircle(Object owner,
                                            Physics2DBody.MotionType motionType,
                                            float x, float y, float angle,
                                            float velX, float velY, float velAngleDeg,
                                            float density, float staticFriction, float dynamicFriction, float restitution,
                                            boolean ghost, int bitmask,
                                            float radius) {
        Physics2DBody body = bodiesPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;
        body.density = density;
        body.shape = new Shape2DCircle(radius);
        body.mass = (body.shape.getArea() * density);
        body.massInv = 1.0f / body.mass;
        body.inertiaInv = 1.0f / calculateMomentOfInertia(body.shape, density);
        body.staticFriction  = staticFriction;
        body.dynamicFriction = dynamicFriction;
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
                                            float density, float staticFriction, float dynamicFriction, float restitution,
                                            boolean ghost, int bitmask,
                                            float radius, float offsetX, float offsetY) {
        Physics2DBody body = bodiesPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;
        body.density = density;
        body.shape = new Shape2DCircle(radius, offsetX, offsetY);
        body.mass = (body.shape.getArea() * density);
        body.massInv = 1.0f / body.mass;
        body.inertiaInv = 1.0f / calculateMomentOfInertia(body.shape, density);
        body.staticFriction  = staticFriction;
        body.dynamicFriction = dynamicFriction;
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
                                               float density, float staticFriction, float dynamicFriction, float restitution,
                                               boolean ghost, int bitmask,
                                               float width, float height, float rot) {
        Physics2DBody body = bodiesPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;
        body.density = density;
        body.shape = new Shape2DRectangle(width, height, rot);
        body.mass = (body.shape.getArea() * density);
        body.massInv = 1.0f / body.mass;
        body.inertiaInv = 1.0f / calculateMomentOfInertia(body.shape, density);
        body.staticFriction  = staticFriction;
        body.dynamicFriction = dynamicFriction;
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
                                               float density, float staticFriction, float dynamicFriction, float restitution,
                                               boolean ghost, int bitmask,
                                               float width, float height, float offsetX, float offsetY, float rot) {
        Physics2DBody body = bodiesPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;
        body.density = density;
        body.shape = new Shape2DRectangle(offsetX, offsetY, width, height, rot);
        body.mass = (body.shape.getArea() * density);
        body.massInv = 1.0f / body.mass;
        body.inertiaInv = 1.0f / calculateMomentOfInertia(body.shape, density);
        body.staticFriction  = staticFriction;
        body.dynamicFriction = dynamicFriction;
        body.restitution = MathUtils.clampFloat(restitution,0.0f, 1.0f);
        body.ghost = ghost;
        body.bitmask = bitmask;
        body.setMotionState(x, y, angleDeg, velX, velY, velAngleDeg);
        bodiesToAdd.add(body);
        return body;
    }

    // TODO
    @Contract(pure = true)
    @NotNull public Physics2DBody createBodyPolygon(Object owner,
                                             Physics2DBody.MotionType motionType,
                                             float x, float y, float angleDeg,
                                             float velX, float velY, float velAngleDeg,
                                             float density, float staticFriction, float dynamicFriction, float restitution,
                                             boolean ghost, int bitmask,
                                             float[] vertices) {
        Physics2DBody body = bodiesPool.allocate();
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

        body.mass = (body.shape.getArea() * density);
        body.massInv = 1.0f / body.mass;
        body.inertiaInv = 1.0f / calculateMomentOfInertia(body.shape, density);
        body.staticFriction  = staticFriction;
        body.dynamicFriction = dynamicFriction;
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
        Physics2DBody body = bodiesPool.allocate();
        return body;
    }

    @Contract(pure = true)
    @NotNull public Physics2DForceField createForceField(BiConsumer<Physics2DBody, Vector2> forceFunction) {
        Physics2DForceField forceField = new Physics2DForceField(this) {
            @Override
            public void calculateForce(Physics2DBody body, Vector2 out) {
                forceFunction.accept(body, out);
            }
        };
        forceFieldsToAdd.add(forceField);
        return forceField;
    }

    public static float calculateMomentOfInertia(final Shape2D shape, float density) {
        return 1;
    }

    public void destroyBody(final Physics2DBody body) {
        bodiesToRemove.add(body);
        // TODO: remove all body constraints ans joints.
    }

    /* Constraints API */

    @Deprecated public Physics2DConstraintWeld_old createConstraintWeld(Physics2DBody body_a, Physics2DBody body_b, Vector2 anchor) {
        if (body_a == body_b) throw new Physics2DException("Cannot weld object to itself.");
        Physics2DConstraintWeld_old weld = new Physics2DConstraintWeld_old(body_a, body_b, anchor);
        constraintsToAdd.add(weld);
        return null;
    }

    public Physics2DConstraintWeld createConstraintWeld_new(Physics2DBody body_a, Physics2DBody body_b) {
        if (body_a == body_b) throw new Physics2DException("Cannot weld object to itself.");
        Physics2DConstraintWeld weld = new Physics2DConstraintWeld(body_a, body_b);
        constraintsToAdd.add(weld);
        return null;
    }

    public void destroyConstraint(Physics2DConstraint constraint) {
        constraintsToRemove.add(constraint);
    }

    /* Ray casting API */

    public void castRay(final Physics2DWorld.RayHitCallback rayHitCallback, float originX, float originY, float dirX, float dirY) {
        Ray ray = raysPool.allocate();
        ray.originX = originX;
        ray.originY = originY;
        float len = Vector2.len(dirX, dirY);
        boolean zero = MathUtils.isZero(dirX) && MathUtils.isZero(dirY);
        ray.dirX = zero ? 1 : dirX / len;
        ray.dirY = zero ? 0 : dirY / len;
        ray.dst = Float.POSITIVE_INFINITY;
        raysToAdd.put(ray, rayHitCallback);
    }

    public void castRay(final RayHitCallback rayHitCallback, float originX, float originY, float dirX, float dirY, float maxDst) {
        Ray ray = raysPool.allocate();
        ray.originX = originX;
        ray.originY = originY;
        float len = Vector2.len(dirX, dirY);
        boolean zero = MathUtils.isZero(dirX) && MathUtils.isZero(dirY);
        ray.dirX = zero ? 1 : dirX / len;
        ray.dirY = zero ? 0 : dirY / len;
        ray.dst = Math.abs(maxDst);
        raysToAdd.put(ray, rayHitCallback);
    }

    public void castRay(final RayHitCallback rayHitCallback, float originX, float originY, float dirX, float dirY, float maxDst, int bitmask) {

    }

    public void render(a_old_Renderer2D renderer) {
        debugRenderer.render(renderer);
    }

    public interface RayHitCallback {

        void intersected(final Array<Intersection> results);

    }

    public static final class CollisionManifold implements MemoryPool.Reset {

        public Physics2DBody body_a        = null;
        public Physics2DBody body_b        = null;
        public Shape2D       shape_a       = null;
        public Shape2D       shape_b       = null;
        public int           contacts      = 0;
        public float         depth         = 0;
        public Vector2 normal        = new Vector2();
        public Vector2 contactPoint1 = new Vector2();
        public Vector2 contactPoint2 = new Vector2();

        @Override
        public void reset() {
            this.contacts = 0;
        }

    }

    public static final class Cell implements MemoryPool.Reset {

        private final Array<Physics2DBody> bodies = new Array<>(false, 2);

        private boolean active = false;

        public Cell() {}

        @Override
        public void reset() {
            bodies.clear();
            active = false;
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

    public static class Ray implements MemoryPool.Reset {

        public float originX;
        public float originY;
        public float dirX;
        public float dirY;
        public float dst;
        public int   bitmask;

        public Ray() {}

        @Override
        public void reset() {
            dst = Float.POSITIVE_INFINITY;
            bitmask = 0;
        }

    }

    public static class Intersection implements MemoryPool.Reset {

        public Physics2DBody body      = null;
        public Vector2 point     = new Vector2();
        public Vector2 direction = new Vector2();
        public float         dst2      = 0;

        public Intersection() {}

        @Override
        public void reset() {
            body = null;
        }

    }
}

/** TODO: optimized ray casting

 final float x1 = ray.originX;
 final float y1 = ray.originY;
 final float x2 = ray.originX + ray.dst * ray.dirX;
 final float y2 = ray.originY + ray.dst * ray.dirY;

 int x1Cell = (int) Math.floor(x1 / cellWidth);
 int y1Cell = (int) Math.floor(y1 / cellHeight);
 int x2Cell = (int) Math.floor(x2 / cellWidth);
 int y2Cell = (int) Math.floor(y2 / cellHeight);
 int dx = Math.abs(x2Cell - x1Cell);
 int dy = Math.abs(y2Cell - y1Cell);

 int sx = x1Cell < x2Cell ? 1 : -1;
 int sy = y1Cell < y2Cell ? 1 : -1;

 int err = dx - dy;
 int e2;

 int currentX = x1Cell;
 int currentY = y1Cell;

 // optimization: only crossed cells will be considered
 CollectionsArray<Cell> crossedCells = new CollectionsArray<>(false, 32);

 while (true) {
 int col = (int) ((currentX - worldMinX) / cellWidth);
 int row = (int) ((currentY - worldMinY) / cellHeight);
 crossedCells.add(spacePartition.get(row * cols + col));
 crossedCells2.add(spacePartition.get(row * cols + col)); // TODO: delete

 if (currentX == x2Cell && currentY == y2Cell) break;

 e2 = 2 * err;
 if (e2 > -dy) {
 err -= dy;
 currentX += sx;
 }
 if (e2 < dx) {
 err += dx;
 currentY += sy;
 }
 }

 **/