package org.example.engine.components;

import org.example.engine.core.math.Matrix4;
import org.example.engine.core.math.Quaternion;
import org.example.engine.core.math.Vector3;

public class ComponentTransform3D extends Component {

    public static final ComponentCategory CATEGORY = ComponentCategory.TRANSFORM;

    public float x, y, z;
    public float angleX, angleY, angleZ;
    public float scaleX, scaleY, scaleZ;

    // TODO: maybe this does not belong here.
    private Matrix4 matrix4;

    public ComponentTransform3D(ComponentCategory category, float x, float y, float z, float angleX, float angleY, float angleZ, float scaleX, float scaleY, float scaleZ) {
        super(category);
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
        this.matrix4 = new Matrix4();
    }

}
