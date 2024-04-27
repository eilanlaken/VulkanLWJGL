package org.example.engine.core.async;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AsyncUtilsTest {

    @Test
    void sync() {
    }

    @Test
    void getAvailableProcessors() {
        Assertions.assertTrue(AsyncUtils.getAvailableProcessorsNum() > 0);
    }
}