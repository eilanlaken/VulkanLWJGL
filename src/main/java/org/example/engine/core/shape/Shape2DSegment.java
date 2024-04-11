package org.example.engine.core.shape;

import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;

public class Shape2DSegment extends Shape2D {

    private final float unscaledBoundingRadius;

    private final MathVector2 a;
    private final MathVector2 b;
    public MathVector2 world_a;
    public MathVector2 world_b;

    public Shape2DSegment(float x1, float y1, float x2, float y2) {
        this.a = new MathVector2(x1, y1);
        this.b = new MathVector2(x2, y2);
        this.world_a = new MathVector2(a);
        this.world_b = new MathVector2(b);
        float centerX = (a.x + b.x) * 0.5f;
        float centerY = (a.y + b.y) * 0.5f;
        float halfDiagonal = MathVector2.dst(a, b) * 0.5f;
        unscaledBoundingRadius = MathVector2.len(centerX, centerY) + halfDiagonal;
    }

    @Override
    protected float getUnscaledBoundingRadius() {
        return unscaledBoundingRadius;
    }

    @Override
    protected float getUnscaledArea() {
        return 0;
    }

    @Override
    public boolean contains(float x, float y) {
        return MathUtils.isEqual(MathVector2.dst(world_a.x, world_a.y, x, y) + MathVector2.dst(x, y, world_b.x, world_b.y), MathVector2.dst(world_a, world_b));
    }

    @Override
    protected void updateWorldCoordinates() {
        this.world_a.set(a).scl(scaleX, scaleY);
        this.world_b.set(b).scl(scaleX, scaleY);
        // rotate
        if (!MathUtils.isZero(angle)) {
            this.world_a.rotateDeg(angle);
            this.world_b.rotateDeg(angle);
        }
        // translate
        world_a.add(x, y);
        world_b.add(x,y);
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + ": " + "a: " + world_a + ", b: " + world_b + ">";
    }

}
