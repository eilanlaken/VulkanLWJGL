package org.example.engine.core.physics2d;

public class Physics2DJointSpring extends Physics2DJoint {

    public float l;
    public float k;

    protected Physics2DJointSpring(final Physics2DBody a, final Physics2DBody b, float l, float k) {
        super(a,b);
        this.l = l;
        this.k = k;
    }

}
