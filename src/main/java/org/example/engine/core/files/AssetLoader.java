package org.example.engine.core.files;

public interface AssetLoader<T> {

    T load(final String path);

}
