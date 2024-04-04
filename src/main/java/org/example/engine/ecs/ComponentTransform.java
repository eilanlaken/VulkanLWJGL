package org.example.engine.ecs;

import org.example.engine.core.math.Matrix4;
import org.example.engine.core.math.Quaternion;

public class ComponentTransform extends Component {

    public static final Category CATEGORY = Category.TRANSFORM;

    public boolean isStatic;
    public float x, y, z;
    public float angleX, angleY, angleZ;
    public float scaleX, scaleY, scaleZ;

    // TODO: maybe this does not belong here.
    public Matrix4 local;
    public Matrix4 world;

    public ComponentTransform(boolean isStatic, float x, float y, float z, float angleX, float angleY, float angleZ, float scaleX, float scaleY, float scaleZ) {
        super(CATEGORY);
        this.isStatic = isStatic;
        this.x = x;
        this.y = y;
        this.z = z;
        this.angleX = angleX;
        this.angleY = angleY;
        this.angleZ = angleZ;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;

        // TODO: maybe remove
        this.local = new Matrix4();
        computeMatrix();
    }

    public Matrix4 computeMatrix() {
        Quaternion r = new Quaternion();
        r.setEulerAnglesRad(angleX, angleY, angleZ);
        return local.setToTranslationRotationScale(x,y,z,r.x,r.y,r.z,r.w,scaleX,scaleY,scaleZ);
    }



}
