package org.example.engine.core.math;

import org.example.engine.core.collections.Array;

public class Shape2DCompound extends Shape2D {

    public final Array<Shape2D> islands;
    public final Array<Shape2D> holes;

    public Shape2DCompound(final Array<Shape2D> islands, final Array<Shape2D> holes) {
        for (Shape2D island : islands) {
            if (island instanceof Shape2DCompound) throw new IllegalArgumentException("Trying to construct a " + Shape2DCompound.class.getSimpleName() + " using 1 or more compound shapes is not allowed.");
        }
        for (Shape2D hole: holes) {
            if (hole instanceof Shape2DCompound) throw new IllegalArgumentException("Trying to construct a " + Shape2DCompound.class.getSimpleName() + " using 1 or more compound shapes is not allowed.");
        }
        this.islands = islands;
        this.holes = holes;
    }

    @Override
    public boolean contains(float x, float y) {
        for (Shape2D shape : islands) {
            if (shape.contains(x, y)) {
                for (Shape2D intersectionShape : holes) {
                    if (intersectionShape.contains(x, y)) break;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected void calculateOriginalBoundingRadius() {
        // TODO: implement?
    }

    @Override
    public float getArea() {
        float area = 0;
        for (Shape2D shape : islands) area += shape.getArea();
        for (Shape2D shape2D : holes) area -= shape2D.getArea();
        return area;
    }

    @Override
    public float getPerimeter() {
        float perimeter = 0;
        for (Shape2D shape : islands) perimeter += shape.getPerimeter();
        for (Shape2D shape : holes) perimeter += shape.getPerimeter();
        return perimeter;
    }

    @Override
    public void update() {
        if (updated) return;
        for (Shape2D island : islands) island.update(x, y, angle, scaleX, scaleY);
        for (Shape2D hole : holes) hole.update(x, y, angle, scaleX, scaleY);
        updated = true;
    }
}
