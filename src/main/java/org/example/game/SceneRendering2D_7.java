package org.example.game;

import org.example.engine.core.graphics.*;
import org.example.engine.core.input.InputKeyboard;
import org.example.engine.core.shape.Shape2D;
import org.example.engine.core.shape.Shape2DCircle;
import org.example.engine.core.memory.MemoryResource;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_7 extends WindowScreen {

    private Renderer2D renderer2D;
    private Camera camera;

    private Shape2D first;
    private Shape2D second;

    public SceneRendering2D_7() {
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
        first = new Shape2DCircle(100);
        second = new Shape2DCircle(50);

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

        renderer2D.pushDebugShape(first, null);
        renderer2D.pushDebugShape(second, null);


        //renderBounds(first);
        //renderBounds(second);

        renderer2D.end();


        float dx = 0;
        float dy = 0;
        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.A)) dx -= 10;
        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.D)) dx += 10;
        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.W)) dy += 10;
        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.S)) dy -= 10;

        first.dx(dx);
        first.dy(dy);

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
