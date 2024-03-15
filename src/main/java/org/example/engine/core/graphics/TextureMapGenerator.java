package org.example.engine.core.graphics;

import com.google.gson.Gson;
import org.example.engine.core.assets.AssetUtils;

import java.io.File;

public class TextureMapGenerator {

    // TODO: implement
    public static TextureMap packDirectory(final Options options, final String directory) {

        return new TextureMap(null, null, null);
    }

    // TODO: implement
    public static TextureMap packTextures(final Options options, final String ...texturePaths) {

        return new TextureMap(null, null, null);
    }

    public static TextureMap packDirectory(final String outputName, final String directory, final boolean recursive) {
        if (directory == null) throw new IllegalArgumentException("Must provide valid directory name");
        if (!AssetUtils.directoryExists(directory)) throw new IllegalArgumentException("The provided path: " + directory + " does not exist, or is not a directory");


        Options options = new Options();
        options.outputName = outputName;

        return new TextureMap(null, null, null);
    }

    public static TextureMap packTextures(final String outputName, final String ...texturePaths) {

        return new TextureMap(null, null, null);
    }

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
        Gson gson = new Gson();
        // TODO: continue.


        return false;
    }


    public static final class Options {

        public String outputDirectory;
        public String outputName;
        public boolean createDebugRectangles;
        public TextureParamFilter filter;
        public TextureParamWrap wrap;
        public boolean extrude;

    }


}
