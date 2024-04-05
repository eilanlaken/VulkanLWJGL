package org.example.game;

import org.example.engine.core.graphics.*;
import org.example.engine.core.input.Keyboard;
import org.example.engine.core.math.AlgorithmsCollisions2D;
import org.example.engine.core.math.Shape2D;
import org.example.engine.core.math.Shape2DAABB;
import org.example.engine.core.math.Shape2DCircle;
import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_7 extends WindowScreen {

    private Renderer2D renderer2D;
    private Camera camera;

    private Shape2D first;
    private Shape2D second;
    private AlgorithmsCollisions2D.Penetration penetration = new AlgorithmsCollisions2D.Penetration();

    public SceneRendering2D_7() {
        renderer2D = new Renderer2D();
    }

    @Override
    public Map<String, Class<? extends Resource>> getRequiredAssets() {
        Map<String, Class<? extends Resource>> requiredAssets = new HashMap<>();

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
        if (Keyboard.isKeyPressed(Keyboard.Key.A)) dx -= 10;
        if (Keyboard.isKeyPressed(Keyboard.Key.D)) dx += 10;
        if (Keyboard.isKeyPressed(Keyboard.Key.W)) dy += 10;
        if (Keyboard.isKeyPressed(Keyboard.Key.S)) dy -= 10;

        first.dx(dx);
        first.dy(dy);

        if (AlgorithmsCollisions2D.collide(first, second, penetration)) {
            System.out.println("normal: " + penetration.normal);
            System.out.println("depth: " + penetration.depth);
        }



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
