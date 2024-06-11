package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;

public interface RayCastingCallback {

    void intersected(final Array<RayCastingIntersection> results);

}
