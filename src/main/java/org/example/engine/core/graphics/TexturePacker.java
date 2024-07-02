package org.example.engine.core.graphics;

import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.collections.Array;
import org.lwjgl.stb.STBRPContext;
import org.lwjgl.stb.STBRPNode;
import org.lwjgl.stb.STBRPRect;
import org.lwjgl.stb.STBRectPack;

import javax.imageio.ImageIO;
import java.awt.*;
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
        final Options options = new Options(directory, outputName);
        packTextures(options, texturePaths);
    }

    public static synchronized void packTextures(final Options options, final String ...texturePaths) throws IOException {
        if (alreadyPacked(options, texturePaths)) return;
        Array<PackedRegionData> regionsData = new Array<>(texturePaths.length);
        for (int i = 0; i < texturePaths.length; i++) {
            String texturePath = texturePaths[i];
            File sourceImageFile = new File(texturePath);
            BufferedImage sourceImage = ImageIO.read(sourceImageFile);
            PackedRegionData regionData = getPackedRegionData(options, texturePath, sourceImage);
            if (regionData.packedWidth > options.maxTexturesSize || regionData.packedHeight > options.maxTexturesSize) throw new IOException("Input texture file: " + regionData.name + " cannot be packed - it's dimensions are bigger than the allowed maximum: width = " + regionData.packedWidth + ", height: " + regionData.packedHeight + ", maximum: " + options.maxTexturesSize + ".");
            regionsData.add(regionData);
        }
        regionsData.sort();

        Map<IndexedBufferedImage, Array<PackedRegionData>> texturePack = new HashMap<>();
        int index = 0;
        while (regionsData.size > 0) {
            int last = regionsData.size - 1;
            while (!pack(options, texturePack, regionsData, last, index)) last--;
            index++;
        }

        generatePackFile(options, texturePack);
        generatePackTextureFiles(options, texturePack);
    }

    private static synchronized boolean pack(Options options, Map<IndexedBufferedImage, Array<PackedRegionData>> texturePack, Array<PackedRegionData> remaining, int last, int currentImageIndex) {
        if (last < 0) return true;
        int size = 1;

        while (size <= options.maxTexturesSize) {
            STBRPContext context = STBRPContext.create();
            STBRPNode.Buffer nodes = STBRPNode.create(size); // Number of nodes can be context width
            STBRectPack.stbrp_init_target(context, size, size, nodes);
            STBRPRect.Buffer rects = STBRPRect.create(last + 1);
            for (int i = 0; i < rects.capacity(); i++) {
                rects.position(i);
                rects.id(i);
                rects.w(remaining.get(i).packedWidth + 2 * (options.extrude + options.padding));
                rects.h(remaining.get(i).packedHeight + 2 * (options.extrude + options.padding));
            }
            rects.position(0);
            int result = STBRectPack.stbrp_pack_rects(context, rects);
            if (result != 0) {
                //BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                IndexedBufferedImage bufferedImage = new IndexedBufferedImage(currentImageIndex, size, size);
                Array<PackedRegionData> regionsData = new Array<>();
                rects.position(0);
                for (int i = 0; i < rects.capacity(); i++) {
                    rects.position(i);
                    PackedRegionData item = remaining.get(i);
                    item.x = rects.x() + options.extrude + options.padding;
                    item.y = rects.y() + options.extrude + options.padding;
                    item.textureIndex = currentImageIndex;
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
        maxX++;
        maxY++;
        int packedWidth = maxX - minX;// + options.extrude + options.padding;
        int packedHeight = maxY - minY;// + options.extrude + options.padding;
        int offsetX = minX;
        int offsetY = originalHeight - packedHeight - minY;
        System.out.println("orig: w, h: " + originalWidth + ", " + originalHeight);
        System.out.println("packed: w, h: " + packedWidth + ", " + packedHeight);
        System.out.println("offset x, y: " + offsetX + ", " + offsetY);
        return new PackedRegionData(sourceImage, path, packedWidth, packedHeight, offsetX, offsetY, minX, minY);
    }

    private static synchronized void generatePackFile(final Options options, Map<IndexedBufferedImage, Array<PackedRegionData>> texturePack) {
        String outputName = options.outputName;
        TextureData[] texturesData = new TextureData[texturePack.size()];
        int i = 0;
        for (IndexedBufferedImage img : texturePack.keySet()) {
            texturesData[i] = new TextureData();
            texturesData[i].file = outputName + "_" + img.index + ".png";
            texturesData[i].width = img.getWidth();
            texturesData[i].height = img.getHeight();
        }

        Map<String, Object> optionsData = new HashMap<>();
        optionsData.put("extrude", options.extrude);
        optionsData.put("padding", options.padding);
        optionsData.put("maxTextureSize", options.maxTexturesSize);
        optionsData.put("magFilter", options.magFilter.name());
        optionsData.put("minFilter", options.minFilter.name());
        optionsData.put("uWrap", options.uWrap.name());
        optionsData.put("vWrap", options.vWrap.name());

        Array<PackedRegionData> allRegions = new Array<>();
        for (Map.Entry<IndexedBufferedImage, Array<PackedRegionData>> imageRegions : texturePack.entrySet()) {
            allRegions.addAll(imageRegions.getValue());
        }
        allRegions.pack();

        Map<String, Object> yamlData = new HashMap<>();
        yamlData.put("regions", allRegions.items);
        yamlData.put("options", optionsData);
        yamlData.put("textures", texturesData);

        String content = AssetUtils.yaml.dump(yamlData);
        try {
            AssetUtils.saveFile(options.outputDirectory, options.outputName + ".txp", content);
        } catch (Exception e) {
            // TODO: take care as part of the error handling branch
            // ignored for now
        }
    }

    private static synchronized void generatePackTextureFiles(final Options options, Map<IndexedBufferedImage, Array<PackedRegionData>> texturePack) throws IOException {
        for (Map.Entry<IndexedBufferedImage, Array<PackedRegionData>> imageRegions : texturePack.entrySet()) {
            IndexedBufferedImage texturePackImage = imageRegions.getKey();
            for (PackedRegionData region : imageRegions.getValue()) {
                File sourceImageFile = new File(region.name);
                BufferedImage sourceImage = ImageIO.read(sourceImageFile);

                // copy non-transparent region
                for (int y = region.y; y < region.y + region.packedHeight; y++) {
                    for (int x = region.x; x < region.x + region.packedWidth; x++) {
                        int color = sourceImage.getRGB(region.minX + x - region.x, region.minY + y - region.y);
                        texturePackImage.setRGB(x,y,color);
                    }
                }
                // extrude up
                for (int y = region.y - options.extrude; y < region.y; y++) {
                    for (int x = region.x; x < region.x + region.packedWidth; x++) {
                        int color = 0;
                        color = sourceImage.getRGB(region.minX + x - region.x, 0);
                        texturePackImage.setRGB(x,y,color);
                    }
                }
                // extrude down
                for (int y = region.y + region.packedHeight; y < region.y + region.packedHeight + options.extrude; y++) {
                    for (int x = region.x; x < region.x + region.packedWidth; x++) {
                        int color = 0;
                        color = sourceImage.getRGB(region.minX + x - region.x, region.originalHeight - 1);
                        texturePackImage.setRGB(x,y,color);
                    }
                }
                // extrude left
                for (int x = region.x - options.extrude; x < region.x; x++) {
                    for (int y = region.y; y < region.y + region.packedHeight; y++) {
                        int color = 0;
                        color = sourceImage.getRGB(0, region.minY + y - region.y);
                        texturePackImage.setRGB(x,y,color);
                    }
                }
                // extrude right
                for (int x = region.x + region.packedWidth; x < region.x + region.packedWidth + options.extrude; x++) {
                    for (int y = region.y; y < region.y + region.packedHeight; y++) {
                        int color = 0;
                        color = sourceImage.getRGB(region.originalWidth - 1, region.minY + y - region.y);
                        texturePackImage.setRGB(x,y,color);
                    }
                }
            }
            // TODO: use asset utils.
            File outputFile = new File(options.outputDirectory + File.separator + options.outputName + "_" + texturePackImage.index + ".png");
            ImageIO.write(texturePackImage, "png", outputFile);
        }
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

        public final String name;
        public final int originalWidth;
        public final int originalHeight;
        public final int packedWidth;
        public final int packedHeight;
        public final int offsetX;
        public final int offsetY;
        private final int minX;
        private final int minY;
        public int x;
        public int y;
        public int textureIndex;

        private final int area;

        public PackedRegionData(BufferedImage sourceImage, String name, int packedWidth, int packedHeight, int offsetX, int offsetY, int minX, int minY) {
            this.name = name;
            this.originalWidth = sourceImage.getWidth();
            this.originalHeight = sourceImage.getHeight();
            this.packedWidth = packedWidth;
            this.packedHeight = packedHeight;
            this.area = packedWidth * packedHeight;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.minX = minX;
            this.minY = minY;
        }

        @Override
        public int compareTo(PackedRegionData o) {
            return Integer.compare(o.area, this.area);
        }

    }

    private static final class TextureData {
        public String file;
        public int width;
        public int height;
    }

    public static final class IndexedBufferedImage extends BufferedImage {

        private int index;

        private IndexedBufferedImage(int index, int width, int height) {
            super(width, height, BufferedImage.TYPE_INT_ARGB);
            this.index = index;
        }

    }

    public static final class Options {

        // TODO: allow rectangular packs.
        public enum Size {

            XX_SMALL_128(128),
            X_SMALL_256(256),
            SMALL_512(512),
            MEDIUM_1024(1024),
            LARGE_2048(2048),
            X_LARGE_4096(4096),
            XX_LARGE_8192(8192),
            ;

            public final int value;

            Size(int value) {
                this.value = value;
            }

        }

        public final String outputDirectory;
        public final String outputName;
        public final Texture.Filter magFilter;
        public final Texture.Filter minFilter;
        public final Texture.Wrap uWrap;
        public final Texture.Wrap vWrap;
        public final int extrude; // extrude REPEATS the border i.e. adding colors.
        public final int padding;
        public final int maxTexturesSize;

        public Options(String outputDirectory, String outputName) {
            this(outputDirectory, outputName, Texture.Filter.MIP_MAP_NEAREST_NEAREST, Texture.Filter.MIP_MAP_NEAREST_NEAREST, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1,1, Size.XX_LARGE_8192);
        }

        public Options(String outputDirectory, String outputName, Texture.Filter magFilter, Texture.Filter minFilter, Texture.Wrap uWrap, Texture.Wrap vWrap, int extrude, int padding, Size size) {
            this.outputDirectory = outputDirectory;
            this.outputName = outputName;
            this.magFilter = magFilter == null ? Texture.Filter.MIP_MAP_NEAREST_NEAREST : magFilter;
            this.minFilter = minFilter == null ? Texture.Filter.MIP_MAP_NEAREST_NEAREST : minFilter;
            this.uWrap = uWrap == null ? Texture.Wrap.CLAMP_TO_EDGE : uWrap;
            this.vWrap = vWrap == null ? Texture.Wrap.CLAMP_TO_EDGE : vWrap;
            this.extrude = Math.max(extrude, 0);
            this.padding = Math.max(padding, 0);
            this.maxTexturesSize = size.value;
        }

    }
}
