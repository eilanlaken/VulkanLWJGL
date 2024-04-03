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
    protected float calculateOriginalBoundingRadius() {
        float max = -1.0f;
        for (Shape2D island : islands) {
            float r = island.calculateOriginalBoundingRadius();
            max = Math.max(max, r);
        }
        return max;
    }

    @Override
    protected void bakeCurrentTransformToLocalCoordinates() {
        // TODO: implement
    }

    @Override
    protected float calculateOriginalArea() {
        float area = 0;
        for (Shape2D shape : islands) area += shape.calculateOriginalArea();
        for (Shape2D shape2D : holes) area -= shape2D.calculateOriginalArea();
        return area;
    }

    @Override
    protected void updateWorldCoordinates() {
        for (Shape2D island : islands) island.update(x, y, angle, scaleX, scaleY);
        for (Shape2D hole : holes) hole.update(x, y, angle, scaleX, scaleY);
    }

}
