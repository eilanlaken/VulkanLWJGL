package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.graphics.TexturePacker;
import org.example.engine.core.graphics.WindowAttributes;
import org.example.engine.core.math.Algorithms;
import org.example.engine.core.math.Shape2DPolygon;
import org.example.game.ScreenLoading;

public class Main {

    public static void main(String[] args) {

        float[] v = new float[] {0,10, 0,0, 10,0, 2,2};
        System.out.println(Algorithms.isPolygonConvex(v));




//
//        System.out.println("world: " + Arrays.toString(p.getWorldPoints()));
//
//        p.setScale(2,2);
//
//        p.update();
//        System.out.println("world: " + Arrays.toString(p.getWorldPoints()));


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

        if (true) return;

        WindowAttributes config = new WindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new ScreenLoading());

    }

}