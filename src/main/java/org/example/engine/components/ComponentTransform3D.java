package org.example.engine.components;

import org.example.engine.core.math.Matrix4;
import org.example.engine.core.math.Quaternion;
import org.example.engine.core.math.Vector3;

public class ComponentTransform3D {

    private Matrix4 matrix4 = new Matrix4();

    public Vector3 position = new Vector3();
    public Quaternion rotation = new Quaternion();
    public Vector3 scale = new Vector3(1,1,1);

    // TODO: remove, this is logic and does not belong here.
    public Matrix4 getMatrix4() {
        return matrix4.setToTranslationRotationScale(
                position.x, position.y, position.z,
                rotation.x, rotation.y, rotation.z, rotation.w,
                scale.x, scale.y, scale.z
        );

    }

}
