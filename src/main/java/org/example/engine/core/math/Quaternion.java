package org.example.engine.core.math;

// TODO: complete and test
public class Quaternion {

    private static Quaternion tmp1 = new Quaternion(0f,0f,0f,0f);
    private static Quaternion tmp2 = new Quaternion(0f,0f,0f,0f);

    public float x;
    public float y;
    public float z;
    public float w;

    public Quaternion() {
        this(0,0,0,1);
    }
    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion add(final Quaternion q) {
        this.x += q.x;
        this.y += q.y;
        this.z += q.z;
        this.w += q.w;
        return q;
    }

    public Quaternion scl(float s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        this.w *= s;
        return this;
    }

    public Quaternion idt() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 1;
        return this;
    }

    public static float dot(Quaternion a, Quaternion b) {
        return a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w;
    }

    public static Quaternion duplicate(final Quaternion q) {
        return new Quaternion(q.x, q.y, q.z, q.w);
    }



}
