package org.example.engine.components;

import org.example.engine.core.math.Matrix4;
import org.example.engine.core.math.Quaternion;
import org.example.engine.core.math.Vector3;

public class ComponentTransform3D {

    public Matrix4 matrix4 = new Matrix4();

    private Vector3 position = new Vector3();
    private Quaternion rotation = new Quaternion();
    private Vector3 scale = new Vector3(1,1,1);



}
