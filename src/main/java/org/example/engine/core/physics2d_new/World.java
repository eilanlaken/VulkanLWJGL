package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.Renderer2D;
import org.example.engine.core.graphics.a_old_Renderer2D_2;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.physics2d.Physics2DBody;
import org.example.engine.core.physics2d.Physics2DForceField;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class World {

    public static final int DEFAULT_VELOCITY_CONSTRAINT_SOLVER_ITERATIONS = 6;
    public static final int DEFAULT_POSITION_CONSTRAINT_SOLVER_ITERATIONS = 2;

    // memory pools
    final MemoryPool<Body>                   bodiesPool        = new MemoryPool<>(Body.class,10);
    final MemoryPool<CollisionManifold>      manifoldsPool     = new MemoryPool<>(CollisionManifold.class,10);
    final MemoryPool<CollisionPair>          pairsPool         = new MemoryPool<>(CollisionPair.class,5);
    final MemoryPool<CollisionCell>          cellsPool         = new MemoryPool<>(CollisionCell.class,1024);
    final MemoryPool<RayCastingRay>          raysPool          = new MemoryPool<>(RayCastingRay.class,4);
    final MemoryPool<RayCastingIntersection> intersectionsPool = new MemoryPool<>(RayCastingIntersection.class,4);

    // bodies
    private int               bodiesCreated  = 0;
    public  final Array<Body> allBodies      = new Array<>(false, 500);
    private final Array<Body> bodiesToAdd    = new Array<>(false, 100);
    private final Array<Body> bodiesToRemove = new Array<>(false, 500);

    // forces
    public Array<ForceField> allForceFields      = new Array<>(false, 4);
    public Array<ForceField> forceFieldsToAdd    = new Array<>(false, 2);
    public Array<ForceField> forceFieldsToRemove = new Array<>(false, 2);

    // constraints
    public int               velocityIterations  = DEFAULT_VELOCITY_CONSTRAINT_SOLVER_ITERATIONS;
    public int               positionIterations  = DEFAULT_POSITION_CONSTRAINT_SOLVER_ITERATIONS;
    public Array<Constraint> allConstraints      = new Array<>(false, 10);
    public Array<Constraint> constraintsToAdd    = new Array<>(false, 5);
    public Array<Constraint> constraintsToRemove = new Array<>(false, 5);

    // debugger options
    private final WorldRenderer debugRenderer     = new WorldRenderer(this);
    public        boolean       renderBodies      = true;
    public        boolean       renderVelocities  = true;
    public        boolean       renderConstraints = true;
    public        boolean       renderRays        = true;
    public        boolean       renderContacts    = true;

    public void update(float delta) {
        /* add and remove bodies */
        {
            for (Body body : bodiesToRemove) {
                for (Constraint constraint : body.constraints) {
                    destroyConstraint(constraint);
                }
                allBodies.removeValue(body, true);
                bodiesPool.free(body);
            }
            for (Body body : bodiesToAdd) {
                allBodies.add(body);
                body.index = bodiesCreated;
                body.init();
                bodiesCreated++;
            }
            bodiesToRemove.clear();
            bodiesToAdd.clear();
        }

        /* preparation: add and remove force fields */
        {
            allForceFields.removeAll(forceFieldsToRemove, true);
            allForceFields.addAll(forceFieldsToAdd);
            forceFieldsToRemove.clear();
            forceFieldsToAdd.clear();
        }

        /* Euler integration: integrate velocities */
        {
            for (Body body : allBodies) {
                if (body.off) continue;
                if (body.motionType == Body.MotionType.NEWTONIAN) {
                    for (ForceField field : allForceFields) {
                        Vector2 force = new Vector2();
                        field.calculateForce(body, force);
                        body.netForceX += force.x;
                        body.netForceY += force.y;
                    }
                    body.vx += body.invM * delta * body.netForceX;
                    body.vy += body.invM * delta * body.netForceY;
                    body.wRad += body.netTorque * body.invI * delta;
                }

                body.netForceX = 0;
                body.netForceY = 0;
                body.netTorque = 0;
                body.touching.clear();
            }
        }

        /* solve velocity constraints */

        /* integrate positions */

        /* solve position constraints */


    }

    public void render(Renderer2D renderer) {
        debugRenderer.render(renderer);
    }

    @Contract(pure = true)
    @NotNull
    public Body createBody(Object owner,
                                 Body.MotionType motionType,
                                 BodyCollider ...colliders) {
        Body body = bodiesPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;
        for (BodyCollider collider : colliders) {
            collider.body = body;
            body.colliders.add(collider);
        }
        bodiesToAdd.add(body);
        return body;
    }

    @Contract(pure = true)
    @NotNull
    public Body createBodyCircle(Object owner,
                                          Body.MotionType motionType,
                                          float x, float y, float angleDeg,
                                          float vx, float vy, float velAngleDeg,
                                          float density, float staticFriction, float dynamicFriction, float restitution,
                                          boolean ghost, int bitmask,
                                          float radius) {
        Body body = bodiesPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;

        BodyColliderCircle circleCollider = new BodyColliderCircle(density, staticFriction,
                dynamicFriction, restitution, ghost, bitmask, radius, 0, 0, 0);
        circleCollider.body = body;
        body.colliders.add(circleCollider);

        body.x = x;
        body.y = y;
        body.aRad = angleDeg * MathUtils.degreesToRadians;

        body.vx = vx;
        body.vy = vy;
        body.wRad = velAngleDeg * MathUtils.degreesToRadians;

        bodiesToAdd.add(body);
        return body;
    }

    @Contract(pure = true)
    @NotNull
    public Body createBodyCircle(Object owner,
                                 Body.MotionType motionType,
                                 float x, float y, float angleDeg,
                                 float vx, float vy, float velAngleDeg,
                                 float density, float staticFriction, float dynamicFriction, float restitution,
                                 boolean ghost, int bitmask,
                                 float radius, float offsetX, float offsetY) {
        Body body = bodiesPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;

        BodyColliderCircle circleCollider = new BodyColliderCircle(density, staticFriction,
                dynamicFriction, restitution, ghost, bitmask, radius, offsetX, offsetY, 0);
        circleCollider.body = body;
        body.colliders.add(circleCollider);

        body.x = x;
        body.y = y;
        body.aRad = angleDeg * MathUtils.degreesToRadians;

        body.vx = vx;
        body.vy = vy;
        body.wRad = velAngleDeg * MathUtils.degreesToRadians;

        bodiesToAdd.add(body);
        return body;
    }

    // TODO
    @Contract(pure = true)
    @NotNull
    public Body createBodyRectangle(Object owner,
                                 Body.MotionType motionType,
                                 float x, float y, float angleDeg,
                                 float vx, float vy, float velAngleDeg,
                                 float density, float staticFriction, float dynamicFriction, float restitution,
                                 boolean ghost, int bitmask,
                                 float width, float height) {
        Body body = bodiesPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;

        BodyColliderRectangle rectangleCollider = new BodyColliderRectangle(density, staticFriction,
                dynamicFriction, restitution, ghost, bitmask, width, height, 0, 0, 0);
        rectangleCollider.body = body;
        body.colliders.add(rectangleCollider);

        body.x = x;
        body.y = y;
        body.aRad = angleDeg * MathUtils.degreesToRadians;

        body.vx = vx;
        body.vy = vy;
        body.wRad = velAngleDeg * MathUtils.degreesToRadians;

        bodiesToAdd.add(body);
        return body;
    }

    // TODO
    @Contract(pure = true)
    @NotNull
    public Body createBodyRectangle(Object owner,
                                    Body.MotionType motionType,
                                    float x, float y, float angleDeg,
                                    float vx, float vy, float velAngleDeg,
                                    float density, float staticFriction, float dynamicFriction, float restitution,
                                    boolean ghost, int bitmask,
                                    float width, float height, float offsetX, float offsetY, float offsetAngleDeg) {
        Body body = bodiesPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;

        BodyColliderRectangle rectangleCollider = new BodyColliderRectangle(density, staticFriction,
                dynamicFriction, restitution, ghost, bitmask, width, height, offsetX, offsetY, offsetAngleDeg * MathUtils.degreesToRadians);
        rectangleCollider.body = body;
        body.colliders.add(rectangleCollider);

        body.x = x;
        body.y = y;
        body.aRad = angleDeg * MathUtils.degreesToRadians;

        body.vx = vx;
        body.vy = vy;
        body.wRad = velAngleDeg * MathUtils.degreesToRadians;

        bodiesToAdd.add(body);
        return body;
    }

    // TODO
    @Contract(pure = true)
    @NotNull
    public Body createBodyPolygon(Object owner,
                                    Body.MotionType motionType,
                                    float x, float y, float angleDeg,
                                    float vx, float vy, float velAngleDeg,
                                    float density, float staticFriction, float dynamicFriction, float restitution,
                                    boolean ghost, int bitmask,
                                    float[] vertices) {
        Body body = bodiesPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;

        boolean convex = MathUtils.isPolygonConvex(vertices);

        if (!convex) throw new IllegalArgumentException("concave polygons not supported yet.");

        // TODO: see if polygon is concave or with holes. If it is, handle case properly.
        BodyColliderPolygon polygonCollider = new BodyColliderPolygon(density, staticFriction,
                dynamicFriction, restitution, ghost, bitmask, vertices);
        polygonCollider.body = body;
        body.colliders.add(polygonCollider);

        body.x = x;
        body.y = y;
        body.aRad = angleDeg * MathUtils.degreesToRadians;

        body.vx = vx;
        body.vy = vy;
        body.wRad = velAngleDeg * MathUtils.degreesToRadians;

        bodiesToAdd.add(body);
        return body;
    }

    public void destroyConstraint(Constraint constraint) {
        constraintsToRemove.add(constraint);
    }

}
