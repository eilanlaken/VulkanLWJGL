package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.graphics.TexturePacker;
import org.example.engine.core.graphics.TexturePackerOptions;
import org.example.engine.core.graphics.WindowAttributes;
import org.example.engine.core.math.MathUtils;
import org.example.game.ScreenLoading;
import org.lwjgl.stb.STBRPContext;
import org.lwjgl.stb.STBRPNode;
import org.lwjgl.stb.STBRPRect;
import org.lwjgl.stb.STBRectPack;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
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