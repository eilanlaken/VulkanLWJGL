package org.example.engine.core.shape;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.Vector2;

// TODO: write unit tests.
public class Shape2DUnion extends Shape2D {

    public final Array<Shape2D> shapes;

    public Shape2DUnion(Shape2D ...shapes) {
        if (shapes == null || shapes.length == 0) throw new IllegalArgumentException("Union must have at least one shape.");
        this.shapes = new Array<>(true, shapes.length);
        for (Shape2D shape : shapes) {
            if (shape instanceof Shape2DUnion) this.shapes.addAll(((Shape2DUnion) shape).shapes);
            else if (shape != null) this.shapes.add(shape);
        }
        this.shapes.pack();
    }

    // TODO: test
    @Override
    protected Vector2 calculateLocalGeometryCenter() {
        Vector2 center = new Vector2();
        float totalArea = 0;
        for (Shape2D shape : shapes) {
            center.add(shape.geometryCenter());
            totalArea += shape.getArea();
        }
        center.scl(1.0f / totalArea);
        return center;
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
    protected Array<Vector2> getWorldVertices() {
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
