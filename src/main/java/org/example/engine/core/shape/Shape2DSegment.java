package org.example.engine.core.shape;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;

public class Shape2DSegment extends Shape2D {

    private final float unscaledBoundingRadius;

    private final MathVector2 localA;
    private final MathVector2 localB;

    private final MathVector2 worldA;
    private final MathVector2 worldB;
    private final CollectionsArray<MathVector2> worldVertices;

    public Shape2DSegment(float x1, float y1, float x2, float y2) {
        this.localA = new MathVector2(x1, y1);
        this.localB = new MathVector2(x2, y2);
        this.worldA = new MathVector2(localA);
        this.worldB = new MathVector2(localB);
        this.worldVertices = new CollectionsArray<>(true, 2);
        this.worldVertices.addAll(worldA, worldB);
        float centerX = (localA.x + localB.x) * 0.5f;
        float centerY = (localA.y + localB.y) * 0.5f;
        float halfDiagonal = MathVector2.dst(localA, localB) * 0.5f;
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
    protected boolean containsPoint(float x, float y) {
        return MathUtils.isEqual(MathVector2.dst(worldA.x, worldA.y, x, y) + MathVector2.dst(x, y, worldB.x, worldB.y), MathVector2.dst(worldA, worldB));
    }

    @Override
    protected void updateWorldCoordinates() {
        this.worldA.set(localA).scl(scaleX, scaleY);
        this.worldB.set(localB).scl(scaleX, scaleY);
        // rotate
        if (!MathUtils.isZero(angle)) {
            this.worldA.rotateDeg(angle);
            this.worldB.rotateDeg(angle);
        }
        // translate
        worldA.add(x, y);
        worldB.add(x,y);
    }

    public MathVector2 getWorldA() {
        if (!updated) update();
        return worldA;
    }

    public MathVector2 getWorldB() {
        if (!updated) update();
        return worldB;
    }

    @Override
    protected CollectionsArray<MathVector2> getWorldVertices() {
        return worldVertices;
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + ": " + "a: " + worldA + ", b: " + worldB + ">";
    }

}
