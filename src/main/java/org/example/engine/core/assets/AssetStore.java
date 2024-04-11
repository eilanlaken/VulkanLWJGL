package org.example.engine.core.assets;

import org.example.engine.core.async.AsyncTaskRunner;
import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.collections.CollectionsQueue;
import org.example.engine.core.graphics.Model;
import org.example.engine.core.graphics.ShaderProgram;
import org.example.engine.core.graphics.Texture;
import org.example.engine.core.graphics.TexturePack;
import org.example.engine.core.memory.MemoryResource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class AssetStore {

    private final static HashMap<Class<? extends MemoryResource>, Class<? extends AssetLoader<? extends MemoryResource>>> loaders = getLoadersMap();
    private static final CollectionsQueue<AssetDescriptor> loadQueue = new CollectionsQueue<>();
    private static final HashMap<String, Asset> store = new HashMap<>();
    private static final Set<AssetStoreLoadingTask> completedAsyncTasks = new HashSet<>();
    private static final Set<AssetStoreLoadingTask> asyncTasks = new HashSet<>();
    private static final Set<AssetStoreLoadingTask> completedCreateTasks = new HashSet<>();
    private static final Set<AssetStoreLoadingTask> createTasks = new HashSet<>();

    // loading state

    public static synchronized void update() {
        for (AssetStoreLoadingTask task : asyncTasks) {
            if (task.ready())  {
                completedAsyncTasks.add(task);
                createTasks.add(task);
            }
        }

        asyncTasks.removeAll(completedAsyncTasks);
        for (AssetDescriptor descriptor : loadQueue) {
            AssetStoreLoadingTask task = new AssetStoreLoadingTask(descriptor);
            asyncTasks.add(task);
            AsyncTaskRunner.runAsync(task);
        }
        loadQueue.clear();

        createTasks.removeAll(completedCreateTasks);
        for (AssetStoreLoadingTask task : createTasks) {
            Asset asset = task.create();
            AssetStore.store(asset);
            completedCreateTasks.add(task);
        }
    }

    protected static void store(final Asset asset) {
        store.put(asset.descriptor.path, asset);
    }

    protected static synchronized CollectionsArray<Asset> getDependencies(final CollectionsArray<AssetDescriptor> dependencies) {
        CollectionsArray<Asset> assets = new CollectionsArray<>();
        if (dependencies != null) {
            for (AssetDescriptor dependency : dependencies) {
                assets.add(store.get(dependency.path));
            }
        }
        return assets;
    }

    protected static synchronized boolean areLoaded(final CollectionsArray<AssetDescriptor> dependencies) {
        if (dependencies == null || dependencies.size == 0) return true;
        for (AssetDescriptor dependency : dependencies) {
            Asset asset = store.get(dependency.path);
            if (asset == null) return false;
        }
        return true;
    }

    public static synchronized boolean isLoaded(final String path) {
        return store.get(path) != null;
    }

    public static synchronized void loadAsset(final Class<? extends MemoryResource> type, final String path) {
        final Asset asset = store.get(path);
        if (asset != null) {
            asset.refCount++;
            return;
        }
        AssetDescriptor descriptor = new AssetDescriptor(type, path);
        loadQueue.addFirst(descriptor);
    }

    public static synchronized void unloadAsset(final String path) {

    }

    public static synchronized <T extends MemoryResource> T get(final String path) {
        return (T) store.get(path).obj;
    }

    public static synchronized Asset getAsset(final String path) {
        return store.get(path);
    }

    public static synchronized void clean() {

    }

    public static long getTotalStorageBytes() {
        long total = 0;
        for (Map.Entry<String, Asset> assetEntry : store.entrySet()) {
            total += assetEntry.getValue().descriptor.size;
        }
        return total;
    }

    public static boolean isLoadingInProgress() {
        return !loadQueue.isEmpty() || !asyncTasks.isEmpty() || !createTasks.isEmpty();
    }

    protected static synchronized AssetLoader<? extends MemoryResource> getNewLoader(Class<? extends MemoryResource> type) {
        Class<? extends AssetLoader<? extends MemoryResource>> loaderClass = AssetStore.loaders.get(type);
        AssetLoader<? extends MemoryResource> loaderInstance;
        try {
            Constructor<?> constructor = loaderClass.getConstructor();
            loaderInstance = (AssetLoader<? extends MemoryResource>) constructor.newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException  | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not get loader for type: " + type.getSimpleName());
        }
        return loaderInstance;
    }

    private static HashMap<Class<? extends MemoryResource>, Class<? extends AssetLoader<? extends MemoryResource>>> getLoadersMap() {
        HashMap<Class<? extends MemoryResource>, Class<? extends AssetLoader<? extends MemoryResource>>> loaders = new HashMap<>();
        loaders.put(Texture.class, AssetLoaderTexture.class);
        loaders.put(Model.class, AssetLoaderModel.class);
        loaders.put(ShaderProgram.class, AssetLoaderShaderProgram.class);
        loaders.put(TexturePack.class, AssetLoaderTexturePack.class);
        return loaders;
    }

}
