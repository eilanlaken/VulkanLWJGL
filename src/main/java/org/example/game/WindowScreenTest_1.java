package org.example.game;

import org.example.engine.components.ComponentTransform3D;
import org.example.engine.core.files.AssetLoaderTexture;
import org.example.engine.core.files.FileUtils;
import org.example.engine.core.graphics.*;
import org.example.engine.core.math.Matrix4;
import org.example.engine.core.math.Vector3;
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
    private Camera camera;

    public WindowScreenTest_1() {
        this.assetLoaderTexture = new AssetLoaderTexture();
        this.renderer3D = new Renderer3D();
        this.modelBuilder = new ModelBuilder();
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

        model = modelBuilder.build(positions, textureCoordinates, indices);
        texture = assetLoaderTexture.load("assets/textures/yellowSquare.png");
        model.texture = texture;

        transform3D.matrix4.translate(0,0,-5f);

    }

    @Override
    public void update(float delta) {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,0,1);

        Matrix4 transform = transform3D.matrix4;

        renderer3D.begin();
        renderer3D.render(camera, model, transform3D.matrix4, shader);
        renderer3D.end();

        // TODO: problem here: anything that has z > 1 or z < -1 is not rendered.
        transform3D.matrix4.rotate(Vector3.Y, 1);

        Matrix4 prjTrans = new Matrix4();
        prjTrans.set(camera.lens.projection);
        prjTrans.mul(transform);
        Vector3 v = new Vector3(0.5f,0.5f,0);
        v.mul(prjTrans);
        System.out.println("prj: " + camera.lens.view);
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
