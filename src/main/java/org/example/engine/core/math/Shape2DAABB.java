package org.example.engine.core.math;

// AABB = axis aligned bonding box
public class Shape2DAABB extends Shape2D {

    private Vector2 localMin;
    private Vector2 localMax;
    public Vector2 worldMin;
    public Vector2 worldMax;

    public Shape2DAABB(float x1, float y1, float x2, float y2) {
        this.localMin = new Vector2(x1, y1);
        this.localMax = new Vector2(x2, y2);
    }

    @Override
    public boolean contains(float x, float y) {
        return x > worldMin.x && x < worldMax.x && y > worldMin.y && y < worldMax.y;
    }

    @Override
    public float getArea() {
        return Math.abs((worldMax.x - worldMin.x) * (worldMax.y - worldMin.y));
    }

    @Override
    public float getPerimeter() {
        return 2 * (Math.abs(worldMax.y - worldMin.y) + Math.abs(worldMax.x - worldMin.x));
    }

    @Override
    public void update() {
        if (angle != 0) throw new IllegalStateException("Cannot rotate an AABB: must remain aligned to axis. angle must remain 0. Current value: angle = " + angle);
        // scale
        float centerX = (localMin.x + localMax.x) * 0.5f;
        float centerY = (localMin.y + localMax.y) * 0.5f;
        this.worldMin.set(localMin);
        this.worldMax.set(localMax);
        this.worldMin.sub(centerX, centerY);
        this.worldMax.sub(centerX, centerY);
        this.worldMin.scl(scaleX, scaleY);
        this.worldMax.scl(scaleX, scaleY);
        this.worldMin.add(centerX, centerY);
        this.worldMax.add(centerX, centerY);
        // "rotate" - AABB so do nothing
        // translate
        worldMin.add(x, y);
        worldMax.add(x,y);
        updated = true;
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + ": " + "min: " + worldMin + ", max: " + worldMax + ">";
    }

}
