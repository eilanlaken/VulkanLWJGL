package org.example.engine.core.physics2d_new;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class Physics2DCollisionPairTest {

    @Test
    void setSets() {
        Physics2DCollisionPair p1 = new Physics2DCollisionPair();
        Physics2DCollisionPair p2 = new Physics2DCollisionPair();
        Set<Physics2DCollisionPair> set1 = new HashSet<>();
        set1.add(p1);
        set1.add(p2);
        Assertions.assertEquals(1, set1.size());

        Physics2DBody b1 = new Physics2DBody();
        Physics2DCollisionPair p3 = new Physics2DCollisionPair(b1, null);
        Physics2DCollisionPair p4 = new Physics2DCollisionPair(null, b1);
        Set<Physics2DCollisionPair> set2 = new HashSet<>();
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