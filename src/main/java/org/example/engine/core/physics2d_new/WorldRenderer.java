package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.Color;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.graphics.Renderer2D_new;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.shape.Shape2DPolygon;
import org.example.engine.core.shape.Shape2DSegment;
import org.example.engine.core.shape.ShapeUtils;

public class WorldRenderer {

    private static final float TINT_STATIC     = new Color(1,1,0,1).toFloatBits();
    private static final float TINT_KINEMATIC  = new Color(1,0,1,1).toFloatBits();
    private static final float TINT_NEWTONIAN  = new Color(0,1,1,1).toFloatBits();
    private static final float CONSTRAINT_TINT = new Color(0.9f, 0.1f, 0.3f, 1).toFloatBits();
    private static final float RAY_TINT        = new Color(0.4f, 0.2f, 0.8f, 1).toFloatBits();

    @Deprecated private final Shape2DSegment segment    = new Shape2DSegment(0,0,0,0);
    @Deprecated private final Shape2DPolygon polyCircle = ShapeUtils.createPolygonCircleFilled(1, 10);
    @Deprecated private final Shape2DPolygon polyRect   = ShapeUtils.createPolygonRectangleFilled(1, 1);

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
                Array<BodyCollider> colliders = body.colliders;
                for (BodyCollider collider : colliders) {
                    /* render a circle */
                    if (collider instanceof BodyColliderCircle) {
                        BodyColliderCircle circle = (BodyColliderCircle) collider;
                        float tint = body.motionType == Body.MotionType.STATIC ? TINT_STATIC : body.motionType == Body.MotionType.KINEMATIC ? TINT_KINEMATIC : TINT_NEWTONIAN;
                        Vector2 worldCenter = circle.worldCenter();
                        float r = circle.r;
                        float angleRad = body.aRad;
                        float x1 = worldCenter.x;
                        float y1 = worldCenter.y;
                        float x2 = x1 + r * MathUtils.cosRad(angleRad);
                        float y2 = y1 + r * MathUtils.sinRad(angleRad);
                        renderer.pushThinCircle(r, x1, y1, tint);
                        renderer.pushThinLineSegment(x1,y1,x2,y2,tint);
                        continue;
                    }

                    /* render a rectangle */
                    if (collider instanceof BodyColliderRectangle) {
                        BodyColliderRectangle rectangle = (BodyColliderRectangle) collider;
                        float tint = body.motionType == Body.MotionType.STATIC ? TINT_STATIC : body.motionType == Body.MotionType.KINEMATIC ? TINT_KINEMATIC : TINT_NEWTONIAN;

                        float angleRad = body.aRad;

                        float x0 = rectangle.c0.x;
                        float y0 = rectangle.c0.y;
                        float x1 = rectangle.c1.x;
                        float y1 = rectangle.c1.y;
                        float x2 = rectangle.c2.x;
                        float y2 = rectangle.c2.y;
                        float x3 = rectangle.c3.x;
                        float y3 = rectangle.c3.y;

                        renderer.pushThinRectangle(x0,y0, x1,y1, x2,y2, x3,y3, tint);
                        renderer.pushThinLineSegment(
                                rectangle.worldCenter().x,
                                rectangle.worldCenter().y,
                                rectangle.worldCenter().x + 0.5f * rectangle.width * MathUtils.cosRad(angleRad),
                                rectangle.worldCenter().y + 0.5f * rectangle.width * MathUtils.sinRad(angleRad),
                                tint);
                        continue;
                    }

                    /* render a rectangle */
                    if (collider instanceof BodyColliderPolygon) {
                        BodyColliderPolygon polygon = (BodyColliderPolygon) collider;
                        float tint = body.motionType == Body.MotionType.STATIC ? TINT_STATIC : body.motionType == Body.MotionType.KINEMATIC ? TINT_KINEMATIC : TINT_NEWTONIAN;
                        renderer.pushThinPolygon(polygon.worldVertices(), polygon.indices, tint);
                        continue;
                    }
                }
                // render point at center.
                renderer.pushPolygon(polyCircle, new Color(0.2f,1,1,1), body.x, body.y, 0,0,0, scaleX, scaleY,null,null);
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
