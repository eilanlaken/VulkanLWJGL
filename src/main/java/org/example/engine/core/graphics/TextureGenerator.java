package org.example.engine.core.graphics;

import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.collections.CollectionsUtils;
import org.example.engine.core.math.MathUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;

// TODO: implement
public final class TextureGenerator {

    private static boolean initialized = false;

    private static final int PERMUTATION[]      = new int[256];
    private static final int p[] = new int[512];

    private TextureGenerator() {}

    private static synchronized void init() {
        if (initialized) return;
        for (int i = 0; i < 256; i++) {
            PERMUTATION[i] = i;
        }
        CollectionsUtils.shuffle(PERMUTATION);
        for (int i = 0; i < 256; i++) {
            p[i] = PERMUTATION[i];
            p[256 + i] = PERMUTATION[i];
        }
        initialized = true;
    }

    /* Noise */

    public synchronized static void generateTextureNoisePerlin(int width, int height, final String directory, final String fileName, boolean overrideExistingFile) throws IOException {
        init();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float nx = x / (float) width;
                float ny = y / (float) height;
                float noise = noise(nx * 10, ny * 10, 0);
                int value = (int) ((noise + 1) * 128); // Convert [-1,1] to [0,255]
                int rgb = value | (value << 8) | (value << 16); // Gray color
                image.setRGB(x, y, rgb);
            }
        }

        AssetUtils.saveImage(directory, fileName, image);
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

    /* Texture Generation Utils  */
    private static float fade(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private static float grad(int hash, float x, float y, float z) {
        int h = hash & 15;
        float u = h < 8 ? x : y;
        float v = h < 4 ? y : (h == 12 || h == 14 ? x : z);
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    private static float noise(float x, float y, float z) {
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;
        int Z = (int) Math.floor(z) & 255;

        x -= Math.floor(x);
        y -= Math.floor(y);
        z -= Math.floor(z);

        float u = fade(x);
        float v = fade(y);
        float w = fade(z);

        int A = p[X] + Y, AA = p[A] + Z, AB = p[A + 1] + Z;
        int B = p[X + 1] + Y, BA = p[B] + Z, BB = p[B + 1] + Z;

        /* Sometimes it is beneficial to not ask questions. */
        return MathUtils.lerp(w, MathUtils.lerp(v, MathUtils.lerp(u, grad(p[AA], x, y, z),
                grad(p[BA], x - 1, y, z)),
                MathUtils.lerp(u, grad(p[AB], x, y - 1, z), grad(p[BB], x - 1, y - 1, z))),
                MathUtils.lerp(v, MathUtils.lerp(u, grad(p[AA + 1], x, y, z - 1), grad(p[BA + 1], x - 1, y, z - 1)), MathUtils.lerp(u, grad(p[AB + 1], x, y - 1, z - 1), grad(p[BB + 1], x - 1, y - 1, z - 1))));
    }

    private static int getColor(float r, float g, float b, float a) {
        int rInt = (int) (r * 255);
        int gInt = (int) (g * 255);
        int bInt = (int) (b * 255);
        int aInt = (int) (a * 255);

        return (aInt << 24) | (rInt << 16) | (gInt << 8) | bInt;
    }

}



