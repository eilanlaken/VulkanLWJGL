package org.example.engine.core.graphics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TexturePackerTest {

    @Test
    void alreadyPacked() {
        Set<String> s1 = new HashSet<>();
        s1.add("a");
        s1.add("b");
        s1.add("c");

        Set<String> s2 = new HashSet<>();
        s2.add("b");
        s2.add("c");
        s2.add("a");

        assertEquals(s1, s2);
    }

}