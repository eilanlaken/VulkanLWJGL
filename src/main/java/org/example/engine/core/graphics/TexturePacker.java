package org.example.engine.core.graphics;

import org.example.engine.core.assets.AssetUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class TexturePacker {

    public static synchronized void packDirectory(final Options options, final String directory) {

    }
    public static synchronized void packDirectory(final String outputName, final String directory, final boolean recursive) {
        if (directory == null) throw new IllegalArgumentException("Must provide non-null directory name.");
        if (!AssetUtils.directoryExists(directory)) throw new IllegalArgumentException("The provided path: " + directory + " does not exist, or is not a directory");
    }
    public static synchronized void packTextures(final String directory, final String outputName, final String ...texturePaths) throws IOException {
        final Options options = new Options(directory, outputName);
        packTextures(options, texturePaths);
    }

    public static synchronized void packTextures(final Options options, final String ...texturePaths) throws IOException {
        if (alreadyPacked(options, texturePaths)) return;
        PackedRegionData[] packedRegionsData = new PackedRegionData[texturePaths.length];
        for (int i = 0; i < texturePaths.length; i++) {
            String texturePath = texturePaths[i];
            File sourceImageFile = new File(texturePath);
            BufferedImage sourceImage = ImageIO.read(sourceImageFile);
            packedRegionsData[i] = getPackedRegionData(options, texturePath, sourceImage);
        }
        Arrays.sort(packedRegionsData);
        for (PackedRegionData regionData : packedRegionsData) {
            System.out.println(regionData.texturePath + ": " + regionData.offsetX);
        }
    }

    // TODO: implement? This can cause major slowdowns.
    private static synchronized boolean areIdentical(BufferedImage a, BufferedImage b) {

        return false;
    }

    private static synchronized PackedRegionData getPackedRegionData(final Options options, final String path, final BufferedImage sourceImage) {
        int originalWidth = sourceImage.getWidth();
        int originalHeight = sourceImage.getHeight();
        int minX = originalWidth;
        int minY = originalHeight;
        int maxX = 0;
        int maxY = 0;
        // Determine the bounds
        for (int y = 0; y < originalHeight; y++) {
            for (int x = 0; x < originalWidth; x++) {
                int alpha = (sourceImage.getRGB(x, y) >> 24) & 0xff;
                if (alpha != 0) {  // Pixel is not transparent
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }
        int packedWidth = maxX - minX + 1 + options.extrude + options.padding;
        int packedHeight = maxY - minY + 1 + options.extrude + options.padding;
        int offsetX = minX;
        int offsetY = minY;
        return new PackedRegionData(sourceImage, path, packedWidth, packedHeight, offsetX, offsetY);
    }

    private static synchronized void generateMapFile(final Options options, PackedRegionData[] regionsData) {

    }

    private static synchronized void generateMapTextures(final Options options, Map<BufferedImage, PackedRegionData[]> partition) {

    }

    private static synchronized Map<BufferedImage, PackedRegionData[]> getPartition(PackedRegionData[] allRegions, int fromIndex) {

        return null;
    }

    // TODO: implement.
    private static synchronized boolean alreadyPacked(final Options options, final String ...texturePaths) {
        final String outputDirectory = options.outputDirectory;
        // check if the output directory or the texture map file is missing
        if (!AssetUtils.directoryExists(outputDirectory)) return false;
        File atlasFile = new File(outputDirectory, options.outputName);
        if (!AssetUtils.fileExists(atlasFile.getPath())) return false;
        // if we did find the map file, check for the presence of all required textures
        final String mapPath = outputDirectory + File.separator + options.outputName;
        String contents = AssetUtils.getFileContent(mapPath);
        // contents should be a json string.
        // get all the textures names and the options object.
        // TODO: continue.

        return false;
    }

    private static final class PackedRegionData implements Comparable<PackedRegionData> {

        private final BufferedImage sourceImage;
        private final String texturePath;
        private final int originalWidth;
        private final int originalHeight;
        private final int packedWidth;
        private final int packedHeight;
        private final int offsetX;
        private final int offsetY;
        private final int totalPixels;

        private int x;
        private int y;
        private int textureIndex;

        public PackedRegionData(BufferedImage sourceImage, String texturePath, int packedWidth, int packedHeight, int offsetX, int offsetY) {
            this.sourceImage = sourceImage;
            this.texturePath = texturePath;
            this.originalWidth = sourceImage.getWidth();
            this.originalHeight = sourceImage.getHeight();
            this.packedWidth = packedWidth;
            this.packedHeight = packedHeight;
            this.totalPixels = packedWidth * packedHeight;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        @Override
        public int compareTo(PackedRegionData o) {
            return Integer.compare(this.totalPixels, o.totalPixels);
        }

    }

    public static final class Options {

        public final String outputDirectory;
        public final String outputName;
        public final TextureParamFilter magFilter;
        public final TextureParamFilter minFilter;
        public final TextureParamWrap uWrap;
        public final TextureParamWrap vWrap;
        public final int extrude; // extrude REPEATS the border i.e. adding colors.
        public final int padding;
        public final int maxTextureWidth;
        public final int maxTextureHeight;

        public Options(String outputDirectory, String outputName) {
            this(outputDirectory, outputName, TextureParamFilter.MIP_MAP_NEAREST_NEAREST, TextureParamFilter.MIP_MAP_NEAREST_NEAREST, TextureParamWrap.CLAMP_TO_EDGE, TextureParamWrap.CLAMP_TO_EDGE, 1,1, GraphicsUtils.getMaxTextureSize() / 4, GraphicsUtils.getMaxTextureSize() / 4);
        }

        public Options(String outputDirectory, String outputName, TextureParamFilter magFilter, TextureParamFilter minFilter, TextureParamWrap uWrap, TextureParamWrap vWrap, int extrude, int padding, int maxTextureWidth, int maxTextureHeight) {
            this.outputDirectory = outputDirectory;
            this.outputName = outputName;
            this.magFilter = magFilter == null ? TextureParamFilter.MIP_MAP_NEAREST_NEAREST : magFilter;
            this.minFilter = minFilter == null ? TextureParamFilter.MIP_MAP_NEAREST_NEAREST : minFilter;
            this.uWrap = uWrap == null ? TextureParamWrap.CLAMP_TO_EDGE : uWrap;
            this.vWrap = vWrap == null ? TextureParamWrap.CLAMP_TO_EDGE : vWrap;
            this.extrude = Math.max(extrude, 0);
            this.padding = Math.max(padding, 0);
            this.maxTextureWidth = Math.min(Math.max(maxTextureWidth, 1), GraphicsUtils.getMaxTextureSize() / 4);
            this.maxTextureHeight = Math.min(Math.max(maxTextureHeight, 1), GraphicsUtils.getMaxTextureSize() / 4);
        }

    }


}
