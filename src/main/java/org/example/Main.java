package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.collections.ArrayInt;
import org.example.engine.core.graphics.WindowAttributes;
import org.example.engine.core.math.*;
import org.example.game.ScreenLoading;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Shape2DPolygon p = new Shape2DPolygon(new float[] {0, 0, 0, 50, 50, 0});
        int[] indices = Algorithms.triangulatePolygon(new float[] {0, 0, 0, 50, 50, 0});
        System.out.println("indices 1: " + Arrays.toString(indices));
        System.out.println("indices 1: " + p.getArea());


//        Shape2DPolygon p = new Shape2DPolygon(new float[] {0,1, 0,0, 1,0, 1,1});
//        System.out.println(Arrays.toString(p.indices));
//        System.out.println(p.getArea());
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
        if (true) return;
        WindowAttributes config = new WindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new ScreenLoading());

    }

}