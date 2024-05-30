package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;

// TODO:
/*
read
https://box2d.org/files/ErinCatto_UnderstandingConstraints_GDC2014.pdf
 */
public interface Physics2DConstraint {

    void getBodies(CollectionsArray<Physics2DBody> out);
    void update(float delta);

}
