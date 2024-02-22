package org.example.game;

import org.example.engine.components.ComponentTransform3D;
import org.example.engine.core.application.WindowScreen;
import org.example.engine.core.files.AssetLoaderTexture;
import org.example.engine.core.files.FileUtils;
import org.example.engine.core.graphics.*;
import org.example.engine.core.input.Mouse;
import org.example.engine.core.math.Vector3;
import org.lwjgl.opengl.GL11;

public class WindowScreenTest_2 extends WindowScreen {

    // TODO: refactor to use assetLoader.load() etc
    AssetLoaderTexture assetLoaderTextures;

    private Renderer3D renderer3D;
    private Model_old modelOld;
    private Texture texture;
    private ShaderProgram shader;
    private ComponentTransform3D transform3D;
    private Camera camera;

    public WindowScreenTest_2() {
        this.assetLoaderTextures = new AssetLoaderTexture();
        this.renderer3D = new Renderer3D();
        final String vertexShaderSrc = FileUtils.getFileContent("assets/shaders/vertex.glsl");
        final String fragmentShaderSrc = FileUtils.getFileContent("assets/shaders/fragment.glsl");
        this.shader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);
        this.camera = new Camera();
    }

    @Override
    public void show() {
        transform3D = new ComponentTransform3D();

        float[] positions = new float[] {
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
        };

        float[] textureCoordinates = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.0f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };

        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2,
                8, 10, 11, 9, 8, 11,
                12, 13, 7, 5, 12, 7,
                14, 15, 6, 4, 14, 6,
                16, 18, 19, 17, 16, 19,
                4, 6, 7, 5, 4, 7,
        };

        modelOld = ModelBuilder.build(positions, textureCoordinates, indices);
        texture = assetLoaderTextures.load("assets/textures/yellowSquare.png");
        modelOld.texture = texture;

        transform3D.matrix4.translate(0,0,-5f);
    }

    @Override
    public void frameUpdate(float delta) {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,0,1);
        renderer3D.begin(shader);
        renderer3D.setCamera(camera);
        renderer3D.draw(modelOld, transform3D.matrix4);
        renderer3D.end();

        if (Mouse.isButtonPressed(Mouse.Button.LEFT)) System.out.println("pressed");
    }

    @Override
    public void fixedUpdate(float delta) {
        float angularSpeed = 200; // degrees per second
        transform3D.matrix4.rotate(Vector3.X, angularSpeed * delta);

    }

    @Override
    public void resize(int width, int height) {
        System.out.println("Resized called.");
        System.out.println(width);
        System.out.println(height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        modelOld.free();
        shader.free();
    }
}
