package org.example.engine.core.physics2d_new;

public final class Physics2DWorldPhaseE implements Physics2DWorldPhase {

    @Override
    public void update(Physics2DWorld world, float delta) {
        for (Physics2DWorld.CollisionManifold manifold : world.collisionManifolds) {
            Physics2DBody a = manifold.a;
            Physics2DBody b = manifold.b;

            a.collidesWith.add(b);
            b.collidesWith.add(a);

            Physics2DCollisionListener listener = world.collisionListener;
            listener.beginContact(a, b);
            listener.preSolve(manifold);
            listener.solve(manifold);
            listener.postSolve();
            listener.endContact(a, b);
        }
    }


}
