package org.example.engine.core.shape;

import org.example.engine.core.collections.CollectionsArray;

public class Shape2DComposite extends Shape2D {

    private float unscaledArea;
    private float unscaledBoundingRadius;
    public final CollectionsArray<Shape2D> shapes;

    public Shape2DComposite(final CollectionsArray<Shape2D> shapes) {
        for (Shape2D shape : shapes) {
            if (shape instanceof Shape2DComposite) throw new IllegalArgumentException("Trying to construct a " + Shape2DComposite.class.getSimpleName() + " using 1 or more compound shapes is not allowed.");
            if (!Shape2D.isTransformIdentity(shape)) throw new IllegalStateException("Transform of input island shape to a " + Shape2DComposite.class.getSimpleName() + " constructor must be identity (x = 0, y = 0, angle = 0, scaleX = 1, scaleY = 1). Got: x = " + shape.x + ", y = " + shape.y + ", angle = " + shape.angle + ", scaleX = " + shape.scaleX + ", scaleY = " + shape.scaleY);
        }
        this.shapes = shapes;
        this.unscaledArea = calculateCurrentUnscaledArea();
        this.unscaledBoundingRadius = calculateCurrentUnscaledBoundingRadius();
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        for (Shape2D shape : shapes)
            if (shape.containsPoint(x, y)) return true;
        return false;
    }

    private float calculateCurrentUnscaledBoundingRadius() {
        float max = -1.0f;
        for (Shape2D shape : shapes) {
            float r = shape.getUnscaledBoundingRadius();
            max = Math.max(max, r);
        }
        return max;
    }

    private float calculateCurrentUnscaledArea() {
        float area = 0;
        for (Shape2D shape : shapes) area += shape.getUnscaledArea();
        return area;
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
    protected void updateWorldCoordinates() {
        for (Shape2D shape : shapes) {
            shape.setTransform(x, y, angle, scaleX, scaleY);
            shape.update();
        }
    }

}
