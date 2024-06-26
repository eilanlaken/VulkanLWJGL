package org.example.engine.core.shape;

import org.example.engine.core.math.Matrix4x4;
import org.example.engine.core.math.Vector3;

// TODO: redo entire Shape2D
public class Shape3DSphere implements Shape3D_old {

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

    @Override
    public float getVolume() {
        return 0;
    }

    @Override
    public float getSurfaceArea() {
        return 0;
    }

    @Override
    public void update(Matrix4x4 m) {

    }
}
