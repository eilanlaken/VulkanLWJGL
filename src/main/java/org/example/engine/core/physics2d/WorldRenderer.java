package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.Color;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.graphics.Renderer2D;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;

import java.util.Set;

public class WorldRenderer {

    private static final float TINT_STATIC     = new Color(1,1,0,1).toFloatBits();
    private static final float TINT_KINEMATIC  = new Color(1,0,1,1).toFloatBits();
    private static final float TINT_NEWTONIAN  = new Color(0,1,1,1).toFloatBits();
    private static final float TINT_COM        = new Color(1,0.1f,0.2f,1).toFloatBits();
    private static final float TINT_CONSTRAINT = new Color(0.9f, 0.1f, 0.3f, 1).toFloatBits();
    private static final float TINT_RAY_HIT    = new Color(0.2f,1,1,1).toFloatBits();
    private static final float TINT_RAY        = new Color(0.4f, 0.2f, 0.8f, 1).toFloatBits();

    private final World world;

    WorldRenderer(final World world) {
        this.world = world;
    }

    public void render(Renderer2D renderer) {
        final float pointPixelRadius = 3;
        float scaleX = renderer.getCurrentCamera().lens.getViewportWidth()  * pointPixelRadius / GraphicsUtils.getWindowWidth();
        float scaleY = renderer.getCurrentCamera().lens.getViewportHeight() * pointPixelRadius / GraphicsUtils.getWindowHeight();

        // render bodies
        if (world.renderBodies) {
            Array<Body> bodies = world.allBodies;
            for (Body body : bodies) {
                // render center of mass

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
                        renderer.setTint(tint);
                        renderer.drawCircleThin(r, 15, x1, y1, 0, 0, body.aRad * MathUtils.radiansToDegrees, 1, 1);
                        renderer.drawLineThin(x1,y1,x2,y2);
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
                        renderer.setTint(tint);
                        renderer.drawRectangleThin(x0,y0, x1,y1, x2,y2, x3,y3);
                        renderer.drawLineThin(
                                rectangle.worldCenter().x,
                                rectangle.worldCenter().y,
                                rectangle.worldCenter().x + 0.5f * rectangle.width * MathUtils.cosRad(angleRad + collider.offsetAngleRad),
                                rectangle.worldCenter().y + 0.5f * rectangle.width * MathUtils.sinRad(angleRad + collider.offsetAngleRad));
                        continue;
                    }

                    /* render a polygon */
                    if (collider instanceof BodyColliderPolygon) {
                        BodyColliderPolygon polygon = (BodyColliderPolygon) collider;
                        float tint = body.motionType == Body.MotionType.STATIC ? TINT_STATIC : body.motionType == Body.MotionType.KINEMATIC ? TINT_KINEMATIC : TINT_NEWTONIAN;
                        renderer.setTint(tint);
                        renderer.drawPolygonThin(polygon.vertices, true, body.x, body.y, 0, 0, body.aRad * MathUtils.radiansToDegrees, 1, 1);
                        continue;
                    }
                }
                renderer.setTint(TINT_COM);
                renderer.drawCircleFilled(1, 10, body.cmX, body.cmY, 0,0, body.aRad * MathUtils.radiansToDegrees, scaleX, scaleY);
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
            Set<RayCastingRay> rays = world.allRays.keySet();
            for (RayCastingRay ray : rays) {
                float scl = ray.dst == Float.POSITIVE_INFINITY || Float.isNaN(ray.dst) ? 100 : ray.dst;
                renderer.setTint(TINT_RAY);
                renderer.drawLineThin(ray.originX, ray.originY, ray.originX + scl * ray.dirX, ray.originY + scl * ray.dirY);
            }

            Array<RayCastingIntersection> intersections = world.intersections;
            for (RayCastingIntersection intersection : intersections) {
                Vector2 point = intersection.point;
                renderer.setTint(TINT_RAY_HIT);
                renderer.drawCircleFilled(1, 10, point.x, point.y, 0,0, 0, scaleX, scaleY);
            }
        }

    }

}
