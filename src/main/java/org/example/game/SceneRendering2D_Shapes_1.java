package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.graphics.*;
import org.example.engine.core.input.InputKeyboard;
import org.example.engine.core.input.InputMouse;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.math.Vector3;
import org.example.engine.core.memory.MemoryResource;
import org.example.engine.core.physics2d.Body;
import org.example.engine.core.physics2d.World;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Shapes_1 extends ApplicationScreen {

    private Renderer2D renderer2D;
    private Camera camera;
    private float red = new Color(1,0,0,1).toFloatBits();
    private float blue = new Color(0,0,1,1).toFloatBits();

    private ShaderProgram shaderYellow;

    public SceneRendering2D_Shapes_1() {
        renderer2D = new Renderer2D();
    }

    @Override
    public void show() {
        camera = new Camera(640f/32,480f/32, 1);
        camera.update();
        shaderYellow = AssetStore.get("assets/shaders/graphics-2d-shader-yellow");

    }

    float aY = 0;
    @Override
    protected void refresh() {
        Vector3 screen = new Vector3(InputMouse.getCursorX(), InputMouse.getCursorY(), 0);
        camera.lens.unproject(screen);

        if (InputMouse.isButtonPressed(InputMouse.Button.LEFT)) {
            aY++;
        }

        if (InputMouse.isButtonClicked(InputMouse.Button.RIGHT)) {

        }

        if (InputKeyboard.isKeyJustPressed(InputKeyboard.Key.S)) {

        }

        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.R)) {
            //body_a.applyForce(1,0, body_a.shape.x(), body_a.shape.y() + 0.2f);
        }

        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.SPACE)) {
            //world.createConstraintWeld(body_a, body_b, new Vector2(1,0));
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);

        renderer2D.begin(camera);
        renderer2D.setTint(red);
        //renderer2D.pushCircleBorder(1, 0.2f,0, 0, 40,0,0,1,1, 1, new Color(1,0,0,0.6f).toFloatBits());
        //renderer2D.pushFilledRectangle(3,1,0,0,0,aY,30,1,1, new Color(1,1,0,1).toFloatBits());
        // x -> 400 * MathUtils.sinDeg(x)
        //renderer2D.pushFilledLineSegment(0,0,0,2,0.1f,new Color(1,0,0,1).toFloatBits());
        //renderer2D.pushThinCurve(MathUtils::sinRad, -3, 3, 20, red);
        //renderer2D.pushCurve(MathUtils::sinRad, -3, 3, 20, 0.1f, blue);
        //renderer2D.pushThinCurve(new Vector2[] {new Vector2(0,0), new Vector2(1,1), new Vector2(2,0)}, red);

        //renderer2D.drawCircleFilled(1, 0,0, 15, 0,0,0,1,1, new Color(1,0,0,0.2f).toFloatBits());
        renderer2D.drawCircleThin(1, 0, 0, 78, 0, 0, 0, 1, 1);
        renderer2D.setTint(blue);
        renderer2D.drawCircleThin(1, 2, 0, 15, 0, 0, 0, 1, 1);

        renderer2D.end();
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

    @Override
    public Map<String, Class<? extends MemoryResource>> getRequiredAssets() {
        Map<String, Class<? extends MemoryResource>> requiredAssets = new HashMap<>();

        requiredAssets.put("assets/shaders/graphics-2d-shader-yellow", ShaderProgram.class);

        return requiredAssets;
    }

}
