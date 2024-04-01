package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.graphics.WindowAttributes;
import org.example.game.ScreenLoading;

public class Main {

    public static void main(String[] args) {




//
//        System.out.println("world: " + Arrays.toString(p.getWorldPoints()));
//
//        p.setScale(2,2);
//
//        p.update();
//        System.out.println("world: " + Arrays.toString(p.getWorldPoints()));


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

        //if (true) return;

        WindowAttributes config = new WindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new ScreenLoading());

    }

}