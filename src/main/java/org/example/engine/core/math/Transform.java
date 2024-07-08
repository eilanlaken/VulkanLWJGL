package org.example.engine.core.math;

import org.example.engine.core.memory.MemoryPool;

// TODO: write unit tests.
// look at godot source.
public class Transform implements MemoryPool.Reset {

    public Transform parent = null;

    /* the local transform (relative to the parent) */

    private float local_x          = 0;
    private float local_y          = 0;
    private float local_z          = 0;
    private float local_angleX_deg = 0;
    private float local_angleY_deg = 0;
    private float local_angleZ_deg = 0;
    private float local_scaleX     = 1;
    private float local_scaleY     = 1;
    private float local_scaleZ     = 1;

    /* the calculated world transform values (relative to the origin) */

    private float x          = 0;
    private float y          = 0;
    private float z          = 0;
    private float angleX_deg = 0;
    private float angleY_deg = 0;
    private float angleZ_deg = 0;
    private float scaleX     = 1;
    private float scaleY     = 1;
    private float scaleZ     = 1;

    public Transform() {
        origin();
    }

    public Transform(Transform parent) {
        this.parent = parent;
        origin();
    }

    public void origin() {
        /* reset local coordinates */
        this.local_x = 0;
        this.local_y = 0;
        this.local_z = 0;
        this.local_angleX_deg = 0;
        this.local_angleY_deg = 0;
        this.local_angleZ_deg = 0;
        this.local_scaleX = 1;
        this.local_scaleY = 1;
        this.local_scaleZ = 1;

        /* reset world coordinates */
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.angleX_deg = 0;
        this.angleY_deg = 0;
        this.angleZ_deg = 0;
        this.scaleX = 1;
        this.scaleY = 1;
        this.scaleZ = 1;
    }

    // TODO: implement.
    public void update() {

    }

    @Override
    public void reset() {
        this.parent = null;
        origin();
    }

}
