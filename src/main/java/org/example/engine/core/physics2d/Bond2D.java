package org.example.engine.core.physics2d;

public abstract class Bond2D {

    public final Body2D a;
    public final Body2D b;

    protected Bond2D(final Body2D a, final Body2D b) {
        this.a = a;
        this.b = b;
    }

}
