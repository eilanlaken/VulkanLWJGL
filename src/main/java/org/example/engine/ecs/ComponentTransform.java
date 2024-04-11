package org.example.engine.ecs;

import org.example.engine.core.math.MathMatrix4;
import org.example.engine.core.math.MathQuaternion;

public class ComponentTransform extends Component {

    public static final Category CATEGORY = Category.TRANSFORM;

    public boolean isStatic;
    public float x, y, z;
    public float angleX, angleY, angleZ;
    public float scaleX, scaleY, scaleZ;

    // TODO: maybe this does not belong here.
    public MathMatrix4 local;
    public MathMatrix4 world;

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
        this.local = new MathMatrix4();
        computeMatrix();
    }

    public MathMatrix4 computeMatrix() {
        MathQuaternion r = new MathQuaternion();
        r.setEulerAnglesRad(angleX, angleY, angleZ);
        return local.setToTranslationRotationScale(x,y,z,r.x,r.y,r.z,r.w,scaleX,scaleY,scaleZ);
    }



}
