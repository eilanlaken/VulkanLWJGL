package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.Color;
import org.example.engine.core.graphics.Renderer2D;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.shape.Shape2DPolygon;
import org.example.engine.core.shape.Shape2DSegment;
import org.example.engine.core.shape.ShapeUtils;

import java.util.Set;

public final class Physics2DWorldRenderer {

    // TODO: change renderer to work directly with float bits
    private static final Color TINT_FIXED     = new Color(1,1,0,1);
    private static final Color TINT_LOGICAL   = new Color(1,0,1,1);
    private static final Color TINT_NEWTONIAN = new Color(0,1,1,1);

    private static final float CONSTRAINT_TINT = new Color(0.9f, 0.1f, 0.3f, 1).toFloatBits();

    private static final float RAY_TINT = new Color(0.4f, 0.2f, 0.8f, 1).toFloatBits();

    private final Shape2DSegment segment    = new Shape2DSegment(0,0,0,0);
    private final Shape2DPolygon polyCircle = ShapeUtils.createPolygonCircleFilled(1, 10);
    private final Shape2DPolygon polyRect   = ShapeUtils.createPolygonRectangleFilled(1, 1);

    private final Physics2DWorld world;

    Physics2DWorldRenderer(final Physics2DWorld world) {
        this.world = world;
    }

    public void render(Renderer2D renderer) {
        final float pointPixelRadius = 3;
        float scaleX = renderer.getCurrentCamera().lens.getViewportWidth()  * pointPixelRadius / GraphicsUtils.getWindowWidth();
        float scaleY = renderer.getCurrentCamera().lens.getViewportHeight() * pointPixelRadius / GraphicsUtils.getWindowHeight();

        // render bodies
        if (world.renderBodies) {
            Array<Physics2DBody> bodies = world.allBodies;
            for (Physics2DBody body : bodies) {
                if (body.motionType == Physics2DBody.MotionType.STATIC)    renderer.pushDebugShape(body.shape, TINT_FIXED);
                if (body.motionType == Physics2DBody.MotionType.KINEMATIC) renderer.pushDebugShape(body.shape, TINT_LOGICAL);
                if (body.motionType == Physics2DBody.MotionType.NEWTONIAN) renderer.pushDebugShape(body.shape, TINT_NEWTONIAN);
                renderer.pushPolygon(polyCircle, new Color(1,0,0,1), body.shape.geometryCenter().x, body.shape.geometryCenter().y, 0,0,0, scaleX, scaleY,null,null);

            }
        }

        // print sections

        // TODO: render velocities
        if (world.renderVelocities) {

        }

        // TODO: render joints
        if (world.renderConstraints) {
            for (Physics2DConstraint constraint : world.allConstraints) {
                if (constraint instanceof Physics2DConstraintWeld) {
                    Physics2DConstraintWeld weld = (Physics2DConstraintWeld) constraint;
                    renderer.pushThinLineSegment(weld.body1.shape.x(), weld.body1.shape.y(), weld.body2.shape.x(), weld.body2.shape.y(), CONSTRAINT_TINT);
                }
            }
        }

        // TODO: render contact points
        if (world.renderContacts) {

        }

        // TODO: render rays and ray casting results
        if (world.renderRays) {
            Set<Physics2DWorld.Ray> rays = world.allRays.keySet();
            for (Physics2DWorld.Ray ray : rays) {
                float scl = ray.dst == Float.POSITIVE_INFINITY || Float.isNaN(ray.dst) ? 100 : ray.dst;
                renderer.pushThinLineSegment(ray.originX, ray.originY, ray.originX + scl * ray.dirX, ray.originY + scl * ray.dirY, RAY_TINT);
            }

            Array<Physics2DWorld.Intersection> intersections = world.intersections;
            for (Physics2DWorld.Intersection intersection : intersections) {
                Vector2 point = intersection.point;
                renderer.pushPolygon(polyCircle, new Color(0.2f,1,1,1), point.x, point.y, 0,0,0, scaleX, scaleY,null,null);
            }
        }

    }

}
