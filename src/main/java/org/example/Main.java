package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.application.ApplicationWindowAttributes;
import org.example.engine.core.graphics.TexturePacker;
import org.example.game.ScreenLoading;

public class Main {

    public static void main(String[] args) {



        try {
            TexturePacker.Options options = new TexturePacker.Options("assets/atlases", "texture-packer-2-output",
                    null, null, null, null,
                    0,0, TexturePacker.Options.Size.SMALL_512);
            //TexturePacker.packTextures(options, "assets/textures/pinkSpot.png", "assets/textures/yellowSquare.png", "assets/textures/yellowSquare2.png");
            TexturePacker.packTextures(options, "assets/textures/physicsCircle.png", "assets/textures/physicsSquare.png");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (true) return;

        ApplicationWindowAttributes config = new ApplicationWindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new ScreenLoading());

    }

}