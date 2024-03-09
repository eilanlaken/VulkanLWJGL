package org.example.engine.core.math;

public class Shape3DSphere implements Shape3D {

    public final Vector3 center;
    public final float radius;
    public Vector3 translatedCenter;
    public float scaledRadius;

    public Shape3DSphere(Vector3 center, float radius) {
        this.center = new Vector3(center);
        this.radius = radius;
        this.translatedCenter = new Vector3(this.center);
        this.scaledRadius = radius;
    }

    public Shape3DSphere(float x, float y, float z, float radius) {
        this.center = new Vector3(x,y,z);
        this.radius = radius;
    }

    public void translateAndScale(float x, float y, float z, float scale) {
        translatedCenter.set(center.x + x,center.y + y,center.z + z);
        scaledRadius = radius * scale;
    }

    public String toString() {
        return "Sphere: <center: " + center + ", r: " + radius + ">";
    }

    @Override
    public boolean contains(float x, float y, float z) {
        return translatedCenter.dst2(x,y,z) <= scaledRadius * scaledRadius;
    }


}
