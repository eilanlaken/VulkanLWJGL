package org.example.game;

import org.example.engine.core.graphics.*;
import org.example.engine.core.input.InputKeyboard;
import org.example.engine.core.memory.MemoryResource;
import org.example.engine.core.shape.Shape2D;
import org.example.engine.core.shape.Shape2DCircle;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_6 extends WindowScreen {

    private Renderer2D renderer2D;
    private Camera camera;

    private Shape2DCircle circle1;
    private Shape2DCircle circle2;

    public SceneRendering2D_6() {
        renderer2D = new Renderer2D();
    }

    @Override
    public Map<String, Class<? extends MemoryResource>> getRequiredAssets() {
        Map<String, Class<? extends MemoryResource>> requiredAssets = new HashMap<>();

        requiredAssets.put("assets/atlases/pack2_0.png", Texture.class);

        return requiredAssets;
    }

    @Override
    public void show() {
        circle1 = new Shape2DCircle(150, 0,0);
        circle2 = new Shape2DCircle(300, 0,0);

        circle1.xy(-200, 200);
        circle1.update();
        //circle.update();

        //rectangle = new Shape2DRectangle(200,300);
        //rectangle = new Shape2DRectangle(200,-100,200,300,-45);
        //rectangle.angle(30);
        //rectangle.update();

        //aabb = new Shape2DAABB(400,400, 500, 500);
        //aabb.update();

        //segment = new Shape2DSegment(0,0,150,200);
        //segment.update();

        //polygon = new Shape2DPolygon(new float[] {0,0, 200,0, 100,200, -300,200, -400,100});


//        Array<Shape2D> islands = new Array<>();
//        islands.add(circle1);
//        islands.add(polygon);
//
//        Array<Shape2D> holes = new Array<>();
//        holes.add(new Shape2DCircle(90, 0,0));

        //compound = new Shape2DMorphed(islands, holes);

        camera = new Camera(640*2,480*2, 1);
        camera.update();
    }


    @Override
    protected void refresh() {

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);
        renderer2D.begin(camera);

        //polygon.setRotation(time);
        //renderer2D.pushDebugShape(circle, null);
        //renderer2D.pushDebugShape(rectangle, null);
        //renderer2D.pushDebugShape(aabb, null);
        //renderer2D.pushDebugShape(compound, null);
        //renderer2D.pushDebugShape(polygon, null);
        //renderBounds(compound);

        renderer2D.pushDebugShape(circle1, null);
        renderer2D.pushDebugShape(circle2, null);


        renderer2D.end();


        float dx = 0;
        float dy = 0;
        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.A)) dx -= 10;
        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.D)) dx += 10;
        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.W)) dy += 10;
        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.S)) dy -= 10;

        circle2.dx(dx);
        circle2.dy(dy);
        circle2.update();
    }

    private void renderBounds(Shape2D shape2D) {
        float r = shape2D.getBoundingRadius();
        Shape2DCircle bounds = new Shape2DCircle(r, shape2D.x(), shape2D.y());
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



}
