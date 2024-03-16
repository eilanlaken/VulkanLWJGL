package org.example.engine.core.graphics;

import java.util.HashMap;

// can contain duplicates.
// example:
// named regions: <my_anim_1->region1, my_anim_2->region2>
// named animations: <my_anim->[region1,region2]>
public class TexturePack {

    protected final Texture[] textures;
    protected final TexturePackerOptions options;
    protected final HashMap<String, TexturePackRegion> namedRegions;
    protected final HashMap<String, TexturePackRegion[]> namedAnimations;

    protected TexturePack(final Texture[] textures, final TexturePackerOptions options, final HashMap<String, TexturePackRegion> namedRegions, final HashMap<String, TexturePackRegion[]> namedAnimations) {
        this.textures = textures;
        this.options = options;
        this.namedRegions = namedRegions;
        this.namedAnimations = namedAnimations;
    }

    public TexturePackRegion getRegion(final String name) {
        final TexturePackRegion region = namedRegions.get(name);
        if (region == null) throw new RuntimeException(TexturePack.class.getSimpleName() + " " + this.options.outputName + " does not contain a region named " + name);
        return region;
    }

    public TexturePackRegion[] getAnimationFrames(final String name) {
        final TexturePackRegion[] regions = namedAnimations.get(name);
        if (regions == null) throw new RuntimeException(TexturePack.class.getSimpleName() + " " + this.options.outputName + " does not contain an animation named " + name);
        return regions;
    }

}
