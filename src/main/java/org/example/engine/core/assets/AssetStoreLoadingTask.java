package org.example.engine.core.assets;

import org.example.engine.core.async.Task;
import org.example.engine.core.collections.Array;

public class AssetStoreLoadingTask extends Task {

    private boolean loadDataFromDiscComplete;
    private final AssetDescriptor descriptor;
    private Array<AssetDescriptor> dependencies;
    private final AssetLoader loader;

    AssetStoreLoadingTask(AssetDescriptor descriptor) {
        this.descriptor = descriptor;
        this.loader = AssetStore.getNewLoader(descriptor.type);
        this.loadDataFromDiscComplete = false;
    }

    @Override
    public void run() {
        loader.asyncLoad(descriptor.path);
        this.dependencies = loader.getDependencies();
        System.out.println("end of run: " + this.dependencies);
    }

    @Override
    public void onComplete() {

        loadDataFromDiscComplete = true;
        if (dependencies == null) return;
        for (AssetDescriptor dependency : dependencies) AssetStore.loadAsset(dependency.type, dependency.path);
    }

    protected boolean dependenciesLoaded() {
        if (dependencies == null || dependencies.size == 0) return true;
        return AssetStore.areLoaded(dependencies);
    }

    public boolean isLoadDataFromDiscComplete() {
        return loadDataFromDiscComplete;
    }

    protected Asset create() {
        System.out.println("FROM CREATEEEEE: " + Thread.currentThread().getName());
        final Object obj = loader.create();
        final Array<Asset> assetDependencies = AssetStore.getDependencies(dependencies);
        Asset asset = new Asset(obj, descriptor, assetDependencies);
        return asset;
    }
}
