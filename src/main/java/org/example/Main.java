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
        try {
            TexturePackerOptions options = new TexturePackerOptions("assets/atlases", "assets/textures/pinkSpot.png",
                    null, null, null, null,
                    1,3, TexturePackerOptions.Size.SMALL_512);
            TexturePacker.packTextures(options, "assets/textures/pinkSpot.png", "assets/textures/yellowSquare.png", "assets/textures/yellowSquare2.png");
        } catch (Exception e) {
            e.printStackTrace();
        }

//        WindowAttributes config = new WindowAttributes();
//        Application.createSingleWindowApplication(config);
//        Application.launch(new ScreenLoading());
    }

    private static void estimateSize() {
        boolean packed = false;
        int contextWidth = 128;
        int contextHeight = 128;
        while (!packed) {
            STBRPContext context = STBRPContext.create();
            STBRPNode.Buffer nodes = STBRPNode.create(contextWidth); // Number of nodes can be context width
            STBRectPack.stbrp_init_target(context, contextWidth, contextHeight, nodes);

            // Populate your rects here based on current textures
            STBRPRect.Buffer rects = STBRPRect.create(5);
            for (int i = 0; i < rects.capacity(); i++) {
                rects.position(i);
                rects.id(i);
                rects.w(1280);
                rects.h(720);
            }
            rects.position(0);

            if (STBRectPack.stbrp_pack_rects(context, rects) != 0) {
                System.out.println("ok");
                packed = true;
                // Proceed with using the packed rects
            } else {
                // Increase context size and try again
                System.out.println("not packed");
                contextWidth *= 2;
                contextHeight *= 2;
            }
        }

    }

    private static void pack() {
        // init the target
        STBRPContext context = STBRPContext.create();
        int width = 2560;
        int height = 1440;
        STBRPNode.Buffer nodes = STBRPNode.create(2560);
        STBRectPack.stbrp_init_target(context, width, height, nodes);
        // create an example buffer of 4 rects to be packed.
        STBRPRect.Buffer rects = STBRPRect.create(4);
        for (int i = 0; i < rects.capacity(); i++) {
            rects.position(i);
            rects.id(i);
            rects.w(1280);
            rects.h(720);
        }
        rects.position(0);
        int result = STBRectPack.stbrp_pack_rects(context, rects);
        if (result == 0) System.out.println("packing failed.");
        else {
            // interpret the result
            for (int i = 0; i < rects.capacity(); i++) {
                STBRPRect rect = rects.get(i);
                System.out.println("Rectangle " + rect.id() + ": <" + rect.x() + ", " + rect.y() + ">." + " packed? " + rect.was_packed());
            }
        }
    }

    private static void trim() {
        try {
            File inputFile = new File("assets/textures/pinkSpot.png");
            BufferedImage sourceImage = ImageIO.read(inputFile);

            int width = sourceImage.getWidth();
            int height = sourceImage.getHeight();

            int minX = width;
            int minY = height;
            int maxX = 0;
            int maxY = 0;

            // Determine the bounds
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int alpha = (sourceImage.getRGB(x, y) >> 24) & 0xff;
                    if (alpha != 0) {  // Pixel is not transparent
                        if (x < minX) minX = x;
                        if (x > maxX) maxX = x;
                        if (y < minY) minY = y;
                        if (y > maxY) maxY = y;
                    }
                }
            }

            // Create a new image with just the non-transparent content
            int newWidth = maxX - minX + 1;
            int newHeight = maxY - minY + 1;
            BufferedImage trimmedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            trimmedImage.getGraphics().drawImage(sourceImage, 0, 0, newWidth, newHeight, minX, minY, maxX + 1, maxY + 1, null);

            // Save or use the trimmed image
            File outputFile = new File("assets/textures/trimmedImage.png");
            ImageIO.write(trimmedImage, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            g.drawImage(sourceImage, 0, 0, null);
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