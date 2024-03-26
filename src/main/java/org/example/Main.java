package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.graphics.TexturePackGenerator;
import org.example.engine.core.graphics.WindowAttributes;
import org.example.engine.core.math.Matrix4;
import org.example.engine.core.math.Shape2DAABB;
import org.example.engine.core.math.Shape2DRectangle;
import org.example.engine.core.math.Shape3DFrustum;
import org.example.game.ScreenLoading;
import org.lwjgl.BufferUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Shape2DRectangle rect = new Shape2DRectangle(0,0,4,1,90);
        rect.updateCorners();
        System.out.println(rect);

        System.out.println(rect.contains(0,0));
        System.out.println(rect.contains(3,0));
        System.out.println(rect.contains(1,-5));
        System.out.println(rect);


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
//        WindowAttributes config = new WindowAttributes();
//        Application.createSingleWindowApplication(config);
//        Application.launch(new ScreenLoading());
    }

}