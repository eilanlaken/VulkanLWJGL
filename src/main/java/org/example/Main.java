package org.example;

import org.example.engine.core.graphics.TexturePacker;
import org.example.engine.core.graphics.TexturePackerOptions;
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
        try {
            TexturePackerOptions options = new TexturePackerOptions("assets/atlases", "pack",
                    null, null, null, null,
                    4,1, TexturePackerOptions.Size.XX_LARGE_8192);
            //TexturePacker.packTextures(options, "assets/textures/pinkSpot.png", "assets/textures/yellowSquare.png", "assets/textures/yellowSquare2.png");
            TexturePacker.packTextures(options, "assets/textures/pattern2.png");

        } catch (Exception e) {
            e.printStackTrace();
        }

//        WindowAttributes config = new WindowAttributes();
//        Application.createSingleWindowApplication(config);
//        Application.launch(new ScreenLoading());
    }

    private static void copy() {
        try {
            // Step 1: Read the source image
            File sourceImageFile = new File("assets/textures/yellowSquare.png");
            BufferedImage sourceImage = ImageIO.read(sourceImageFile);
            // Step 2: Create a destination image
            BufferedImage destinationImage = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
            // Step 3: Get the Graphics2D object
            Graphics2D g = destinationImage.createGraphics();
            // Step 4: Draw the source image onto the destination
            g.drawImage(sourceImage, 50, 50, null);
            // Step 5: Dispose of the Graphics2D object
            g.dispose();
            // Step 6: Save the destination image (optional)
            File outputFile = new File("assets/textures/destinationImage.png");
            ImageIO.write(destinationImage, "png", outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}