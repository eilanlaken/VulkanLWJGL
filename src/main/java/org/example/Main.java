package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.application.ApplicationWindowAttributes;
import org.example.engine.core.graphics.TextureGenerator;
import org.example.game.ScreenLoading;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        /* texture generator tests */
//        try {
//            TextureGenerator.generateTextureNoisePerlin(128, 128, "assets/textures", "hi", false);
//        } catch (Exception e) {
//            throw e;
//        }


//        try {
//            TexturePacker.Options options = new TexturePacker.Options("assets/atlases", "physicsDebugShapes",
//                    null, null, null, null,
//                    0,0, TexturePacker.Options.Size.XX_LARGE_8192);
//            //TexturePacker.packTextures(options, "assets/textures/pinkSpot.png", "assets/textures/yellowSquare.png", "assets/textures/yellowSquare2.png");
//            TexturePacker.packTextures(options, "assets/textures/physicsCircle.png", "assets/textures/physicsSquare.png");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        ApplicationWindowAttributes config = new ApplicationWindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new ScreenLoading());

    }

}