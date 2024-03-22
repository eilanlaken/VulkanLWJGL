package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.TexturePack;

// TODO: implement. Finalize AssetStore.
public class AssetLoaderTexturePack implements AssetLoader<TexturePack> {

    @Override
    public Array<AssetDescriptor> getDependencies() {
        return null;
    }

    @Override
    public void asyncLoad(String path) {

    }

    @Override
    public TexturePack create() {
        return null;
    }

}
