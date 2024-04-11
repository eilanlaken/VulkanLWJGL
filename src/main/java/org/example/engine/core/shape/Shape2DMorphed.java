package org.example.engine.core.shape;

import org.example.engine.core.collections.CollectionsArray;

public class Shape2DMorphed extends Shape2D {

    private float unscaledArea;
    private float unscaledBoundingRadius;
    public final CollectionsArray<Shape2D> islands;
    public final CollectionsArray<Shape2D> holes;

    public Shape2DMorphed(final CollectionsArray<Shape2D> islands, final CollectionsArray<Shape2D> holes) {
        for (Shape2D island : islands) {
            if (island instanceof Shape2DMorphed) throw new IllegalArgumentException("Trying to construct a " + Shape2DMorphed.class.getSimpleName() + " using 1 or more compound shapes is not allowed.");
            if (!Shape2D.isTransformIdentity(island)) throw new IllegalStateException("Transform of input island shape to a " + Shape2DMorphed.class.getSimpleName() + " constructor must be identity (x = 0, y = 0, angle = 0, scaleX = 1, scaleY = 1). Got: x = " + island.x + ", y = " + island.y + ", angle = " + island.angle + ", scaleX = " + island.scaleX + ", scaleY = " + island.scaleY);
        }
        for (Shape2D hole: holes) {
            if (hole instanceof Shape2DMorphed) throw new IllegalArgumentException("Trying to construct a " + Shape2DMorphed.class.getSimpleName() + " using 1 or more compound shapes is not allowed.");
            if (!Shape2D.isTransformIdentity(hole)) throw new IllegalStateException("Transform of input island shape to a " + Shape2DMorphed.class.getSimpleName() + " constructor must be identity (x = 0, y = 0, angle = 0, scaleX = 1, scaleY = 1). Got: x = " + hole.x + ", y = " + hole.y + ", angle = " + hole.angle + ", scaleX = " + hole.scaleX + ", scaleY = " + hole.scaleY);
        }
        this.islands = islands;
        this.holes = holes;
        this.unscaledArea = calculateCurrentUnscaledArea();
        this.unscaledBoundingRadius = calculateCurrentUnscaledBoundingRadius();
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

    private float calculateCurrentUnscaledBoundingRadius() {
        float max = -1.0f;
        for (Shape2D island : islands) {
            float r = island.getUnscaledBoundingRadius();
            max = Math.max(max, r);
        }
        return max;
    }

    private float calculateCurrentUnscaledArea() {
        float area = 0;
        for (Shape2D shape : islands) area += shape.getUnscaledArea();
        for (Shape2D shape2D : holes) area -= shape2D.getUnscaledArea();
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
        for (Shape2D island : islands) {
            island.transform(x, y, angle, scaleX, scaleY);
            island.update();
        }
        for (Shape2D hole : holes) {
            hole.transform(x, y, angle, scaleX, scaleY);
            hole.update();
        }
    }

    public void addIsland(Shape2D island) {
        unscaledBoundingRadius = Math.max(unscaledBoundingRadius, island.getBoundingRadius());
        unscaledArea += island.getArea();
        island.transform(x, y, angle, scaleX, scaleY);
        this.islands.add(island);
        forceUpdateBoundingRadius();
        forceUpdateArea();
        update();
    }

    public void addHole(Shape2D hole) {
        unscaledArea -= hole.getArea();
        hole.transform(x, y, angle, scaleX, scaleY);
        this.holes.add(hole);
        forceUpdateArea();
        update();
    }

}
