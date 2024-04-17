package org.example.engine.core.shape;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;

// AABB = axis aligned bonding box
public class Shape2DAABB extends Shape2D {

    private final float unscaledArea;
    private final float unscaledBoundingRadius;

    private final MathVector2 localMin;
    private final MathVector2 localMax;
    private final MathVector2 worldMin;
    private final MathVector2 worldMax;

    private CollectionsArray<MathVector2> worldVertices;

    public Shape2DAABB(float x1, float y1, float x2, float y2) {
        float xMin = Math.min(x1, x2);
        float xMax = Math.max(x1, x2);
        float yMin = Math.min(y1, y2);
        float yMax = Math.max(y1, y2);
        this.localMin = new MathVector2(xMin, yMin);
        this.localMax = new MathVector2(xMax, yMax);
        this.worldMin = new MathVector2(localMin);
        this.worldMax = new MathVector2(localMax);
        this.worldVertices = new CollectionsArray<>(true, 4);
        this.worldVertices.addAll(new MathVector2(), new MathVector2(), new MathVector2(), new MathVector2());
        this.unscaledArea = Math.abs(x2 - x1) * Math.abs(y2 - y1);
        float centerX = (localMin.x + localMax.x) * 0.5f;
        float centerY = (localMin.y + localMax.y) * 0.5f;
        float halfDiagonal = MathVector2.dst(localMin, localMax) * 0.5f;
        unscaledBoundingRadius = MathVector2.len(centerX, centerY) + halfDiagonal;
    }

    public Shape2DAABB(float width, float height) {
        this(-width * 0.5f, -height * 0.5f, width * 0.5f, height * 0.5f);
    }

    @Override
    protected boolean containsPoint(float x, float y) {
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
        if (MathUtils.isEqual(scaleX, 1.0f) && MathUtils.isEqual(scaleY, 1.0f)) {
            this.worldMin.set(localMin).add(x, y);
            this.worldMax.set(localMax).add(x, y);
            return;
        }
        float absScaleX = Math.abs(scaleX); // to maintain correct min-max relations
        float absScaleY = Math.abs(scaleY); // to maintain correct min-max relations
        this.worldMin.set(localMin).scl(absScaleX, absScaleY).add(x, y);
        this.worldMax.set(localMax).scl(absScaleX, absScaleY).add(x, y);
    }

    public MathVector2 getWorldMin() {
        if (!updated) update();
        return worldMin;
    }

    public MathVector2 getWorldMax() {
        if (!updated) update();
        return worldMax;
    }

    @Override
    protected CollectionsArray<MathVector2> getWorldVertices() {
        worldVertices.get(0).set(worldMin.x, worldMax.y);
        worldVertices.get(0).set(worldMin);
        worldVertices.get(0).set(worldMax.x, worldMin.y);
        worldVertices.get(0).set(worldMax);
        return worldVertices;
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + ": " + "min: " + worldMin + ", max: " + worldMax + ">";
    }

}