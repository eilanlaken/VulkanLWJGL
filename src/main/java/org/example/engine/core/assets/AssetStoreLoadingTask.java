package org.example.engine.core.assets;

import org.example.engine.core.async.AsyncTask;
import org.example.engine.core.collections.Array;

public class AssetStoreLoadingTask extends AsyncTask {

    private final AssetDescriptor descriptor;
    private Array<AssetDescriptor> dependencies;
    private final AssetLoader loader;

    AssetStoreLoadingTask(AssetDescriptor descriptor) {
        this.descriptor = descriptor;
        this.loader = AssetStore.getNewLoader(descriptor.type);
    }

    @Override
    public void task() {
        loader.asyncLoad(descriptor.path);
        this.dependencies = loader.getDependencies();
    }

    @Override
    public void onComplete() {
        if (dependencies == null) return;
        for (AssetDescriptor dependency : dependencies) AssetStore.loadAsset(dependency.type, dependency.path);
    }

    protected boolean ready() {
        if (!isComplete()) return false;
        if (dependencies == null || dependencies.size == 0) return true;
        return AssetStore.areLoaded(dependencies);
    }

    protected Asset create() {
        final Object obj = loader.create();
        final Array<Asset> assetDependencies = AssetStore.getDependencies(dependencies);
        return new Asset(obj, descriptor, assetDependencies);
    }
}
