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

public class WindowScreenTest_Lights_1 extends WindowScreen {

    // TODO: refactor to use assetLoader.load() etc
    AssetLoaderTexture assetLoaderTextures;

    private Renderer3D renderer3D;
    private ModelPart modelPart;
    private ShaderProgram shader;
    private ComponentTransform3D transform3D;
    private Camera camera;

    private Matrix4 cameraTransform;

    public WindowScreenTest_Lights_1() {
        this.assetLoaderTextures = new AssetLoaderTexture();
        this.renderer3D = new Renderer3D();
        final String vertexShaderSrc = FileUtils.getFileContent("assets/shaders/2_vertex.glsl");
        final String fragmentShaderSrc = FileUtils.getFileContent("assets/shaders/2_fragment.glsl");
        this.shader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);
        this.camera = new Camera();
    }

    @Override
    public void show() {
        transform3D = ComponentFactory.createTransform3D();

        modelPart = ModelBuilder.createRedCube();

        transform3D.matrix4.translate(0,0,-5f);

        cameraTransform = new Matrix4();

    }


    @Override
    protected void refresh() {
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
        GL11.glClearColor(0,0,0,1);
        renderer3D.begin(shader);
        renderer3D.setCamera(camera);
        renderer3D.draw(modelPart, transform3D.matrix4);
        renderer3D.end();
    }

    @Override
    public void resize(int width, int height) {

    }


    @Override
    public void hide() {
        shader.free();
    }

    @Override
    public void free() {

    }

}
