package org.example.game;

import org.example.engine.components.Component;
import org.example.engine.components.ComponentGraphicsCamera;
import org.example.engine.components.ComponentTransform;
import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.graphics.*;
import org.example.engine.core.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class WindowScreenTest_Rendering_2D extends WindowScreen {

    private Renderer2D renderer2D;
    private ShaderProgram shader;
    private ComponentTransform transform;
    private ComponentGraphicsCamera camera;

    public WindowScreenTest_Rendering_2D() {
        this.renderer2D = new Renderer2D();

        final String vertexShaderSrc = AssetUtils.getFileContent("assets/shaders/default.vert");
        final String fragmentShaderSrc = AssetUtils.getFileContent("assets/shaders/default.frag");
        this.shader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);

        this.camera = Component.Factory.createCamera2D(20,20);

    }

    @Override
    public void show() {
    }


    @Override
    protected void refresh() {
        // rotate
        if (Keyboard.isKeyPressed(Keyboard.Key.R)) {
            transform.angleX += 0.01f;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.T)) {
            transform.angleY += 0.01f;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.Y)) {
            transform.angleZ += 0.01f;
        }

        // scale
        if (Keyboard.isKeyPressed(Keyboard.Key.KEY_1)) {
            transform.scaleX *= 1.01f;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.KEY_2)) {
            transform.scaleY *= 1.01f;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.KEY_3)) {
            transform.scaleZ *= 1.01f;
        }

        // translate
        if (Keyboard.isKeyPressed(Keyboard.Key.A)) {
            transform.x += 0.1f;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.D)) {
            transform.x -= 0.1f;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.W)) {
            transform.y += 0.1f;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.S)) {
            transform.y -= 0.1f;
        }


        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,0,1);

//        renderer3D.begin(shader);
//        renderer3D.setCamera(camera);
//        renderer3D.end();
    }

    @Override
    public void resize(int width, int height) { }


    @Override
    public void hide() {
        shader.free();
    }

    @Override
    public void free() {

    }

}
