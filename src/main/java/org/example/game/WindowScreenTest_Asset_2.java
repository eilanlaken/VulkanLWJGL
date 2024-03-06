package org.example.game;

import org.example.engine.components.ComponentFactory;
import org.example.engine.components.ComponentTransform3D;
import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.graphics.*;
import org.example.engine.core.input.Keyboard;
import org.example.engine.core.math.Matrix4;
import org.example.engine.core.math.Vector3;
import org.lwjgl.opengl.GL11;

public class WindowScreenTest_Asset_2 extends WindowScreen {

    private Renderer3D renderer3D;
    private Model model;
    private ShaderProgram shader;
    private ComponentTransform3D transform3D;
    private Camera camera;
    private Environment environment;
    private Matrix4 cameraTransform;

    // debug
    private BlenderCameraController cameraController;

    public WindowScreenTest_Asset_2() {
        this.renderer3D = new Renderer3D();

        final String vertexShaderSrc = AssetUtils.getFileContent("assets/shaders/simple_3.vert");
        final String fragmentShaderSrc = AssetUtils.getFileContent("assets/shaders/simple_3.frag");
        this.shader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);


        this.camera = new Camera();
        this.environment = new Environment();

        cameraController = new BlenderCameraController(camera);
    }

    @Override
    public void show() {

        transform3D = ComponentFactory.createTransform3D();

        model = AssetStore.get("assets/models/cube-blue.fbx");

        transform3D.matrix4.translateSelfAxis(0,0,-15f);

        cameraTransform = new Matrix4();

        //environment.add(new EnvironmentLightAmbient(0.2f,0.1f,11.1f,0.2f));
        environment.add(new EnvironmentLightPoint(new Color(1,0.2f,0,1), 1f, 0, 0, -3));

        transform3D.matrix4.rotateSelfAxis(Vector3.Y, 30);

    }


    @Override
    protected void refresh() {

        //if (GraphicsUtils.getFrameCount() % 60 == 0) System.out.println("Main Thread.");
        float delta = GraphicsUtils.getDeltaTime();
        cameraController.update(delta);
        // fixed update
        float angularSpeed = 200; // degrees per second

        if (Keyboard.isKeyPressed(Keyboard.Key.A)) {
            transform3D.matrix4.rotateSelfAxis(Vector3.Y, angularSpeed * delta);

        }

        if (Keyboard.isKeyPressed(Keyboard.Key.W)) {
            transform3D.matrix4.rotateSelfAxis(Vector3.X, angularSpeed * delta);


        }


        if (Keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {
            cameraTransform.translateSelfAxis(0,0,-1*delta);
            cameraTransform.rotateSelfAxis(0,0,1,1*delta);

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
        renderer3D.setEnvironment(environment);
        renderer3D.draw(model.parts[0], transform3D.matrix4);


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
