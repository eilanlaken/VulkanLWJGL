package org.example.engine.core.graphics;

import com.google.gson.Gson;
import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.collections.Array;
import org.lwjgl.stb.STBRPContext;
import org.lwjgl.stb.STBRPNode;
import org.lwjgl.stb.STBRPRect;
import org.lwjgl.stb.STBRectPack;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class TexturePacker {

    // TODO: refactor the yaml and json to a higher level usage.
    private static final Yaml yaml;
    private static final Gson gson = new Gson();

    static {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Representer representer = new Representer(dumperOptions) {
            @Override
            protected MappingNode representJavaBean(Set<Property> properties, Object obj) {
                if (!classTags.containsKey(obj.getClass())) addClassTag(obj.getClass(), Tag.MAP);
                return super.representJavaBean(properties, obj);
            }
        };
        yaml = new Yaml(representer);
    }

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

        int i = 0;
        for (Map.Entry<BufferedImage, Array<PackedRegionData>> entry : texturePack.entrySet()) {
            Array<PackedRegionData> regions = entry.getValue();
            for (PackedRegionData regionData : regions) regionData.textureIndex = i;
            i++;
        }

        generatePackFile(options, texturePack);
        generatePackTextureFiles(options, texturePack);
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
        int offsetY = originalHeight - packedHeight - minY;
        System.out.println("orig: w, h: " + originalWidth + ", " + originalHeight);
        System.out.println("packed: w, h: " + packedWidth + ", " + packedHeight);
        System.out.println("offset x, y: " + offsetX + ", " + offsetY);
        return new PackedRegionData(sourceImage, path, packedWidth, packedHeight, offsetX, offsetY);
    }

    private static synchronized void generatePackFile(final TexturePackerOptions options, Map<BufferedImage, Array<PackedRegionData>> texturePack) {
        String outputName = options.outputName;
        //Map<String, Object> texturesData = new LinkedHashMap<String, Object>();
        //texturesData.put("k", new int[] {1,2,3});
        TextureData[] texturesData = new TextureData[texturePack.size()];
        int i = 0;
        for (BufferedImage bufferedImage : texturePack.keySet()) {
            texturesData[i] = new TextureData();
            texturesData[i].file = outputName + "_" + i + ".png";
            texturesData[i].width = bufferedImage.getWidth();
            texturesData[i].height = bufferedImage.getHeight();
        }
        List<TextureData> list = List.of(texturesData);

        Map<String, Object> optionsData = new LinkedHashMap<String, Object>();
        optionsData.put("extrude", options.extrude);
        optionsData.put("padding", options.padding);
        optionsData.put("maxTextureSize", options.maxTexturesSize);
        optionsData.put("magFilter", options.magFilter.name());
        optionsData.put("minFilter", options.minFilter.name());
        optionsData.put("uWrap", options.uWrap.name());
        optionsData.put("vWrap", options.vWrap.name());


        Map<String, Object> yamlData = new LinkedHashMap<String, Object>();

        yamlData.put("textures", list);
        yamlData.put("options", optionsData);


        String s = yaml.dump(yamlData);
        System.out.println(s);
    }

    private static synchronized void generatePackTextureFiles(final TexturePackerOptions options, Map<BufferedImage, Array<PackedRegionData>> texturePack) {

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

    private static final class TextureData {
        public String file;
        public int width;
        public int height;

        @Override
        public String toString() {
            return "TextureData{"+"file="+file+
                    ",width="+width+
                    ",height="+height+"}";
        }
    }

}
