package org.example.engine.core.physics2d_new;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class CollisionPairTest {

    @Test
    void setSets() {
        Physics2DWorldPhaseC.CollisionPair p1 = new Physics2DWorldPhaseC.CollisionPair();
        Physics2DWorldPhaseC.CollisionPair p2 = new Physics2DWorldPhaseC.CollisionPair();
        Set<Physics2DWorldPhaseC.CollisionPair> set1 = new HashSet<>();
        set1.add(p1);
        set1.add(p2);
        Assertions.assertEquals(1, set1.size());

        Physics2DBody b1 = new Physics2DBody();
        Physics2DWorldPhaseC.CollisionPair p3 = new Physics2DWorldPhaseC.CollisionPair(b1, null);
        Physics2DWorldPhaseC.CollisionPair p4 = new Physics2DWorldPhaseC.CollisionPair(null, b1);
        Set<Physics2DWorldPhaseC.CollisionPair> set2 = new HashSet<>();
        set2.add(p3);
        set2.add(p4);
        Assertions.assertEquals(1, set2.size());
    }

    @Test
    void testEquals() {
    }

    @Test
    void testHashCode() {
    }

    @Test
    void reset() {
    }
}