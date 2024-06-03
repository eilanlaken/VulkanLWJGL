package org.example.engine.core.math;

import org.example.engine.core.memory.MemoryPool;

public class Transform implements MemoryPool.Reset {

    public float x, y, z;
    public float angleX, angleY, angleZ;
    public float scaleX, scaleY, scaleZ;

    public Transform() {
        origin();
    }

    public void origin() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.angleX = 0;
        this.angleY = 0;
        this.angleZ = 0;
        this.scaleX = 1;
        this.scaleY = 1;
        this.scaleZ = 1;
    }

    @Override
    public void reset() {
        origin();
    }
}
