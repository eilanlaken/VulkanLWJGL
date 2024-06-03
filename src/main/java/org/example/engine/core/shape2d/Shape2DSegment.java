package org.example.engine.core.shape2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;

public class Shape2DSegment extends Shape2D {

    private final Vector2 localA;
    private final Vector2 localB;
    private final Vector2 worldA;
    private final Vector2 worldB;

    private final Array<Vector2> worldVertices;

    public Shape2DSegment(float x1, float y1, float x2, float y2) {
        this.localA = new Vector2(x1, y1);
        this.localB = new Vector2(x2, y2);
        this.worldA = new Vector2(localA);
        this.worldB = new Vector2(localB);
        this.worldVertices = new Array<>(true, 2);
        this.worldVertices.addAll(worldA, worldB);
    }

    @Override
    protected Vector2 calculateLocalGeometryCenter() {
        Vector2 center = new Vector2();
        center.add(localB).scl(0.5f);
        center.add(localA);
        return center;
    }

    @Override
    protected float calculateUnscaledBoundingRadius() {
        float  centerX = (localA.x + localB.x) * 0.5f;
        float  centerY = (localA.y + localB.y) * 0.5f;
        float  halfDiagonal = Vector2.dst(localA, localB) * 0.5f;
        return Vector2.len(centerX, centerY) + halfDiagonal;
    }

    @Override
    protected float calculateUnscaledArea() {
        return 0;
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        return MathUtils.floatsEqual(Vector2.dst(worldA.x, worldA.y, x, y) + Vector2.dst(x, y, worldB.x, worldB.y), Vector2.dst(worldA, worldB));
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

    public void localA(float x, float y) {
        this.localA.set(x, y);
        updated = false;
    }

    public void localB(float x, float y) {
        this.localB.set(x, y);
        updated = false;
    }

    public Vector2 localA() {
        return localA;
    }

    public Vector2 localB() {
        return localB;
    }

    public Vector2 worldA() {
        if (!updated) update();
        return worldA;
    }

    public Vector2 worldB() {
        if (!updated) update();
        return worldB;
    }

    @Override
    protected Array<Vector2> getWorldVertices() {
        return worldVertices;
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + ": " + "a: " + worldA + ", b: " + worldB + ">";
    }

}
