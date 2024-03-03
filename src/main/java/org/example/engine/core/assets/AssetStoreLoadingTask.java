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

        if (dependencies == null) return;
        for (AssetDescriptor dependency : dependencies) AssetStore.loadAsset(dependency.type, dependency.path);
        loadDataFromDiscComplete = true;
    }

    protected boolean readyForCreation() {
        System.out.println(dependencies);
        return AssetStore.areLoaded(dependencies);
    }

    public boolean isLoadDataFromDiscComplete() {
        return loadDataFromDiscComplete;
    }

    protected Asset create() {
        final Object obj = loader.create();
        final Array<Asset> assetDependencies = AssetStore.getDependencies(dependencies);
        Asset asset = new Asset(obj, descriptor, assetDependencies);
        return asset;
    }
}
