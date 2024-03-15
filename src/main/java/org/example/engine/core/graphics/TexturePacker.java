package org.example.engine.core.graphics;

import org.example.engine.core.assets.AssetUtils;

import java.io.File;

public class TexturePacker {

    // TODO: implement
    public static TexturePack packDirectory(final Options options, final String directory) {

        return new TexturePack(null, null, null, null);
    }

    // TODO: implement
    public static TexturePack packTextures(final Options options, final String ...texturePaths) {

        return new TexturePack(null, null, null, null);
    }

    public static TexturePack packDirectory(final String outputName, final String directory, final boolean recursive) {
        if (directory == null) throw new IllegalArgumentException("Must provide non-null directory name.");
        if (!AssetUtils.directoryExists(directory)) throw new IllegalArgumentException("The provided path: " + directory + " does not exist, or is not a directory");


        return new TexturePack(null, null, null, null);
    }

    public static TexturePack packTextures(final String directory, final String outputName, final String ...texturePaths) {

        return new TexturePack(null, null, null, null);
    }

    // TODO: implement.
    private static boolean alreadyPacked(final Options options, final String ...texturePaths) {
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


    public static final class Options {

        public final String outputDirectory;
        public final String outputName;
        public final TextureParamFilter magFilter;
        public final TextureParamFilter minFilter;
        public final TextureParamWrap uWrap;
        public final TextureParamWrap vWrap;
        public final int extrude; // extrude REPEATS the border i.e. adding colors.
        public final int padding;

        public Options(String outputDirectory, String outputName) {
            this(outputDirectory, outputName, TextureParamFilter.MIP_MAP_NEAREST_NEAREST, TextureParamFilter.MIP_MAP_NEAREST_NEAREST, TextureParamWrap.CLAMP_TO_EDGE, TextureParamWrap.CLAMP_TO_EDGE, 1,1);
        }

        public Options(String outputDirectory, String outputName, TextureParamFilter magFilter, TextureParamFilter minFilter, TextureParamWrap uWrap, TextureParamWrap vWrap, int extrude, int padding) {
            this.outputDirectory = outputDirectory;
            this.outputName = outputName;
            this.magFilter = magFilter;
            this.minFilter = minFilter;
            this.uWrap = uWrap;
            this.vWrap = vWrap;
            this.extrude = extrude;
            this.padding = padding;
        }
    }


}
