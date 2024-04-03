package org.example.engine.core.math;

public class Shape2DSegment extends Shape2D {

    private final Vector2 a;
    private final Vector2 b;
    public Vector2 world_a;
    public Vector2 world_b;

    public Shape2DSegment(float x1, float y1, float x2, float y2) {
        this.a = new Vector2(x1, y1);
        this.b = new Vector2(x2, y2);
        this.world_a = new Vector2(a);
        this.world_b = new Vector2(b);
    }

    @Override
    protected float calculateOriginalBoundingRadius() {
        float x1 = a.x;
        float y1 = a.y;
        float x2 = b.x;
        float y2 = b.y;
        float localCenterX = (x1 + x2) * 0.5f;
        float localCenterY = (y1 + y1) * 0.5f;
        return Vector2.len(localCenterX, localCenterY) + (float) Math.sqrt((x2-x1) * (x2-x1) * 0.25f + (y2-y1) * (y2-y1) * 0.25f);
    }

    @Override
    protected void bakeCurrentTransformToLocalCoordinates() {
        a.set(world_a);
        b.set(world_b);
    }

    @Override
    protected float calculateOriginalArea() {
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
