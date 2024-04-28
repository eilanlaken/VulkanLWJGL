package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.graphics.Camera;
import org.example.engine.core.graphics.Color;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.graphics.Renderer2D;
import org.example.engine.core.input.InputKeyboard;
import org.example.engine.core.input.InputMouse;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.math.MathVector3;
import org.example.engine.core.physics2d.Physics2DBody;
import org.example.engine.core.physics2d.Physics2DWorld;
import org.example.engine.core.physics2d.Physics2DWorldCollision;
import org.example.engine.core.shape.*;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
// TODO: switch to newer physics.
public class ScenePhysics2D_4 extends ApplicationScreen {

    private Renderer2D renderer2D;
    private Color staleTint = new Color(1,0,0,1);
    private Camera camera;

    private Shape2D circle;
    private Shape2D otherCircle;
    private Shape2D otherAABB;
    private Shape2D otherRectangle;
    private Shape2D otherPolygon;
    private Shape2D otherMorphed;

    Physics2DWorld world = new Physics2DWorld();
    Physics2DBody body;
    private Shape2DPolygon contactIndicator = ShapeUtils.createPolygonCircleFilled(1, 10);

    public ScenePhysics2D_4() {
        renderer2D = new Renderer2D();
    }

    @Override
    public void show() {
        circle = new Shape2DCircle(1);
        //circle = new Shape2DComposite(new Shape2DCircle(1.5f,-1,0), new Shape2DCircle(1.5f,1,0));
        otherCircle = new Shape2DCircle(1.5f,0,0);
        otherAABB = new Shape2DAABB(4, 2);
        otherRectangle = new Shape2DRectangle(4.5f,2.2f);
        otherPolygon = new Shape2DPolygon(new float[] {2,1,1,2,-1,2,-2,1,-2,-1,-1,-2,1,-2,2,-1});
        //otherPolygon = new Shape2DPolygon(new float[] {-2,0, 0,4, 2,2, 3,-3, 0,1});
        //otherPolygon = new Shape2DPolygon(new float[] { 0, 0, 5, 0, 5, 5, 0, 5, 1, 1, 4, 1, 4, 4, 1, 4 }, new int[] { 4 });


        camera = new Camera(640f/64,480f/64, 1);
        camera.update();

        body = world.createBody(circle, new MathVector2(0,0),0, new MathVector2(0.f, 0));
        //world.createBody(otherCircle, new MathVector2(3,1.5f), 0, new MathVector2(0.f, 0));
        world.createBody(otherCircle, new MathVector2(0, 0), 0, new MathVector2(0.f, 0));
        //world.createBody(otherRectangle, new MathVector2(0,0.0f), 30, new MathVector2(0.f, 0));
        //world.createBody(otherPolygon, new MathVector2(0.0f,0f), 0, new MathVector2(0.f, 0));

    }


    @Override
    protected void refresh() {

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);
        renderer2D.begin(camera);

        renderer2D.pushDebugShape(circle, null);
        //renderer2D.pushDebugShape(otherCircle, staleTint);
        renderer2D.pushDebugShape(otherCircle, staleTint);
        //renderer2D.pushDebugShape(otherRectangle, staleTint);
        //renderer2D.pushDebugShape(otherPolygon, staleTint);

        //renderBounds(composite);
        renderer2D.end();



        world.update(GraphicsUtils.getDeltaTime());
        MathVector3 screen = new MathVector3(InputMouse.getCursorX(), InputMouse.getCursorY(), 0);
        camera.lens.unproject(screen);
        body.setPosition(screen.x, screen.y);

        // render physics 2d debug:
        renderer2D.begin(camera);
        for (Physics2DWorldCollision.Manifold manifold : world.collisionManifolds)
            renderManifold(manifold);
        renderer2D.end();

        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.R)) {
            circle.rot(-1);
        }
    }

    // TODO: refactor out into the physics debug renderer. For now, use to implement correct narrow phase.
    private void renderManifold(Physics2DWorldCollision.Manifold manifold) {
        MathVector2 penetration = new MathVector2(manifold.normal).scl(manifold.depth);

        // calculate points scale
        final float pointPixelRadius = 6;
        float scaleX = camera.lens.getViewportWidth() * pointPixelRadius / GraphicsUtils.getWindowWidth();
        float scaleY = camera.lens.getViewportHeight() * pointPixelRadius / GraphicsUtils.getWindowHeight();

        // render contact points
        // TODO: switch to debug renderer. consider the GraphicsCamera.
        if (manifold.contactPoint1 != null) {
            renderer2D.pushPolygon(contactIndicator, new Color(1,0,0,1), manifold.contactPoint1.x, manifold.contactPoint1.y, 0,0,0,scaleX,scaleY,null,null);
            Shape2DSegment segment = new Shape2DSegment(manifold.contactPoint1.x, manifold.contactPoint1.y, manifold.contactPoint1.x + penetration.x, manifold.contactPoint1.y + penetration.y);
            renderer2D.pushDebugShape(segment, new Color(1,0,1,1));
        }
        if (manifold.contactPoint2 != null) {
            renderer2D.pushPolygon(contactIndicator, new Color(1,0,0,1), manifold.contactPoint2.x, manifold.contactPoint2.y, 0,0,0,scaleX,scaleY,null,null);
            Shape2DSegment segment = new Shape2DSegment(manifold.contactPoint2.x, manifold.contactPoint2.y, manifold.contactPoint2.x + penetration.x, manifold.contactPoint2.y + penetration.y);
            renderer2D.pushDebugShape(segment, new Color(1,0,1,1));
        }
    }

    private void renderBounds(Shape2D shape2D) {
        Shape2DCircle bounds = new Shape2DCircle(shape2D.getBoundingRadius(), shape2D.x(), shape2D.y());
        renderer2D.pushDebugShape(bounds,new Color(1,1,0,1));
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void hide() {
        renderer2D.deleteAll();
    }

    @Override
    public void deleteAll() {

    }


    public static class Cell {

        public static float CELL_SIZE = 10;
        CollectionsArray<Shape2D> shapes = new CollectionsArray<>();

    }

}