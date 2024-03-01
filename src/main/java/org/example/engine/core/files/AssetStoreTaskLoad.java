package org.example.engine.core.files;

import org.example.engine.core.memory.Resource;

public class AssetStoreTaskLoad {

    private Thread thread;

    AssetStoreTaskLoad(final AssetDescriptor assetDescriptor) {
        AssetLoader<? extends Resource> loader = AssetStore.getNewLoader(assetDescriptor.type);
        this.thread = new Thread(() -> loader.asyncLoad(assetDescriptor.path));
    }

}
