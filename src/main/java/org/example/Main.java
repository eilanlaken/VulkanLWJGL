package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.graphics.TexturePackGenerator;
import org.example.engine.core.graphics.WindowAttributes;
import org.example.engine.core.math.*;
import org.example.game.ScreenLoading;
import org.lwjgl.BufferUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Shape2DPolygon polygon = new Shape2DPolygon(new float[] {-1,0,0,-1,1,0,0,1});
        System.out.println(polygon.getArea());

        System.out.println(polygon.contains(-0.5f,-1.3f));


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