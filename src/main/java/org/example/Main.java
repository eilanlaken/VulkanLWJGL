package org.example;

import org.example.engine.components.ComponentGraphics2DShape;
import org.example.engine.components.FactoryComponent;
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

        ComponentGraphics2DShape s0 = FactoryComponent.createShapeLine(100, -4, 333,-4, 3, null,null, null);
        System.out.println("indices 1: " + Arrays.toString(s0.polygon.indices));
        ComponentGraphics2DShape s1 = FactoryComponent.createShapeCircleHollow(10, 10, 1,null, null, null);
        System.out.println("indices 1: " + Arrays.toString(s1.polygon.indices));
        ComponentGraphics2DShape s2 = FactoryComponent.createShapeCircleHollow(150, 10, 2,null, null, null);
        System.out.println("indices 2: " + Arrays.toString(s2.polygon.indices));
        ComponentGraphics2DShape s3 = FactoryComponent.createShapeRectangleFilled(150, 10,null, null, null);
        System.out.println("indices 2fff: " + Arrays.toString(s3.polygon.indices));

        ComponentGraphics2DShape s4 = FactoryComponent.createShapeRectangleHollow(150, 600, 10,null, null, null);
        System.out.println("indices gggggg: " + Arrays.toString(s4.polygon.indices));


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