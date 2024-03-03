package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;

public class AssetLoaderDebug implements AssetLoader<Debug> {

    @Override
    public Array<AssetDescriptor> getDependencies() {
        return null;
    }

    @Override
    public void asyncLoad(String path) {

    }

    @Override
    public Debug create() {
        return null;
    }
}
