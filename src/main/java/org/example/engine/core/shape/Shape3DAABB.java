package org.example.engine.core.shape;

import org.example.engine.core.math.MathMatrix4;
import org.example.engine.core.math.MathVector3;

// TODO: redo entire Shape2D
public class Shape3DAABB implements Shape3D_old {

    public MathVector3 offset = new MathVector3();
    public MathVector3 min;
    public MathVector3 max;

    public Shape3DAABB(MathVector3 min, MathVector3 max) {
        this(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public Shape3DAABB(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.min = new MathVector3(minX, minY, minZ);
        this.max = new MathVector3(maxX, maxY, maxZ);
    }

    public void translate(final MathMatrix4 transform) {
        transform.getTranslation(offset);
    }

    public MathVector3 computeCorner(int i, MathVector3 result) {
        switch (i) {
            case 0:
                return result.set(min.x + offset.x, min.y + offset.y, min.z + offset.z);
            case 1:
                return result.set(max.x + offset.x, min.y + offset.y, min.z + offset.z);
            case 2:
                return result.set(max.x + offset.x, max.y + offset.y, min.z + offset.z);
            case 3:
                return result.set(min.x + offset.x, max.y + offset.y, min.z + offset.z);
            case 4:
                return result.set(min.x + offset.x, min.y + offset.y, max.z + offset.z);
            case 5:
                return result.set(max.x + offset.x, min.y + offset.y, max.z + offset.z);
            case 6:
                return result.set(min.x + offset.x, max.y + offset.y, max.z + offset.z);
            case 7:
                return result.set(max);
            default:
                throw new IllegalArgumentException("3D Cube only has 8 corners, index should be between 0 and 8. Provided: " + i);
        }
    }

    @Override
    public String toString() {
        return "AABB: <min: " + min + ", max: " + max + ">";
    }

    // TODO: implement.
    @Override
    public boolean contains(float x, float y, float z) {
        return false;
    }

    @Override
    public float getVolume() {
        return 0;
    }

    @Override
    public float getSurfaceArea() {
        return 0;
    }

    @Override
    public void update(MathMatrix4 m) {

    }
}
