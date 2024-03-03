package org.example.engine.core.assets;

import org.example.engine.core.async.Task;
import org.example.engine.core.collections.Array;

public class AssetStoreLoadingTask extends Task {

    private boolean asyncFinished;
    private final AssetDescriptor descriptor;
    private Array<AssetDescriptor> dependencies;
    private final AssetLoader loader;

    AssetStoreLoadingTask(AssetDescriptor descriptor) {
        this.descriptor = descriptor;
        this.loader = AssetStore.getNewLoader(descriptor.type);
        this.asyncFinished = false;
    }

    @Override
    public void run() {
        loader.asyncLoad(descriptor.path);
        this.dependencies = loader.getDependencies();
    }

    @Override
    public void onComplete() {
        asyncFinished = true;
        if (dependencies == null) return;
        for (AssetDescriptor dependency : dependencies) AssetStore.loadAsset(dependency.type, dependency.path);
    }

    protected boolean dependenciesLoaded() {
        if (dependencies == null || dependencies.size == 0) return true;
        return AssetStore.areLoaded(dependencies);
    }

    public boolean asyncFinished() {
        return asyncFinished;
    }

    protected Asset create() {
        final Object obj = loader.create();
        final Array<Asset> assetDependencies = AssetStore.getDependencies(dependencies);
        return new Asset(obj, descriptor, assetDependencies);
    }
}
