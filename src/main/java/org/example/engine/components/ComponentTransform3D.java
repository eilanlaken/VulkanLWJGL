package org.example.engine.components;

import org.example.engine.core.math.Matrix4;
import org.example.engine.core.math.Quaternion;
import org.example.engine.core.math.Vector3;

public class ComponentTransform3D extends Component {

    public static final ComponentCategory CATEGORY = ComponentCategory.TRANSFORM;

    public Matrix4 matrix4 = new Matrix4();

    @Deprecated private Vector3 position = new Vector3();
    @Deprecated private Quaternion rotation = new Quaternion();
    @Deprecated private Vector3 scale = new Vector3(1,1,1);

    protected ComponentTransform3D(Matrix4 matrix4) {
        super(CATEGORY);
        if (matrix4 != null) this.matrix4.set(matrix4);
    }

    @Deprecated public Vector3 getPosition() {
        return matrix4.getTranslation(position);
    }
    @Deprecated public Quaternion getRotation() {
        return matrix4.getRotation(rotation);
    }
    @Deprecated public Vector3 getScale() {
        return matrix4.getScale(scale);
    }

}
