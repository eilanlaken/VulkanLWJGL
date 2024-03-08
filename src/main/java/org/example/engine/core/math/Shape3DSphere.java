package org.example.engine.core.math;

public class Shape3DSphere implements Shape3D {

    public final Vector3 center;
    public float radius;
    public Vector3 offset;
    public float scaledRadius;

    public Shape3DSphere(Vector3 center, float radius) {
        this.center = new Vector3(center);
        this.radius = radius;
        this.offset = new Vector3();
        this.scaledRadius = radius;
    }

    public Shape3DSphere(float x, float y, float z, float radius) {
        this.center = new Vector3(x,y,z);
        this.radius = radius;
    }

    public void translateAndScale(final Matrix4 transform) {
        transform.getTranslation(offset);
        scaledRadius = radius * transform.getMaxScale();
    }

    public Vector3 computeCenter(Vector3 result) {
        return result.set(center.x + offset.x, center.y + offset.y, center.z + offset.z);
    }

    public Vector3 computeCenter(Matrix4 transform, Vector3 result) {
        transform.getTranslation(result);
        result.add(center);
        return result;
    }

    public String toString() {
        return "center: " + center.toString() + "\nr: " + radius;
    }

    // TODO: revise
    @Override
    public boolean contains(float x, float y, float z) {
        return center.dst2(x,y,z) <= radius * radius;
    }


}
