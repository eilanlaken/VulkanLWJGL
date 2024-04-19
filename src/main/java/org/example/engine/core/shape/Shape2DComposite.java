package org.example.engine.core.shape;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathVector2;

// TODO: write unit tests.
public class Shape2DComposite extends Shape2D {

    public final CollectionsArray<Shape2D> shapes;

    public Shape2DComposite(Shape2D ...shapes) {
        for (Shape2D shape : shapes) {
            if (shape instanceof Shape2DComposite) throw new IllegalArgumentException("Trying to construct a " + Shape2DComposite.class.getSimpleName() + " using 1 or more compound shapes is not allowed.");
        }
        this.shapes = new CollectionsArray<>(true, shapes.length);
        this.shapes.addAll(shapes);
    }

    @Deprecated
    public Shape2DComposite(final CollectionsArray<Shape2D> shapes) {
        for (Shape2D shape : shapes) {
            if (shape instanceof Shape2DComposite) throw new IllegalArgumentException("Trying to construct a " + Shape2DComposite.class.getSimpleName() + " using 1 or more compound shapes is not allowed.");
        }
        this.shapes = shapes;
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        for (Shape2D shape : shapes)
            if (shape.containsPoint(x, y)) return true;
        return false;
    }

    @Override
    protected float calculateUnscaledBoundingRadius() {
        float max = -1.0f;
        for (Shape2D shape : shapes) {
            float r = shape.getUnscaledBoundingRadius();
            max = Math.max(max, r);
        }
        return max;
    }

    @Override
    protected float calculateUnscaledArea() {
        float area = 0;
        for (Shape2D shape : shapes) area += shape.getUnscaledArea();
        return area;
    }

    @Override
    protected CollectionsArray<MathVector2> getWorldVertices() {
        throw new UnsupportedOperationException("Cannot get a world vertices list for " + Shape2DComposite.class.getSimpleName() + ": operation not strictly defined.");
    }

    @Override
    protected void updateWorldCoordinates() {
        for (Shape2D shape : shapes) {
            shape.setTransform(x, y, angle, scaleX, scaleY);
            shape.update();
        }
    }

}
