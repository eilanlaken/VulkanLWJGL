package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.graphics.WindowAttributes;
import org.example.game.ScreenLoading;

public class Main {

    public static void main(String[] args) {
        short[] x = new short[1_000_000];


        long nanos1 = System.nanoTime();
        for (int i = 0; i < x.length; i++) {
            x[i] = 2;
        }
        long nanos2 = System.nanoTime();
        long diff1 = nanos2 - nanos1;

        long nanos3 = System.nanoTime();
        for (int i = x.length - 1; i >= 0; i--) {
            x[i] = 2;
        }
        long nanos4 = System.nanoTime();
        long diff2 = nanos4 - nanos3;

        System.out.println("diff1 " + diff1);

        System.out.println("diff2 " + diff2);
        //copy();
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