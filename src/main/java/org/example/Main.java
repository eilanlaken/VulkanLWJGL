package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.graphics.WindowAttributes;
import org.example.game.ScreenLoading;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

public class Main {

    public static void main(String[] args) {
        IntBuffer b = BufferUtils.createIntBuffer(400);
        b.put(4).put(5).put(6);
        System.out.println(b.position());
        b.flip();
        System.out.println(b.get());
        System.out.println(b.position());

        System.out.println(b.position(0));
        System.out.println("limit: " + b.limit());
        System.out.println(b.get());

//        try {
//            TexturePackerOptions options = new TexturePackerOptions("assets/atlases", "pack",
//                    null, null, null, null,
//                    20,5, TexturePackerOptions.Size.XX_LARGE_8192);
//            //TexturePacker.packTextures(options, "assets/textures/pinkSpot.png", "assets/textures/yellowSquare.png", "assets/textures/yellowSquare2.png");
//            TexturePacker.packTextures(options, "assets/textures/pattern2.png", "assets/textures/yellowSquare2.png", "assets/textures/pinkSpot.png");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        WindowAttributes config = new WindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new ScreenLoading());
    }

}