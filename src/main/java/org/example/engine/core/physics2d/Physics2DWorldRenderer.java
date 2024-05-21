package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.graphics.GraphicsColor;
import org.example.engine.core.graphics.GraphicsRenderer2D;
import org.example.engine.core.graphics.GraphicsUtils;
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

        // render bodies
        if (world.renderBodies) {
            CollectionsArray<Physics2DBody> bodies = world.allBodies;
            for (Physics2DBody body : bodies) {
                if (body.motionType == Physics2DBody.MotionType.STATIC)       renderer.pushDebugShape(body.shape, TINT_FIXED);
                if (body.motionType == Physics2DBody.MotionType.KINEMATIC)     renderer.pushDebugShape(body.shape, TINT_LOGICAL);
                if (body.motionType == Physics2DBody.MotionType.NEWTONIAN)   renderer.pushDebugShape(body.shape, TINT_NEWTONIAN);
                renderer.pushPolygon(polyCircle, new GraphicsColor(1,0,0,1), body.shape.geometryCenter().x, body.shape.geometryCenter().y, 0,0,0, scaleX, scaleY,null,null);

            }
        }

        // print sections

        // TODO: render velocities

        // TODO: render joints
    }

}
