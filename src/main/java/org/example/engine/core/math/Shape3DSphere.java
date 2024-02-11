package org.example.engine.core.math;

public class Shape3DSphere implements Shape3D {

    public Vector3 center;
    public float radius;

    public Shape3DSphere(Vector3 center, float radius) {
        this.center = new Vector3(center);
        this.radius = radius;
    }

    public Shape3DSphere(float x, float y, float z, float radius) {
        this.center = new Vector3(x,y,z);
        this.radius = radius;
    }

    @Override
    public boolean contains(float x, float y, float z) {
        return center.dst2(x,y,z) <= radius * radius;
    }


}
