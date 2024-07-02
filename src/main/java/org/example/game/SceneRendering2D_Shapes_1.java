package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.*;
import org.example.engine.core.input.InputKeyboard;
import org.example.engine.core.input.InputMouse;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.math.Vector3;
import org.example.engine.core.memory.MemoryResource;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Shapes_1 extends ApplicationScreen {

    private Renderer2D renderer2D;
    private Camera camera;
    private float red = new Color(1,0,0,1).toFloatBits();
    private float green = new Color(0,1,0,1f).toFloatBits();
    private float blue = new Color(0,0,1,0.5f).toFloatBits();
    private float white = new Color(1,1,1,0.5f).toFloatBits();
    private float yellow = new Color(1,1,0,0.3f).toFloatBits();

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

    float ay = 0;
    @Override
    protected void refresh() {
        Vector3 screen = new Vector3(InputMouse.getCursorX(), InputMouse.getCursorY(), 0);
        camera.lens.unproject(screen);

        if (InputMouse.isButtonPressed(InputMouse.Button.LEFT)) {
            ay++;
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
        GL11.glClearColor(1f,1f,0,1);

        renderer2D.begin(camera);
        renderer2D.setTint(blue);
//        renderer2D.drawCircleFilled(3, 3, 0, 8, 0, 0, 0, 1, 1);
//        renderer2D.drawCircleFilled(3, -3, 0, 8, 0, 0, 0, 1, 1);
//        renderer2D.drawCircleBorder(1, 0.2f,60, 0,-3, ay,0,0,1, 1);
//        //renderer2D.drawCircleFilled(3, -4, 0, 30,25, 0, 0, ay, 1, 1);
//
//        renderer2D.drawCircleThin(1, 20, 0, 0, 0, ay, 0, 1, 1);
//        renderer2D.setTint(red);
//        renderer2D.drawRectangleThin(5,5, 1f, 5,4,-2,0,ay,0,1,1);
//        renderer2D.drawRectangleThin(0,0, 2,0,2,2,0,2);
//        renderer2D.drawCircleBorder(1, 0.2f, 90, 30, 0, 3,ay,0,45,1, 1);
        //renderer2D.setTint(yellow);


        //renderer2D.drawCurveThin(new Vector2(0,0), new Vector2(4,4));
        //renderer2D.drawLineFilled(-3,0,3,0, 8f, 18);
        //renderer2D.drawLineFilled(-3,0,3,3, 0.2f, 18);
        //renderer2D.drawCurveFilled(0.1f,new Vector2(-3,0), new Vector2(0,0), new Vector2(3,0), new Vector2(3,-3), new Vector2(0,-1));


//        renderer2D.setTint(red);
//        renderer2D.drawCircleFilled(0.1f, -0.30868483f,0.0f, 11, 0, 0, 0, 1, 1);
//        renderer2D.setTint(green);
//        renderer2D.drawCircleFilled(0.1f, -0.30868483f,0.5f, 11, 0, 0, 0, 1, 1);
//        renderer2D.setTint(blue);
//        renderer2D.drawCircleFilled(0.1f, 0.21827313f,0.21827313f, 11, 0, 0, 0, 1, 1);
//        renderer2D.setTint(Color.WHITE);
//        renderer2D.drawCircleFilled(0.1f, -0.13528025f,0.5718265f, 11, 0, 0, 0, 1, 1);
//        renderer2D.setTint(Color.BLACK);
//        renderer2D.drawCircleFilled(0.1f, -0.30868483f,0.74523115f, 11, 0, 0, 0, 1, 1);


        //renderer2D.drawCurveFilled(1f, new Vector2(-4,0), new Vector2(0,0), new Vector2(4,4));

        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.W)) dy += GraphicsUtils.getDeltaTime();
        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.S)) dy -= GraphicsUtils.getDeltaTime();

        renderer2D.setTint(new Color(0,0,0,0.2f));

        if (false) {
            // ~
            renderer2D.drawCurveFilled_1(0.6f, 3, new Vector2(-4,4 + dy), new Vector2(0,0), new Vector2(4,4 + dy));

            // V
            //renderer2D.drawCurveFilled(0.6f, 3, new Vector2(-2,4), new Vector2(0,0), new Vector2(2,4));

            // happy
            //renderer2D.drawCurveFilled(0.6f, 3, new Vector2(-4,4), new Vector2(0,0), new Vector2(4,4));

            // sad
            //renderer2D.drawCurveFilled(0.6f, 3, new Vector2(-4,-4), new Vector2(0,0), new Vector2(4,-4));

            // Nike
            //renderer2D.drawCurveFilled(0.6f, 3, new Vector2(-4,-4), new Vector2(0,-4), new Vector2(4,0));

            // ---
            //renderer2D.drawCurveFilled(0.6f, 3, new Vector2(-4,-4), new Vector2(0,0), new Vector2(4,4));
        }
        else {
            //renderer2D.drawCurveFilled(1f, new Vector2(-4, 0), new Vector2(0, 0), new Vector2(4, -4 + dy * 2));

            // V
            renderer2D.drawCurveFilled(0.6f, new Vector2(-2,4), new Vector2(0,0), new Vector2(2,4));

            Array<Vector2> vertices_2 = renderer2D.drawCurveFilled_2(0.6f, 12, new Vector2(-2,4 + dy), new Vector2(0,0), new Vector2(2,4 + dy));
            renderer2D.setTint(Color.BLACK);
            for (int i = 0; i < vertices_2.size; i++) {
                renderer2D.drawCircleFilled(0.05f, vertices_2.get(i).x, vertices_2.get(i).y, 11, 0, 0, 0, 1, 1);
            }
        }
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
