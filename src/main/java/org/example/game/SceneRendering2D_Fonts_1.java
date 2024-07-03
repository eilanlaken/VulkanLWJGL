package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.graphics.Camera;
import org.example.engine.core.graphics.Color;
import org.example.engine.core.graphics.GraphicsException;
import org.example.engine.core.graphics.Renderer2D;
import org.example.engine.core.input.Keyboard;
import org.example.engine.core.input.Mouse;
import org.example.engine.core.math.Vector3;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.freetype.FreeType;

import java.nio.ByteBuffer;

public class SceneRendering2D_Fonts_1 extends ApplicationScreen {

    private Renderer2D renderer2D;
    private Camera camera;
    private float red = new Color(1,0,0,1).toFloatBits();
    private float blue = new Color(0,0,1,1).toFloatBits();

    private String text = "render me!";

    private STBTTFontinfo info;
    private ByteBuffer ttf;
    private int ascent;
    private int descent;
    private int lineGap;

    public SceneRendering2D_Fonts_1() {
        renderer2D = new Renderer2D();
    }

    @Override
    public void show() {
        camera = new Camera(640f/32,480f/32, 1);
        camera.update();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer libraryPointer = stack.mallocPointer(1);
            int errorInit = FreeType.FT_Init_FreeType(libraryPointer);
            if (errorInit != 0) throw new GraphicsException("Failed to load font face. Error code: " + errorInit);
            long library = libraryPointer.get(0);

            PointerBuffer facePointer = stack.mallocPointer(1);
            int errorFace = FreeType.FT_New_Face(library, "path/to/your/font.ttf", 0, facePointer);
            if (errorFace != 0) throw new GraphicsException("Failed to load font face. Error code: " + errorFace);
            long face = facePointer.get(0);
            // Set the pixel size for the font
            //FreeType.FT_Set_Pixel_Sizes(face, 0, 48); // Set font size to 48 pixels high

        }

    }

    @Override
    protected void refresh() {
        Vector3 screen = new Vector3(Mouse.getCursorX(), Mouse.getCursorY(), 0);
        camera.lens.unproject(screen);

        if (Mouse.isButtonPressed(Mouse.Button.LEFT)) {

        }

        if (Mouse.isButtonClicked(Mouse.Button.RIGHT)) {

        }

        if (Keyboard.isKeyJustPressed(Keyboard.Key.S)) {

        }

        if (Keyboard.isKeyPressed(Keyboard.Key.R)) {
            //body_a.applyForce(1,0, body_a.shape.x(), body_a.shape.y() + 0.2f);
        }

        if (Keyboard.isKeyPressed(Keyboard.Key.SPACE)) {
            //world.createConstraintWeld(body_a, body_b, new Vector2(1,0));
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);

        renderer2D.begin(camera);

        renderer2D.end();
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void hide() {
        renderer2D.deleteAll();
    }

    @Override
    public void deleteAll() {

    }

}
