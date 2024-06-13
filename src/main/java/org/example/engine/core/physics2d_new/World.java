package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.physics2d_new.Body;

public class World {

    // bodies
    private int               bodiesCreated  = 0;
    public  final Array<Body> allBodies      = new Array<>(false, 500);
    private final Array<Body> bodiesToAdd    = new Array<>(false, 100);
    private final Array<Body> bodiesToRemove = new Array<>(false, 500);

    boolean renderBodies = true;

}
