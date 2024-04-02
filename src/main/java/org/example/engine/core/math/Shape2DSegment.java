package org.example.engine.core.math;

public class Shape2DSegment extends Shape2D {

    private final Vector2 a;
    private final Vector2 b;
    public Vector2 world_a;
    public Vector2 world_b;

    Vector2 tmp = new Vector2();

    public Shape2DSegment(float x1, float y1, float x2, float y2) {
        this.a = new Vector2(x1, y1);
        this.b = new Vector2(x2, y2);
        this.world_a = new Vector2(a);
        this.world_b = new Vector2(b);
        super.originalBoundingRadius = (float) Math.sqrt((x2-x1) * (x2-x1) * 0.25f + (y2-y1) * (y2-y1) * 0.25f);
    }

    // TODO: make sure to fix all floating points comparisons
    @Override
    public boolean contains(float x, float y) {
        tmp.set(x, y);
        return Vector2.dst(world_a, tmp) + Vector2.dst(tmp, world_b) == Vector2.dst(world_a, world_b);
    }

    @Override
    public float getArea() {
        return 0;
    }

    @Override
    public float getPerimeter() {
        return Vector2.dst(world_a, world_b);
    }

    @Override
    public void update() {
        if (updated) return;
        this.world_a.set(a);
        this.world_b.set(b);
        // scale
        if (scaleX != 1.0f || scaleY != 1.0f) {
            float centerX = (a.x + b.x) * 0.5f;
            float centerY = (a.y + b.y) * 0.5f;
            this.world_a.sub(centerX, centerY);
            this.world_b.sub(centerX, centerY);
            this.world_a.scl(scaleX, scaleY);
            this.world_b.scl(scaleX, scaleY);
            this.world_a.add(centerX, centerY);
            this.world_b.add(centerX, centerY);
        }
        // rotate
        if (angle != 0.0f) {
            this.world_a.rotateDeg(angle);
            this.world_b.rotateDeg(angle);
        }
        // translate
        world_a.add(x, y);
        world_b.add(x,y);
        updated = true;
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + ": " + "a: " + world_a + ", b: " + world_b + ">";
    }

}
