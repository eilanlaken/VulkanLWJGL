package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.graphics.Color;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.graphics.Renderer2D;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.shape.Shape2DPolygon;
import org.example.engine.core.shape.Shape2DSegment;
import org.example.engine.core.shape.ShapeUtils;

public final class Physics2DWorldRenderer {

    private static final Color TINT_FIXED     = new Color(1,1,0,1);
    private static final Color TINT_LOGICAL   = new Color(1,0,1,1);
    private static final Color TINT_NEWTONIAN = new Color(0,1,1,1);
    private static final Color TINT_CELL_OFF  = new Color(1,0.5f, 0.5f, 0.5f);
    private static final Color TINT_CELL_ON   = new Color(0,0.5f, 1f, 0.5f);

    private final Shape2DSegment segment    = new Shape2DSegment(0,0,0,0);
    private final Shape2DPolygon polyCircle = ShapeUtils.createPolygonCircleFilled(1, 10);
    private final Shape2DPolygon polyRect   = ShapeUtils.createPolygonRectangleFilled(1, 1);

    private final Physics2DWorld world;

    Physics2DWorldRenderer(final Physics2DWorld world) {
        this.world = world;
    }

    public void render(Renderer2D renderer) {
        final float pointPixelRadius = 6;
        float scaleX = renderer.getCurrentCamera().lens.getViewportWidth() * pointPixelRadius / GraphicsUtils.getWindowWidth();
        float scaleY = renderer.getCurrentCamera().lens.getViewportHeight() * pointPixelRadius / GraphicsUtils.getWindowHeight();

        // render broad phase
        float cellWidth = world.cellWidth;
        float cellHeight = world.cellHeight;

        for (Physics2DWorldPhaseCBroad.Cell cell : world.spacePartition) {
            renderer.pushPolygon(polyRect, TINT_CELL_OFF, cell.x, cell.y,0,0,0, cellWidth, cellHeight,null,null);
        }

        for (Physics2DWorldPhaseCBroad.Cell cell : world.activeCells) {
            renderer.pushPolygon(polyRect, TINT_CELL_ON, cell.x, cell.y,0,0,0, cellWidth, cellHeight,null,null);
        }

        // render bodies
        CollectionsArray<Physics2DBody> bodies = world.allBodies;
        for (Physics2DBody body : bodies) {
            if (body.motionType == Physics2DBody.MotionType.FIXED)       renderer.pushDebugShape(body.shape, TINT_FIXED);
            if (body.motionType == Physics2DBody.MotionType.LOGICAL)     renderer.pushDebugShape(body.shape, TINT_LOGICAL);
            if (body.motionType == Physics2DBody.MotionType.NEWTONIAN)   renderer.pushDebugShape(body.shape, TINT_NEWTONIAN);
        }

        // TODO: render constraints

        // TODO: render joints

        // render manifolds
        CollectionsArray<Physics2DWorld.CollisionManifold> manifolds = world.collisionManifolds;
        for (Physics2DWorld.CollisionManifold manifold : manifolds) {
            if (manifold.contactsCount == 0) continue;

            MathVector2 penetration = new MathVector2(manifold.normal).scl(manifold.depth);

            // render first contact point.
            renderer.pushPolygon(polyCircle, new Color(1,0,0,1), manifold.contactPoint1.x, manifold.contactPoint1.y, 0,0,0,scaleX,scaleY,null,null);
            segment.localA(manifold.contactPoint1.x, manifold.contactPoint1.y);
            segment.localB(manifold.contactPoint1.x + penetration.x, manifold.contactPoint1.y + penetration.y);
            renderer.pushDebugShape(segment, new Color(1,0,1,1));

            // render second contact point
            if (manifold.contactsCount == 1) continue;
            renderer.pushPolygon(polyCircle, new Color(1,0,0,1), manifold.contactPoint2.x, manifold.contactPoint2.y, 0,0,0,scaleX,scaleY,null,null);
            segment.localA(manifold.contactPoint2.x, manifold.contactPoint2.y);
            segment.localB(manifold.contactPoint2.x + penetration.x, manifold.contactPoint2.y + penetration.y);
            renderer.pushDebugShape(segment, new Color(1,0,1,1));
        }
    }

}
