package org.example.engine.core.shape;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;

// AABB = axis aligned bonding box
public class Shape2DAABB extends Shape2D {

    private final Vector2 localMin;
    private final Vector2 localMax;
    private final Vector2 worldMin;
    private final Vector2 worldMax;

    private Array<Vector2> worldVertices;

    public Shape2DAABB(float x1, float y1, float x2, float y2) {
        float xMin = Math.min(x1, x2);
        float xMax = Math.max(x1, x2);
        float yMin = Math.min(y1, y2);
        float yMax = Math.max(y1, y2);
        this.localMin = new Vector2(xMin, yMin);
        this.localMax = new Vector2(xMax, yMax);
        this.worldMin = new Vector2(localMin);
        this.worldMax = new Vector2(localMax);
        this.worldVertices = new Array<>(true, 4);
        this.worldVertices.addAll(new Vector2(), new Vector2(), new Vector2(), new Vector2());
    }

    public Shape2DAABB(float width, float height) {
        this(-width * 0.5f, -height * 0.5f, width * 0.5f, height * 0.5f);
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        return x > worldMin.x && x < worldMax.x && y > worldMin.y && y < worldMax.y;
    }

    @Override
    protected Vector2 calculateLocalGeometryCenter() {
        Vector2 center = new Vector2();
        center.add(localMin).add(localMax).scl(0.5f);
        return center;
    }

    @Override
    protected float calculateUnscaledBoundingRadius() {
        float centerX = (localMin.x + localMax.x) * 0.5f;
        float centerY = (localMin.y + localMax.y) * 0.5f;
        float halfDiagonal = Vector2.dst(localMin, localMax) * 0.5f;
        return Vector2.len(centerX, centerY) + halfDiagonal;
    }

    @Override
    protected float calculateUnscaledArea() {
        return (localMax.x - localMin.x) * (localMax.y - localMin.y);
    }

    @Override
    public void updateWorldCoordinates() {
        if (!MathUtils.isZero(angle % 90.0f)) throw new ShapeException("Cannot rotate an AABB: must remain aligned to axis. angle must remain 0. Current value: angle = " + angle);
        if (MathUtils.floatsEqual(scaleX, 1.0f) && MathUtils.floatsEqual(scaleY, 1.0f)) {
            this.worldMin.set(localMin).add(x, y);
            this.worldMax.set(localMax).add(x, y);
            return;
        }
        float absScaleX = Math.abs(scaleX); // to maintain correct min-max relations
        float absScaleY = Math.abs(scaleY); // to maintain correct min-max relations
        this.worldMin.set(localMin).scl(absScaleX, absScaleY).add(x, y);
        this.worldMax.set(localMax).scl(absScaleX, absScaleY).add(x, y);
    }

    public Vector2 getWorldMin() {
        if (!updated) update();
        return worldMin;
    }

    public Vector2 getWorldMax() {
        if (!updated) update();
        return worldMax;
    }

    @Override
    protected Array<Vector2> getWorldVertices() {
        worldVertices.get(0).set(worldMin.x, worldMax.y);
        worldVertices.get(1).set(worldMin);
        worldVertices.get(2).set(worldMax.x, worldMin.y);
        worldVertices.get(3).set(worldMax);
        return worldVertices;
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + ": " + "min: " + worldMin + ", max: " + worldMax + ">";
    }

}