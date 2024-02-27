package org.example.engine.core.math;

import org.example.engine.core.collections.Array;

public class Shape2DCompound implements Shape2D {

    public Array<Shape2D> shapes;

    public Shape2DCompound(Shape2D ...shapes) {
        this.shapes = new Array<>();
        for (Shape2D shape : shapes) {
            this.shapes.add(shape);
        }
    }

    @Override
    public boolean contains(float x, float y) {
        boolean contains = false;
        for (Shape2D shape : shapes) {
            if (shape.contains(x, y)) return true;
        }
        return false;
    }

    @Override
    public float getArea() {
        float area = 0;
        for (Shape2D shape : shapes) {
            area += shape.getArea();
        }
        return area;
    }

    @Override
    public float getPerimeter() {
        float perimeter = 0;
        for (Shape2D shape : shapes) {
            perimeter += shape.getPerimeter();
        }
        return perimeter;
    }

}
