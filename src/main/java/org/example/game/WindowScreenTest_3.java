package org.example.game;

import org.example.engine.components.ComponentFactory;
import org.example.engine.components.ComponentTransform3D;
import org.example.engine.core.files.AssetLoaderTexture;
import org.example.engine.core.files.FileUtils;
import org.example.engine.core.graphics.*;
import org.example.engine.core.input.Keyboard;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Matrix4;
import org.example.engine.core.math.Vector3;
import org.example.engine.core.memory.Pool;
import org.example.engine.core.memory.Pooled;
import org.lwjgl.opengl.GL11;

import java.io.OutputStream;
import java.io.PrintStream;

public class WindowScreenTest_3 extends WindowScreen {

    // TODO: refactor to use assetLoader.load() etc
    AssetLoaderTexture assetLoaderTextures;

    private Renderer3D renderer3D;
    private Model_old modelOld;
    private Texture texture;
    private ShaderProgram shader;
    private ComponentTransform3D transform3D;
    private Camera camera;

    private Matrix4 cameraTransform;

    private Pool<Test> testPool = new Pool<>(Test.class);

    public WindowScreenTest_3() {
        this.assetLoaderTextures = new AssetLoaderTexture();
        this.renderer3D = new Renderer3D();
        final String vertexShaderSrc = FileUtils.getFileContent("assets/shaders/vertex.glsl");
        final String fragmentShaderSrc = FileUtils.getFileContent("assets/shaders/fragment.glsl");
        this.shader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);
        this.camera = new Camera();
    }

    @Override
    public void show() {
        transform3D = ComponentFactory.createTransform3D();

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

        int[] indices = new int[] {
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

        cameraTransform = new Matrix4();

    }


    @Override
    protected void refresh() {
        // Pooling example
        Test test = testPool.grabOne();
        System.out.println(test.x);
        testPool.letGo(test);

        float delta = GraphicsUtils.getDeltaTime();
        // fixed update
        float angularSpeed = 200; // degrees per second
        transform3D.matrix4.rotate(Vector3.X, angularSpeed * delta);

        if (Keyboard.isKeyJustPressed(Keyboard.Key.A))
            GraphicsUtils.enableVSync();
        if (Keyboard.isKeyJustPressed(Keyboard.Key.B))
            GraphicsUtils.disableVSync();

        if (Keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {
            cameraTransform.translate(0,0,-1*delta);
            cameraTransform.rotate(0,0,1,1*delta);

            Vector3 position = new Vector3();
            camera.lens.position.set(cameraTransform.getTranslation(position));

            Vector3 direction = new Vector3(0,0,-1);
            camera.lens.direction.rot(cameraTransform);

            Vector3 up = new Vector3(0,1,0);
            camera.lens.up.rot(cameraTransform);

            camera.lens.update();

            Matrix4 m = new Matrix4(cameraTransform);
            m.inv();
            //camera.lens.view.set(m);
        }

        if (Keyboard.isKeyPressed(Keyboard.Key.LEFT)) {
            camera.lens.up.rotate(1, camera.lens.direction.x,camera.lens.direction.y,camera.lens.direction.z);
            camera.lens.update();
            camera.lens.direction.rotate(1, camera.lens.up.x,camera.lens.up.y,camera.lens.up.z);
            camera.lens.update();

            //camera.lens.position.add(0,0,4*delta);
            //System.out.println("up \n" + camera.lens.up);
            //System.out.println("dir \n" + camera.lens.direction);

            Matrix4 v = new Matrix4(camera.lens.view);
            Matrix4 t = new Matrix4(camera.lens.view);
            t.inv();
            //System.out.println("transform \n" + t);
            //System.out.println("transform inv (view) \n" + camera.lens.view);

            Vector3 up2 = new Vector3(v.val[Matrix4.M00],v.val[Matrix4.M01],v.val[Matrix4.M02]);
            up2.crs(camera.lens.direction);
            //System.out.println("up2 " + up2);
        }

        // frame update
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,0,1);
        renderer3D.begin(shader);
        renderer3D.setCamera(camera);
        renderer3D.draw(modelOld, transform3D.matrix4);
        renderer3D.end();
    }

    @Override
    public void resize(int width, int height) {

    }


    @Override
    public void hide() {
        modelOld.free();
        shader.free();
    }

    @Override
    public void free() {

    }

    public static class Test implements Pooled {

        public int x;

        public Test() {
            reset();
        }

        @Override
        public void reset() {
            x = MathUtils.random(200);
        }
    }
}
