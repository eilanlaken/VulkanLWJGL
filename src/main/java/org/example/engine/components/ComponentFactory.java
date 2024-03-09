package org.example.engine.components;

import org.example.engine.core.math.Matrix4;

public final class ComponentFactory {

    // Transform 2D

    // Transform 3D
    public static ComponentTransform3D_old createTransform3D() {
        return new ComponentTransform3D_old(null);
    }
    public static ComponentTransform3D_old createTransform3D(Matrix4 matrix4) {
        return new ComponentTransform3D_old(matrix4);
    }

}
