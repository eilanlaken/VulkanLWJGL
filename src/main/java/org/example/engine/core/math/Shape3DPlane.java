package org.example.engine.core.math;

public class Shape3DPlane implements Shape3D {

    public Vector3 normal;
    public float d;

    public Shape3DPlane(Vector3 normal, float d) {
        this.normal = new Vector3(normal);
        this.normal.normalize();
        this.d = d;
    }

    @Override
    public boolean contains(float x, float y, float z) {
        return false;
    }
}
