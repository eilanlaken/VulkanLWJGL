package org.example.engine.core.math;

import org.example.engine.core.collections.Array;

// TODO: finish implementing
public class Shape2DCompound extends Shape2D {

    public final Array<Shape2D> islands;
    public final Array<Shape2D> holes;

    public Shape2DCompound(final Array<Shape2D> islands, final Array<Shape2D> holes) {
        for (Shape2D island : islands) {
            if (island instanceof Shape2DCompound) throw new IllegalArgumentException("Trying to construct a " + Shape2DCompound.class.getSimpleName() + " using 1 or more compound shapes is not allowed.");
            island.update();
        }
        for (Shape2D hole: holes) {
            if (hole instanceof Shape2DCompound) throw new IllegalArgumentException("Trying to construct a " + Shape2DCompound.class.getSimpleName() + " using 1 or more compound shapes is not allowed.");
            hole.update();
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
    protected float getUnscaledBoundingRadius() {
        float max = -1.0f;
        for (Shape2D island : islands) {
            float r = island.getUnscaledBoundingRadius();
            max = Math.max(max, r);
        }
        return max;
    }

    @Override
    protected float getUnscaledArea() {
        float area = 0;
        for (Shape2D shape : islands) area += shape.getUnscaledArea();
        for (Shape2D shape2D : holes) area -= shape2D.getUnscaledArea();
        return area;
    }

    @Override
    protected void updateWorldCoordinates() {
        for (Shape2D island : islands) {
            island.transform(x, y, angle, scaleX, scaleY);
            island.update();
        }
        for (Shape2D hole : holes) {
            hole.transform(x, y, angle, scaleX, scaleY);
            hole.update();
        }
    }

}
