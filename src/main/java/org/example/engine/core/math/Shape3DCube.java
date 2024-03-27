package org.example.engine.core.math;

public class Shape3DCube implements Shape3D {

    // for the purpose of intermediate computations
    private static final Vector3 vector = new Vector3();
    private static final Matrix4 mtx4 = new Matrix4();

    public Matrix4 transform;
    private final Vector3[] vertices;

    public Shape3DCube(float x, float y, float z,
                       float qx, float qy, float qz, float qw,
                       float sizeX, float sizeY, float sizeZ) {
        this.transform = new Matrix4();
        this.transform.setToTranslationRotationScale(x, y, z, qx, qy, qz, qw, sizeX, sizeY, sizeZ);
        this.vertices = new Vector3[8];
        vertices[0] = new Vector3(0.5f,0.5f,0.5f);
        vertices[1] = new Vector3(-0.5f,0.5f,0.5f);
        vertices[2] = new Vector3(-0.5f,-0.5f,0.5f);
        vertices[3] = new Vector3(0.5f,-0.5f,0.5f);
        vertices[4] = new Vector3(0.5f,0.5f,-0.5f);
        vertices[5] = new Vector3(-0.5f,0.5f,-0.5f);
        vertices[6] = new Vector3(-0.5f,-0.5f,-0.5f);
        vertices[7] = new Vector3(0.5f,-0.5f,-0.5f);
    }

    public Vector3 computeCorner(int i, Vector3 result) {
        if (i < 0 || i >= 8) throw new IllegalArgumentException("3D Cube only has 8 corners, index should be between 0 and 7. Provided: " + i);
        result.set(vertices[i]);
        return result.mul(transform);
    }

    @Override
    public boolean contains(float x, float y, float z) {
        mtx4.set(transform);
        mtx4.inv();
        vector.set(x,y,z).mul(mtx4);
        if (vector.x > 0.5f || vector.x < -0.5f) return false;
        if (vector.y > 0.5f || vector.y < -0.5f) return false;
        if (vector.z > 0.5f || vector.z < -0.5f) return false;
        return true;
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
    public void update(Matrix4 m) {

    }
}
