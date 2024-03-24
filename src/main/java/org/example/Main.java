package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.graphics.TexturePackGenerator;
import org.example.engine.core.graphics.WindowAttributes;
import org.example.engine.core.math.Matrix4;
import org.example.engine.core.math.Shape3DFrustum;
import org.example.game.ScreenLoading;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

public class Main {

    public static void main(String[] args) {

//        try {
//            TexturePackGenerator.Options options = new TexturePackGenerator.Options("assets/atlases", "pack2",
//                    null, null, null, null,
//                    20,5, TexturePackGenerator.Options.Size.XX_LARGE_8192);
//            //TexturePacker.packTextures(options, "assets/textures/pinkSpot.png", "assets/textures/yellowSquare.png", "assets/textures/yellowSquare2.png");
//            TexturePackGenerator.packTextures(options, "assets/textures/sphere-colored.png", "assets/textures/yellowSquare2.png", "assets/textures/pinkSpot.png");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        WindowAttributes config = new WindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new ScreenLoading());
    }

}