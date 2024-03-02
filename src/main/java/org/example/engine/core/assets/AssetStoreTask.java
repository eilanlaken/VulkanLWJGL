package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;

class AssetStoreTask implements Runnable {

    final AssetDescriptor descriptor;
    final AssetLoader loader;
    final long startTime;
    private volatile Array<AssetDescriptor> dependencies;
    private volatile Asset asset;
    private volatile boolean cancel;

    AssetStoreTask(final AssetDescriptor descriptor) {
        this.descriptor = descriptor;
        this.loader = AssetStore.getNewLoader(descriptor.type);
        startTime = System.nanoTime();
    }

    @Override
    public void run() {

    }
}
