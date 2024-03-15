package org.example.engine.core.graphics;

import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.collections.Array;

public class TextureMapGenerator {

    // TODO: implement
    public static TextureMap packDirectory(final Options options, final String directory) {

        return new TextureMap(null, null);
    }

    // TODO: implement
    public static TextureMap packTextures(final Options options, final String ...texturePaths) {

        return new TextureMap(null, null);
    }

    public static TextureMap packDirectory(final String outputName, final String directory) {
        if (directory == null) throw new IllegalArgumentException("Must provide valid directory name");
        if (!AssetUtils.directoryExists(directory)) throw new IllegalArgumentException("The provided path: " + directory + " does not exist, or is not a directory");


        Options options = new Options();
        options.outputName = outputName;

        return new TextureMap(null, null);
    }

    public static TextureMap packTextures(final String outputName, final String ...texturePaths) {

        return new TextureMap(null, null);
    }

    private static boolean alreadyPacked(final Options options, final String ...texturePaths) {
        final String outputPath = options.outputPath;
        final String outputName = options.outputName;

        return false;
    }


    public static final class Options {

        public String outputPath;
        public String outputName;
        public boolean createDebugRectangles;
        public TextureParamFilter filter;
        public TextureParamWrap wrap;
        public boolean extrude;

    }


}
