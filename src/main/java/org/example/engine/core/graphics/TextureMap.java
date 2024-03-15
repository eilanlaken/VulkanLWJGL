package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;

// TODO: implement
public class TextureMap {

    public final Texture[] textures;
    public final Array<TextureMapRegion> regions;
    protected final TextureMapGenerator.Options options;

    protected TextureMap(final Texture[] textures, final Array<TextureMapRegion> regions, final TextureMapGenerator.Options options) {
        this.textures = textures;
        this.regions = regions;
        this.options = options;
    }

}
