package org.example.engine.core.physics2d_new;

public interface Physics2DCollisionListener {

    default void beginContact(Physics2DBody a, Physics2DBody b) {

    }

    default void endContact(Physics2DBody a, Physics2DBody b) {

    }

    default void preSolve(Physics2DWorld.CollisionManifold manifold) {

    }

    default void solve(Physics2DWorld.CollisionManifold manifold) {
        Physics2DBody a = manifold.a;
        Physics2DBody b = manifold.b;

        System.out.println("a coll with: " + a.collidesWith);
        System.out.println("b coll with: " + b.collidesWith);
    }

    default void postSolve() {

    }

}
