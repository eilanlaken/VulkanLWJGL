package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.graphics.*;
import org.example.engine.core.input.Keyboard;
import org.example.engine.core.math.Matrix4x4;
import org.example.engine.core.memory.MemoryResource;
import org.example.engine.ecs.Component;
import org.example.engine.ecs.ComponentTransform;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering3D_1 extends ApplicationScreen {

    private Renderer3D renderer3DOld;
    private Model model;
    private ShaderProgram shader;
    private ComponentTransform transform;
    private Camera camera;


    public SceneRendering3D_1() {
        this.renderer3DOld = new Renderer3D();

        final String vertexShaderSrc = AssetUtils.getFileContent("assets/shaders/default.vert");
        final String fragmentShaderSrc = AssetUtils.getFileContent("assets/shaders/default.frag");
        this.shader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);


        this.camera = new Camera(100, 100, 1, 0.1f, 100, 70);

    }

    @Override
    public Map<String, Class<? extends MemoryResource>> getRequiredAssets() {
        Map<String, Class<? extends MemoryResource>> requiredAssets = new HashMap<>();

        requiredAssets.put("assets/models/cube-blue.fbx", Model.class);

        return requiredAssets;
    }

    @Override
    public void show() {
        transform = Component.createTransform();
        transform.z = -15;
        model = AssetStore.get("assets/models/cube-blue.fbx");
        System.out.println(model.parts[0].material.uniformParams);
        //environment.add(new EnvironmentLightAmbient(0.2f,0.1f,11.1f,0.2f));
        //transform3D.matrix4.rotateSelfAxis(Vector3.Y, 30);
    }


    @Override
    protected void refresh() {
        float delta = GraphicsUtils.getDeltaTime();
        float angularSpeed = 200; // degrees per second

        Matrix4x4 m = transform.computeMatrix();
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
        GL11.glClearColor(0,0,0,1);
        renderer3DOld.begin(shader);
        renderer3DOld.setCamera(camera);
        renderer3DOld.draw(model.parts[0], transform);
        //renderer3DOld.draw(model.parts[1], transform3D.matrix4);
        renderer3DOld.end();
    }

    @Override
    public void resize(int width, int height) { }


    @Override
    public void hide() {
        shader.delete();
    }

    @Override
    public void deleteAll() {

    }

}
