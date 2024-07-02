package org.example.engine.core.graphics;

import org.example.engine.core.assets.AssetUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

// TODO: implement
public final class TextureGenerator {

    private TextureGenerator() {}

    /* Noise */
    public synchronized static void generateTextureNoisePerlin(int width, int height, final String outputPath, boolean overrideExistingFile) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                image.setRGB(i, j, Color.RED.toIntBits());
            }
        }

        AssetUtils.saveImage(outputPath, image);
    }

    public static void generateTextureNoiseSimplex() {}
    public static void generateTextureNoiseWhite() {}
    public static void generateTextureNoiseValue() {}
    public static void generateTextureNoiseVornoi() {}

    /* Patterns */
    public static void generateTextureCheckers() {}
    public static void generateTextureGradient() {}
    public static void generateTextureWave() {}

    /* Maps */
    public static void generateTextureMapNormal() {}
    public static void generateTextureMapRoughness() {}
    public static void generateTextureMapMetallic() {}


}
