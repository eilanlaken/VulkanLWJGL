package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.Color;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.graphics.Renderer2D;
import org.example.engine.core.graphics.Renderer2D_new;
import org.example.engine.core.shape.Shape2DPolygon;
import org.example.engine.core.shape.Shape2DSegment;
import org.example.engine.core.shape.ShapeUtils;

public class WorldRenderer {

    private static final float TINT_FIXED      = new Color(1,1,0,1).toFloatBits();
    private static final float TINT_LOGICAL    = new Color(1,0,1,1).toFloatBits();
    private static final float TINT_NEWTONIAN  = new Color(0,1,1,1).toFloatBits();
    private static final float CONSTRAINT_TINT = new Color(0.9f, 0.1f, 0.3f, 1).toFloatBits();
    private static final float RAY_TINT        = new Color(0.4f, 0.2f, 0.8f, 1).toFloatBits();

    private final Shape2DSegment segment    = new Shape2DSegment(0,0,0,0);
    private final Shape2DPolygon polyCircle = ShapeUtils.createPolygonCircleFilled(1, 10);
    private final Shape2DPolygon polyRect   = ShapeUtils.createPolygonRectangleFilled(1, 1);

    private final World world;

    WorldRenderer(final World world) {
        this.world = world;
    }

    public void render(Renderer2D_new renderer) {
        final float pointPixelRadius = 3;
        float scaleX = renderer.getCurrentCamera().lens.getViewportWidth()  * pointPixelRadius / GraphicsUtils.getWindowWidth();
        float scaleY = renderer.getCurrentCamera().lens.getViewportHeight() * pointPixelRadius / GraphicsUtils.getWindowHeight();

        // render bodies
        if (world.renderBodies) {
            Array<Body> bodies = world.allBodies;
            for (Body body : bodies) {

            }
        }

        // TODO: render velocities
        if (world.renderVelocities) {

        }

        // TODO: render joints
        if (world.renderConstraints) {

        }

        // TODO: render contact points
        if (world.renderContacts) {

        }

        // TODO: render rays and ray casting results
        if (world.renderRays) {

        }

    }

}
