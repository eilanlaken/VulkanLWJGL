package org.example.engine.components;

import org.example.engine.core.math.Matrix4;

public final class ComponentFactory {

    // Transform
    public static ComponentTransform createTransform() {
        return new ComponentTransform(false,0,0,0,0,0,0,1,1,1);
    }

}
