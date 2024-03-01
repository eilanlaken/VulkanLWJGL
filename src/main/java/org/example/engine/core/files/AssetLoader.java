package org.example.engine.core.files;

public interface AssetLoader<T> {

    default T load(final String... path) {
        return load(path[0]);
    }

    T load(final String path);

}
