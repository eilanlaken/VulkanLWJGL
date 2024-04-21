package org.example.engine.core.shape;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathVector2;

// TODO: write unit tests.
public class Shape2DUnion extends Shape2D {

    public final CollectionsArray<Shape2D> shapes;

    public Shape2DUnion(Shape2D ...shapes) {
        this.shapes = new CollectionsArray<>(true, shapes.length);
        for (Shape2D shape : shapes) {
            if (shape instanceof Shape2DUnion) this.shapes.addAll(((Shape2DUnion) shape).shapes);
            else this.shapes.add(shape);
        }
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        for (Shape2D shape : shapes)
            if (shape.containsPoint(x, y)) return true;
        return false;
    }

    @Override
    protected float calculateUnscaledBoundingRadius() {
        float max = 0.0f;
        for (Shape2D shape : shapes) {
            float r = shape.getUnscaledBoundingRadius();
            max = Math.max(max, r);
        }
        return max;
    }

    @Override
    protected float calculateUnscaledArea() {
        float area = 0.0f;
        for (Shape2D shape : shapes) {
            area += shape.getUnscaledArea();
        }
        return area;
    }

    @Override
    protected CollectionsArray<MathVector2> getWorldVertices() {
        throw new UnsupportedOperationException("Cannot get a world vertices list for " + Shape2DUnion.class.getSimpleName() + ": operation not strictly defined.");
    }

    @Override
    protected void updateWorldCoordinates() {
        for (Shape2D shape : shapes) {
            shape.setTransform(x, y, angle, scaleX, scaleY);
            shape.update();
        }
    }

}
