package org.example.engine.core.math;

// AABB = axis aligned bonding box
public class Shape2DAABB extends Shape2D {

    private final float unscaledArea;
    private final float unscaledBoundingRadius;

    private final Vector2 localMin;
    private final Vector2 localMax;
    public Vector2 worldMin;
    public Vector2 worldMax;

    public Shape2DAABB(float x1, float y1, float x2, float y2) {
        this.localMin = new Vector2(x1, y1);
        this.localMax = new Vector2(x2, y2);
        this.worldMin = new Vector2(localMin);
        this.worldMax = new Vector2(localMax);
        this.unscaledArea = Math.abs(x2 - x1) * Math.abs(y2 - y1);
        float localCenterX = (x1 + x2) * 0.5f;
        float localCenterY = (y1 + y1) * 0.5f;
        unscaledBoundingRadius = Vector2.len(localCenterX, localCenterY) + (float) Math.sqrt((x2-x1) * (x2-x1) * 0.25f + (y2-y1) * (y2-y1) * 0.25f);

    }

    public Shape2DAABB(float width, float height) {
        this(-width * 0.5f, -height * 0.5f, width * 0.5f, height * 0.5f);
    }

    @Override
    public boolean contains(float x, float y) {
        return x > worldMin.x && x < worldMax.x && y > worldMin.y && y < worldMax.y;
    }

    @Override
    protected float getUnscaledBoundingRadius() {
        return unscaledBoundingRadius;
    }

    @Override
    protected float getUnscaledArea() {
        return unscaledArea;
    }

    @Override
    public void updateWorldCoordinates() {
        if (angle != 0.0f) throw new IllegalStateException("Cannot rotate an AABB: must remain aligned to axis. angle must remain 0. Current value: angle = " + angle);
        this.worldMin.set(localMin).scl(scaleX, scaleY).add(x, y);
        this.worldMax.set(localMax).scl(scaleX, scaleY).add(x, y);
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + ": " + "min: " + worldMin + ", max: " + worldMax + ">";
    }

}