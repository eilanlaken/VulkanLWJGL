package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.graphics.GraphicsColor;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.graphics.GraphicsRenderer2D;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.shape.Shape2DPolygon;
import org.example.engine.core.shape.Shape2DSegment;
import org.example.engine.core.shape.ShapeUtils;

public final class Physics2DWorldRenderer {

    private static final GraphicsColor TINT_FIXED     = new GraphicsColor(1,1,0,1);
    private static final GraphicsColor TINT_LOGICAL   = new GraphicsColor(1,0,1,1);
    private static final GraphicsColor TINT_NEWTONIAN = new GraphicsColor(0,1,1,1);
    private static final GraphicsColor TINT_CELL_OFF  = new GraphicsColor(1,0.5f, 0.5f, 0.5f);
    private static final GraphicsColor TINT_CELL_ON   = new GraphicsColor(0,0.5f, 1f, 0.5f);

    private final Shape2DSegment segment    = new Shape2DSegment(0,0,0,0);
    private final Shape2DPolygon polyCircle = ShapeUtils.createPolygonCircleFilled(1, 10);
    private final Shape2DPolygon polyRect   = ShapeUtils.createPolygonRectangleFilled(1, 1);

    private final Physics2DWorld world;

    Physics2DWorldRenderer(final Physics2DWorld world) {
        this.world = world;
    }

    public void render(GraphicsRenderer2D renderer) {
        final float pointPixelRadius = 3;
        float scaleX = renderer.getCurrentCamera().lens.getViewportWidth() * pointPixelRadius / GraphicsUtils.getWindowWidth();
        float scaleY = renderer.getCurrentCamera().lens.getViewportHeight() * pointPixelRadius / GraphicsUtils.getWindowHeight();

        // render broad phase
        if (world.renderBroadPhase) {
            float cellWidth = world.cellWidth;
            float cellHeight = world.cellHeight;

            for (Physics2DWorldPhaseC.Cell cell : world.spacePartition) {
                renderer.pushPolygon(polyRect, TINT_CELL_OFF, cell.x, cell.y, 0, 0, 0, cellWidth, cellHeight, null, null);
            }

            for (Physics2DWorldPhaseC.Cell cell : world.activeCells) {
                renderer.pushPolygon(polyRect, TINT_CELL_ON, cell.x, cell.y, 0, 0, 0, cellWidth, cellHeight, null, null);
            }
        }

        // render bodies
        if (world.renderBodies) {
            CollectionsArray<Physics2DBody> bodies = world.allBodies;
            for (Physics2DBody body : bodies) {
                if (body.motionType == Physics2DBody.MotionType.STATIC)       renderer.pushDebugShape(body.shape, TINT_FIXED);
                if (body.motionType == Physics2DBody.MotionType.KINEMATIC)     renderer.pushDebugShape(body.shape, TINT_LOGICAL);
                if (body.motionType == Physics2DBody.MotionType.NEWTONIAN)   renderer.pushDebugShape(body.shape, TINT_NEWTONIAN);
            }
        }

        // print sections

        // TODO: render constraints

        // TODO: render joints

        // render manifolds
        if (world.renderManifolds) {
            CollectionsArray<Physics2DWorld.CollisionManifold> manifolds = world.collisionManifolds;
            for (Physics2DWorld.CollisionManifold manifold : manifolds) {
                if (manifold.contacts == 0) continue;

                MathVector2 penetration = new MathVector2(manifold.normal).scl(manifold.depth);

                // render first contact point.
                renderer.pushPolygon(polyCircle, new GraphicsColor(1,0,0,1), manifold.contactPoint1.x, manifold.contactPoint1.y, 0,0,0,scaleX,scaleY,null,null);
                segment.localA(manifold.contactPoint1.x, manifold.contactPoint1.y);
                segment.localB(manifold.contactPoint1.x + penetration.x, manifold.contactPoint1.y + penetration.y);
                renderer.pushDebugShape(segment, new GraphicsColor(1,0,1,1));

                // render second contact point
                if (manifold.contacts == 1) continue;
                renderer.pushPolygon(polyCircle, new GraphicsColor(1,0,0,1), manifold.contactPoint2.x, manifold.contactPoint2.y, 0,0,0,scaleX,scaleY,null,null);
                segment.localA(manifold.contactPoint2.x, manifold.contactPoint2.y);
                segment.localB(manifold.contactPoint2.x + penetration.x, manifold.contactPoint2.y + penetration.y);
                renderer.pushDebugShape(segment, new GraphicsColor(1,0,1,1));
            }
        }
    }

}
