package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;

public interface AssetLoader<T> {

    Array<AssetDescriptor> getDependencies();
    void asyncLoad(final String path);
    T create();

}
