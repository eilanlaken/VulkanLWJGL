package org.example.engine.core.graphics;

import org.example.engine.core.memory.MemoryResource;

import java.util.HashMap;

// can contain duplicates.
// example:
// named regions: <my_anim_1->region1, my_anim_2->region2>
// named animations: <my_anim->[region1,region2]>
public class TexturePack implements MemoryResource {

    protected final Texture[] textures;
    protected final TexturePacker.Options options;
    protected final HashMap<String, TextureRegion> namedRegions;
    protected final HashMap<String, TextureRegion[]> namedAnimations;

    protected TexturePack(final Texture[] textures, final TexturePacker.Options options, final HashMap<String, TextureRegion> namedRegions, final HashMap<String, TextureRegion[]> namedAnimations) {
        this.textures = textures;
        this.options = options;
        this.namedRegions = namedRegions;
        this.namedAnimations = namedAnimations;
    }

    public TextureRegion getRegion(final String name) {
        final TextureRegion region = namedRegions.get(name);
        if (region == null) throw new RuntimeException(TexturePack.class.getSimpleName() + " " + this.options.outputName + " does not contain a region named " + name);
        return region;
    }

    public TextureRegion[] getAnimationFrames(final String name) {
        final TextureRegion[] regions = namedAnimations.get(name);
        if (regions == null) throw new RuntimeException(TexturePack.class.getSimpleName() + " " + this.options.outputName + " does not contain an animation named " + name);
        return regions;
    }

    @Override
    public void delete() {
        // TODO: see how should be implemented.
    }
}
