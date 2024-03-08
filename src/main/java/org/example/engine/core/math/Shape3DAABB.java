package org.example.engine.core.math;

// AABB = axis aligned bonding box
public class Shape3DAABB implements Shape3D {

    public Vector3 offset = new Vector3();
    public Vector3 min;
    public Vector3 max;

    public Shape3DAABB(Vector3 min, Vector3 max) {
        this(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public Shape3DAABB(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.min = new Vector3(minX, minY, minZ);
        this.max = new Vector3(maxX, maxY, maxZ);
    }

    public void translate(final Matrix4 transform) {
        transform.getTranslation(offset);
    }

    public Vector3 computeCorner(int i, Vector3 result) {
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
        return "min: " + min + "\nmax: " + max;
    }

    // TODO: implement.
    @Override
    public boolean contains(float x, float y, float z) {
        return false;
    }
}
