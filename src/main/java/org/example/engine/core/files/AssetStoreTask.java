package org.example.engine.core.files;

import org.example.engine.core.collections.Array;

class AssetStoreTask implements Runnable {

    final AssetDescriptor descriptor;
    final AssetLoader loader;
    final long startTime;
    private volatile boolean asyncDone;
    private volatile boolean dependenciesLoaded;
    private volatile Array<AssetDescriptor> dependencies;
    //private volatile AsyncResult<Void> depsFuture;
    //volatile AsyncResult<Void> loadFuture;
    private volatile Asset asset;
    private volatile boolean cancel;

    AssetStoreTask(final AssetDescriptor descriptor) {
        this.descriptor = descriptor;
        this.loader = AssetStore.getNewLoader(descriptor.type);
        startTime = System.nanoTime();
    }

    @Override
    public void run() {
        if (cancel) return;
        if (!dependenciesLoaded) {
            dependencies = loader.getDependencies(descriptor.path);
            if (dependencies != null) {
                dependencies.removeDuplicates(false);
                //manager.injectDependencies(descriptor.fileName, dependencies);
            } else {
                loader.asyncLoad(descriptor.path);
                asyncDone = true;
            }
        } else {
            loader.asyncLoad(descriptor.path);
            asyncDone = true;
        }
    }
}
