package org.example.engine.core.graphics;

import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.collections.Array;
import org.lwjgl.stb.STBRPContext;
import org.lwjgl.stb.STBRPNode;
import org.lwjgl.stb.STBRPRect;
import org.lwjgl.stb.STBRectPack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TexturePacker {

    public static synchronized void packDirectory(final String outputName, final String directory, final boolean recursive) {
        if (directory == null) throw new IllegalArgumentException("Must provide non-null directory name.");
        if (!AssetUtils.directoryExists(directory)) throw new IllegalArgumentException("The provided path: " + directory + " does not exist, or is not a directory");
    }
    public static synchronized void packTextures(final String directory, final String outputName, final String ...texturePaths) throws IOException {
        final TexturePackerOptions options = new TexturePackerOptions(directory, outputName);
        packTextures(options, texturePaths);
    }

    public static synchronized void packTextures(final TexturePackerOptions options, final String ...texturePaths) throws IOException {
        if (alreadyPacked(options, texturePaths)) return;
        Array<PackedRegionData> regionsData = new Array<>(texturePaths.length);
        for (int i = 0; i < texturePaths.length; i++) {
            String texturePath = texturePaths[i];
            File sourceImageFile = new File(texturePath);
            BufferedImage sourceImage = ImageIO.read(sourceImageFile);
            PackedRegionData regionData = getPackedRegionData(options, texturePath, sourceImage);
            if (regionData.packedWidth > options.maxTexturesSize || regionData.packedHeight > options.maxTexturesSize) throw new IOException("Input texture file: " + regionData.texturePath + " cannot be packed - it's dimensions are bigger than the allowed maximum: width = " + regionData.packedWidth + ", height: " + regionData.packedHeight + ", maximum: " + options.maxTexturesSize + ".");
            regionsData.add(regionData);
        }
        regionsData.sort();

        Map<BufferedImage, Array<PackedRegionData>> texturePack = new HashMap<>();
        while (regionsData.size > 0) {
            System.out.println("size: " + regionsData.size);
            int last = regionsData.size - 1;
            while (!pack(options, texturePack, regionsData, last)) last--;
        }
        // print:
        for (Map.Entry<BufferedImage, Array<PackedRegionData>> entry : texturePack.entrySet()) {
            System.out.println(entry.getKey().getWidth());
        }
    }

    private static synchronized boolean pack(TexturePackerOptions options, Map<BufferedImage, Array<PackedRegionData>> texturePack, Array<PackedRegionData> remaining, int last) {
        if (last < 0) return true;
        int size = 1;
        System.out.println("last: " + last);
        while (size <= options.maxTexturesSize) {
            System.out.println(remaining.size + ": " + "<" + size + ">");
            STBRPContext context = STBRPContext.create();
            STBRPNode.Buffer nodes = STBRPNode.create(size); // Number of nodes can be context width
            STBRectPack.stbrp_init_target(context, size, size, nodes);
            STBRPRect.Buffer rects = STBRPRect.create(last + 1);
            for (int i = 0; i < rects.capacity(); i++) {
                rects.position(i);
                rects.id(i);
                rects.w(remaining.get(i).packedWidth);
                rects.h(remaining.get(i).packedHeight);
            }
            rects.position(0);
            int result = STBRectPack.stbrp_pack_rects(context, rects);
            if (result != 0) {
                System.out.println("packed!");
                BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Array<PackedRegionData> regionsData = new Array<>();
                rects.position(0);
                for (int i = 0; i < rects.capacity(); i++) {
                    rects.position(i);
                    PackedRegionData item = remaining.get(i);
                    item.x = rects.x();
                    item.y = rects.y();
                    regionsData.add(item);
                }
                texturePack.put(bufferedImage, regionsData);
                remaining.removeAll(regionsData, true);
                return true;
            } else {
                size *= 2;
            }
        }
        return false;
    }

    // TODO: implement? This can cause major slowdowns.
    private static synchronized boolean areIdentical(BufferedImage a, BufferedImage b) { return false; }

    private static synchronized PackedRegionData getPackedRegionData(final TexturePackerOptions options, final String path, final BufferedImage sourceImage) {
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

    private static synchronized void generatePackFile(final TexturePackerOptions options, PackedRegionData[] regionsData) {

    }

    private static synchronized void generatePackTextureFiles(final TexturePackerOptions options, Map<BufferedImage, PackedRegionData[]> partition) {

    }

    // TODO: implement.
    private static synchronized boolean alreadyPacked(final TexturePackerOptions options, final String ...texturePaths) {
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
        private final int area;

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
            this.area = packedWidth * packedHeight;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        @Override
        public int compareTo(PackedRegionData o) {
            return Integer.compare(o.area, this.area);
        }

    }


}
