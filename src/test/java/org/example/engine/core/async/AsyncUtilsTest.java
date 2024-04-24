package org.example.engine.core.async;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AsyncUtilsTest {

    @Test
    void sync() {
    }

    @Test
    void getAvailableProcessors() {
        Assertions.assertTrue(AsyncUtils.getAvailableProcessors() > 0);
    }
}