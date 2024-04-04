package org.example.engine.core.math;

public class Shape2DSegment extends Shape2D {

    private final float unscaledBoundingRadius;

    private final Vector2 a;
    private final Vector2 b;
    public Vector2 world_a;
    public Vector2 world_b;

    public Shape2DSegment(float x1, float y1, float x2, float y2) {
        this.a = new Vector2(x1, y1);
        this.b = new Vector2(x2, y2);
        this.world_a = new Vector2(a);
        this.world_b = new Vector2(b);
        this.unscaledBoundingRadius = a.len() + Vector2.dst(a, b);
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
        return MathUtils.isEqual(Vector2.dst(world_a.x, world_a.y, x, y) + Vector2.dst(x, y, world_b.x, world_b.y), Vector2.dst(world_a, world_b));
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
