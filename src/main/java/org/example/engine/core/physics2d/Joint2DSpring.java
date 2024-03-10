package org.example.engine.core.physics2d;

public class Joint2DSpring extends Joint2D {

    public float l;
    public float k;

    protected Joint2DSpring(final Body2D a, final Body2D b, float l, float k) {
        super(a,b);
        this.l = l;
        this.k = k;
    }

}
