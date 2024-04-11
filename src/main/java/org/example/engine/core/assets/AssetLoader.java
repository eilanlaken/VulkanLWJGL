package org.example.engine.core.assets;

import org.example.engine.core.collections.CollectionsArray;

public interface AssetLoader<T> {

    CollectionsArray<AssetDescriptor> getDependencies();
    void asyncLoad(final String path);
    T create();

}
