package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;

// TODO: implement
public class TexturePack {

    public final Texture[] textures;
    public final Array<TexturePackRegion> regions;
    protected final TexturePacker.Options options;

    protected TexturePack(final Texture[] textures, final Array<TexturePackRegion> regions, final TexturePacker.Options options) {
        this.textures = textures;
        this.regions = regions;
        this.options = options;
    }

}
