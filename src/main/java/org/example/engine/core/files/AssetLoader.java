package org.example.engine.core.files;

public interface AssetLoader<T> {

    void asyncLoad(final String path);

    T create(String path);

}
