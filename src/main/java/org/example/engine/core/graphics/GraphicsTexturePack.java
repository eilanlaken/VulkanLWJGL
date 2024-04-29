package org.example.engine.core.graphics;

import org.example.engine.core.memory.MemoryResource;

import java.util.HashMap;

// can contain duplicates.
// example:
// named regions: <my_anim_1->region1, my_anim_2->region2>
// named animations: <my_anim->[region1,region2]>
public class GraphicsTexturePack implements MemoryResource {

    protected final GraphicsTexture[] textures;
    protected final GraphicsTexturePacker.Options options;
    protected final HashMap<String, GraphicsTextureRegion> namedRegions;
    protected final HashMap<String, GraphicsTextureRegion[]> namedAnimations;

    protected GraphicsTexturePack(final GraphicsTexture[] textures, final GraphicsTexturePacker.Options options, final HashMap<String, GraphicsTextureRegion> namedRegions, final HashMap<String, GraphicsTextureRegion[]> namedAnimations) {
        this.textures = textures;
        this.options = options;
        this.namedRegions = namedRegions;
        this.namedAnimations = namedAnimations;
    }

    public GraphicsTextureRegion getRegion(final String name) {
        final GraphicsTextureRegion region = namedRegions.get(name);
        if (region == null) throw new RuntimeException(GraphicsTexturePack.class.getSimpleName() + " " + this.options.outputName + " does not contain a region named " + name);
        return region;
    }

    public GraphicsTextureRegion[] getAnimationFrames(final String name) {
        final GraphicsTextureRegion[] regions = namedAnimations.get(name);
        if (regions == null) throw new RuntimeException(GraphicsTexturePack.class.getSimpleName() + " " + this.options.outputName + " does not contain an animation named " + name);
        return regions;
    }

    @Override
    public void delete() {
        // TODO: see how should be implemented.
    }
}
