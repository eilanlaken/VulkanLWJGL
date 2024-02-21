package org.example.engine.core.files;

public abstract class AssetLoader<T> {
    public abstract T load(final String path);

}
