package org.example.game;

import org.example.engine.components.ComponentTransform3D;
import org.example.engine.core.files.AssetLoaderTexture;
import org.example.engine.core.files.FileUtils;
import org.example.engine.core.graphics.*;
import org.example.engine.core.math.Matrix4;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class WindowScreenTest_1 extends WindowScreen {

    // TODO: refactor to use assetLoader.load() etc
    AssetLoaderTexture assetLoaderTexture;

    private Renderer3D renderer3D;
    private ModelBuilder modelBuilder;
    private Model model;
    private Texture texture;
    private ShaderProgram shader;
    private ComponentTransform3D transform3D;


    public WindowScreenTest_1() {
        this.assetLoaderTexture = new AssetLoaderTexture();
        this.renderer3D = new Renderer3D();
        this.modelBuilder = new ModelBuilder();
        final String vertexShaderSrc = FileUtils.getFileContent("assets/shaders/vertex.glsl");
        final String fragmentShaderSrc = FileUtils.getFileContent("assets/shaders/fragment.glsl");
        shader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);
    }

    @Override
    public void show() {
        transform3D = new ComponentTransform3D();

        float[] positions = {
                -0.5f,  0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f,  0.5f, 0f,
        };

        float[] textureCoordinates = {
                0,0,
                0,1,
                1,1,
                1,0
        };

        int[] indices = {
                0,1,3,
                3,1,2
        };

        model = modelBuilder.build(positions, textureCoordinates, indices);
        texture = assetLoaderTexture.load("assets/textures/yellowSquare.png");
        model.texture = texture;

    }

    @Override
    public void update(float delta) {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,0,1);

        Matrix4 transform = transform3D.getMatrix4();

        renderer3D.begin();
        renderer3D.render(model, transform3D.getMatrix4(), shader);
        renderer3D.end();

        // TODO: problem here: anything that has z > 1 or z < -1 is not rendered.
        transform3D.position.z += 0.01f;

        System.out.println("transform: " + transform);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        model.free();
        shader.free();
    }
}
