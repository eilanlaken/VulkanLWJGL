package org.example.engine.core.graphics;

public final class TexturePackerOptions {

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
    public final TextureParamFilter magFilter;
    public final TextureParamFilter minFilter;
    public final TextureParamWrap uWrap;
    public final TextureParamWrap vWrap;
    public final int extrude; // extrude REPEATS the border i.e. adding colors.
    public final int padding;
    public final int maxTexturesSize;

    public TexturePackerOptions(String outputDirectory, String outputName) {
        this(outputDirectory, outputName, TextureParamFilter.MIP_MAP_NEAREST_NEAREST, TextureParamFilter.MIP_MAP_NEAREST_NEAREST, TextureParamWrap.CLAMP_TO_EDGE, TextureParamWrap.CLAMP_TO_EDGE, 1,1, Size.MEDIUM_1024);
    }

    public TexturePackerOptions(String outputDirectory, String outputName, TextureParamFilter magFilter, TextureParamFilter minFilter, TextureParamWrap uWrap, TextureParamWrap vWrap, int extrude, int padding, Size size) {
        this.outputDirectory = outputDirectory;
        this.outputName = outputName;
        this.magFilter = magFilter == null ? TextureParamFilter.MIP_MAP_NEAREST_NEAREST : magFilter;
        this.minFilter = minFilter == null ? TextureParamFilter.MIP_MAP_NEAREST_NEAREST : minFilter;
        this.uWrap = uWrap == null ? TextureParamWrap.CLAMP_TO_EDGE : uWrap;
        this.vWrap = vWrap == null ? TextureParamWrap.CLAMP_TO_EDGE : vWrap;
        this.extrude = Math.max(extrude, 0);
        this.padding = Math.max(padding, 0);
        this.maxTexturesSize = size.value;
    }

}
