package org.example.engine.core.physics2d;

public abstract class Joint2D {

    public final Body2D a;
    public final Body2D b;

    protected Joint2D(final Body2D a, final Body2D b) {
        this.a = a;
        this.b = b;
    }

}
