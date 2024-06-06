package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.Renderer2D_new;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.memory.MemoryPool;
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
                body.inserted = true;
                body.index = bodiesCreated;
                bodiesCreated++;
            }
            bodiesToRemove.clear();
            bodiesToAdd.clear();
        }
    }

    public void render(Renderer2D_new renderer) {
        debugRenderer.render(renderer);
    }

    // TODO
    @Contract(pure = true)
    @NotNull
    public Body createBodyCircle(Object owner,
                                          Body.MotionType motionType,
                                          float x, float y, float angleDeg,
                                          float velX, float velY, float velAngleDeg,
                                          float density, float staticFriction, float dynamicFriction, float restitution,
                                          boolean ghost, int bitmask,
                                          float radius) {
        Body body = bodiesPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;

        z_BodyCollider_old collider = new z_BodyCollider_old();
        collider.shape = new ShapeCircle(radius, 0,0);
        collider.density = 1;
        collider.body = body;
        body.colliders.add(collider);

        // TODO
        body.mass = Physics2DUtils.calculateTotalMass(null);
        body.massInv = 1.0f / body.mass;
        // TODO
        body.inertia = Physics2DUtils.calculateMomentOfInertia(null);
        body.inertiaInv = 1.0f / body.inertia;
        body.setMotionState(x, y, angleDeg * MathUtils.degreesToRadians, velX, velY, velAngleDeg * MathUtils.degreesToRadians);
        bodiesToAdd.add(body);
        return body;
    }

    public void destroyConstraint(Constraint constraint) {
        constraintsToRemove.add(constraint);
    }

}
