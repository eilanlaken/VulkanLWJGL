package org.example.engine.core.files;

import org.example.engine.core.collections.Array;

public interface AssetLoader<T> {

    Array<AssetDescriptor> getDependencies(String path);
    void asyncLoad(final String path);
    T create(String path);

}
