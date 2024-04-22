package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.graphics.Camera;
import org.example.engine.core.graphics.Color;
import org.example.engine.core.graphics.Renderer2D;
import org.example.engine.core.input.InputKeyboard;
import org.example.engine.core.memory.MemoryResource;
import org.example.engine.core.shape.Shape2D;
import org.example.engine.core.shape.Shape2DCircle;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class ScenePhysics2D_1 extends ApplicationScreen {

    private Renderer2D renderer2D;
    private Camera camera;

    private Shape2D moving;
    private Shape2D[] stale;
    private Color staleTint = new Color(1,0,0,1);

    public ScenePhysics2D_1() {
        renderer2D = new Renderer2D();
    }

    @Override
    public Map<String, Class<? extends MemoryResource>> getRequiredAssets() {
        Map<String, Class<? extends MemoryResource>> requiredAssets = new HashMap<>();
        //requiredAssets.put("assets/atlases/pack2_0.png", Texture.class);
        return requiredAssets;
    }

    @Override
    public void show() {
        moving = new Shape2DCircle(100);
        stale = new Shape2DCircle[4000];
        for (int i = 0; i < stale.length; i++) {
            stale[i] = new Shape2DCircle(150 + 80 * (float) Math.random(), -4096 + 8281 * (float) Math.random(), -4096 + 8281 * (float) Math.random());
        }

        camera = new Camera(640*2,480*2, 1);
        camera.update();
    }


    @Override
    protected void refresh() {

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);
        renderer2D.begin(camera);

        renderer2D.pushDebugShape(moving, null);
        for (int i = 0; i < stale.length; i++) {
            renderer2D.pushDebugShape(stale[i], staleTint);
        }
        //System.out.println("draw calls: " + renderer2D.getDrawCalls());

        renderer2D.end();


        float dx = 0;
        float dy = 0;
        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.A)) dx -= 10;
        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.D)) dx += 10;
        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.W)) dy += 10;
        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.S)) dy -= 10;

        moving.dx(dx);
        moving.dy(dy);

        int hash = hash(moving.x(), moving.y(), 4096);
        //System.out.println(hash);

    }

    public static int hash(float x, float y, int tableSize) {
        int i = (int) Math.floor(x / 2) % 64;
        if (i < 0) i += 64;
        int j = (int) Math.floor(y / 2) % 64;
        if (j < 0) j += 64;
        return i * 64 + j;
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
