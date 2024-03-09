package org.example.game;

import org.example.engine.components.ComponentFactory;
import org.example.engine.components.ComponentTransform3D_old;
import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.graphics.*;
import org.example.engine.core.input.Keyboard;
import org.example.engine.core.math.Matrix4;
import org.example.engine.core.math.Vector3;
import org.lwjgl.opengl.GL11;

public class WindowScreenTest_Rendering_1 extends WindowScreen {

    private Renderer3D_old renderer3DOld;
    private Model model;
    private ShaderProgram shader;
    private ComponentTransform3D_old transform3D;
    private Camera camera;
    private Environment environment;
    private Matrix4 cameraTransform;

    // debug
    private BlenderCameraController cameraController;

    public WindowScreenTest_Rendering_1() {
        this.renderer3DOld = new Renderer3D_old();

        final String vertexShaderSrc = AssetUtils.getFileContent("assets/shaders/default.vert");
        final String fragmentShaderSrc = AssetUtils.getFileContent("assets/shaders/default.frag");
        this.shader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);


        this.camera = new Camera();
        this.environment = new Environment();

        cameraController = new BlenderCameraController(camera);
    }

    @Override
    public void show() {
        transform3D = ComponentFactory.createTransform3D();
        model = AssetStore.get("assets/models/cube-blue.fbx");
        System.out.println(model.parts[0].material.uniformParams);
        transform3D.matrix4.translateSelfAxis(0,0,-5f);
        cameraTransform = new Matrix4();
        //environment.add(new EnvironmentLightAmbient(0.2f,0.1f,11.1f,0.2f));
        environment.add(new EnvironmentLightPoint(new Color(1,0.2f,0,1), 1f, 0, 0, -3));
        //transform3D.matrix4.rotateSelfAxis(Vector3.Y, 30);
    }


    @Override
    protected void refresh() {
        float delta = GraphicsUtils.getDeltaTime();
        cameraController.update(delta);
        float angularSpeed = 200; // degrees per second

        System.out.println(transform3D.matrix4.getScale(new Vector3()));

        // rotate
        if (Keyboard.isKeyPressed(Keyboard.Key.R)) {
            transform3D.matrix4.spin(0.5f);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.T)) {
            transform3D.matrix4.roll(0.5f);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.Y)) {
            transform3D.matrix4.turn(0.5f);
        }

        // scale
        if (Keyboard.isKeyPressed(Keyboard.Key.KEY_1)) {
            //transform3D.matrix4.sclXYZ(1.01f,1,1);
            transform3D.matrix4.stretch(0.99f);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.KEY_2)) {
            //transform3D.matrix4.elongate(1.01f);
            transform3D.matrix4.elongate(1.01f);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.KEY_3)) {
            transform3D.matrix4.lengthen(1.01f);
            //transform3D.matrix4.elongate(1.01f);
        }

        // translate
        if (Keyboard.isKeyPressed(Keyboard.Key.A)) {
            //transform3D.matrix4.translateSelfAxis(0,0.01f,0);
            transform3D.matrix4.translateXYZAxis(-0.1f,0,0);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.D)) {
            //transform3D.matrix4.rotateSelfAxis(Vector3.X, angularSpeed * delta);
            transform3D.matrix4.translateXYZAxis(0.1f,0,0);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.W)) {
            //transform3D.matrix4.rotateSelfAxis(Vector3.X, angularSpeed * delta);
            transform3D.matrix4.translateXYZAxis(0f,0.1f,0);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.S)) {
            //transform3D.matrix4.rotateSelfAxis(Vector3.X, angularSpeed * delta);
            transform3D.matrix4.translateXYZAxis(0f,-0.1f,0);
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
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.LEFT)) {
            camera.lens.up.rotate(1, camera.lens.direction.x,camera.lens.direction.y,camera.lens.direction.z);
            camera.lens.update();
            camera.lens.direction.rotate(1, camera.lens.up.x,camera.lens.up.y,camera.lens.up.z);
            camera.lens.update();
            Matrix4 v = new Matrix4(camera.lens.view);
            Matrix4 t = new Matrix4(camera.lens.view);
            t.inv();
            Vector3 up2 = new Vector3(v.val[Matrix4.M00],v.val[Matrix4.M01],v.val[Matrix4.M02]);
            up2.crs(camera.lens.direction);
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);
        renderer3DOld.begin(shader);
        renderer3DOld.setCamera(camera);
        renderer3DOld.setEnvironment(environment);
        renderer3DOld.draw(model.parts[0], transform3D.matrix4);
        //renderer3DOld.draw(model.parts[1], transform3D.matrix4);

        renderer3DOld.end();
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
