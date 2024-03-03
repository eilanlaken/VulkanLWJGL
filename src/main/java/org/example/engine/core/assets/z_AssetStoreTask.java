package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;

class z_AssetStoreTask {

    final AssetDescriptor descriptor;
    final AssetLoader loader;
    final long startTime;
    private volatile Array<AssetDescriptor> dependencies;
    private volatile Asset asset;
    private volatile boolean cancel;

    z_AssetStoreTask(final AssetDescriptor descriptor) {
        this.descriptor = descriptor;
        this.loader = z_AssetStore.getNewLoader(descriptor.type);
        startTime = System.nanoTime();
    }

}
