package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.graphics.*;
import org.example.engine.core.input.Keyboard;
import org.example.engine.core.input.Mouse;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.math.Vector3;
import org.example.engine.core.memory.MemoryResource;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Shapes_2 extends ApplicationScreen {

    private Renderer2D renderer2D;
    private Camera camera;
    private float red = new Color(1,0,0,1).toFloatBits();
    private float green = new Color(0,1,0,1f).toFloatBits();
    private float blue = new Color(0,0,1,0.5f).toFloatBits();
    private float white = new Color(1,1,1,0.5f).toFloatBits();
    private float yellow = new Color(1,1,0,0.3f).toFloatBits();

    private ShaderProgram shaderYellow;

    public SceneRendering2D_Shapes_2() {
        renderer2D = new Renderer2D();
    }

    @Override
    public void show() {
        camera = new Camera(640f/32,480f/32, 1);
        camera.update();
        shaderYellow = AssetStore.get("assets/shaders/graphics-2d-shader-yellow");

    }

    float ay = 0;
    @Override
    protected void refresh() {
        Vector3 screen = new Vector3(Mouse.getCursorX(), Mouse.getCursorY(), 0);
        camera.lens.unproject(screen);

        Vector2[] vs = new Vector2[3];
        vs[0] = new Vector2(-2,-2);
        vs[1] = new Vector2(0,0);
        vs[2] = new Vector2(2,0);
        for (Vector2 v : vs) {
            v.rotateRad(dy);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.W)) dy += GraphicsUtils.getDeltaTime();
        if (Keyboard.isKeyPressed(Keyboard.Key.S)) dy -= GraphicsUtils.getDeltaTime();

        if (Mouse.isButtonPressed(Mouse.Button.LEFT)) {
            ay++;
        }


        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1f,1f,0,1);

        renderer2D.begin(camera);
        renderer2D.setTint(blue);
        //renderer2D.drawCurveFilled(0.6f, 10, vs);

        // TODO: FIX HERE
        //renderer2D.drawCurveFilled(x -> MathUtils.sinRad(x+dy) + 4,0.2f, -4, 4, 10, 10);
        //renderer2D.drawCurveFilled_broken2(x -> MathUtils.sinRad(x+dy) + 4,0.2f, -4, 4, 8);
        //renderer2D.drawCurveFilled_broken(x -> MathUtils.sinRad(x+dy) + 4,0.2f, -4, 4, 10, 10);




        renderer2D.end();
    }

    float dy = 0;

    @Override
    public void resize(int width, int height) { }

    @Override
    public void hide() {
        renderer2D.deleteAll();
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public Map<String, Class<? extends MemoryResource>> getRequiredAssets() {
        Map<String, Class<? extends MemoryResource>> requiredAssets = new HashMap<>();

        requiredAssets.put("assets/shaders/graphics-2d-shader-yellow", ShaderProgram.class);

        return requiredAssets;
    }

}
