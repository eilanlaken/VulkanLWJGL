package org.example.engine.core.assets;

import org.example.engine.core.async.Task;
import org.example.engine.core.async.TaskRunner;
import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.Queue;
import org.example.engine.core.graphics.Model;
import org.example.engine.core.graphics.ShaderProgram;
import org.example.engine.core.graphics.Texture;
import org.example.engine.core.memory.Resource;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public final class AssetStore {

    private final static HashMap<Class<? extends Resource>, Class<? extends AssetLoader<? extends Resource>>> loaders = getLoadersMap();
    private static volatile Queue<AssetDescriptor> loadQueue = new Queue<>();
    private static volatile HashMap<String, Asset> store = new HashMap<>();

    private static volatile Set<AssetStoreLoadingTask> completedAsyncTasks = new HashSet<>();
    private static volatile Set<AssetStoreLoadingTask> parallelLoadTasks = new HashSet<>();

    private static volatile Set<AssetStoreLoadingTask> completedSyncTasks = new HashSet<>();
    private static volatile Set<AssetStoreLoadingTask> inOrderLoadTasks = new HashSet<>();

    public static synchronized void update() {
        parallelLoadTasks.removeAll(completedAsyncTasks);
        for (AssetDescriptor descriptor : loadQueue) {
            AssetStoreLoadingTask task = new AssetStoreLoadingTask(descriptor);
            parallelLoadTasks.add(task);
            TaskRunner.runAsync(task);
        }
        loadQueue.clear();

        inOrderLoadTasks.removeAll(completedSyncTasks);
        for (AssetStoreLoadingTask task : inOrderLoadTasks) {
            if (task.readyForCreation()) {
                Asset asset = task.create();
                AssetStore.store(asset);
                completedSyncTasks.add(task);
            }
        }
    }

    protected static void store(final Asset asset) {
        store.put(asset.descriptor.path, asset);
    }

    protected static synchronized Array<Asset> getDependencies(final Array<AssetDescriptor> dependencies) {
        Array<Asset> assets = new Array<>();
        for (AssetDescriptor dependency : dependencies) {
            assets.add(store.get(dependency.path));
        }
        return assets;
    }

    protected static synchronized boolean areLoaded(final Array<AssetDescriptor> dependencies) {
        for (AssetDescriptor dependency : dependencies) {
            Asset asset = store.get(dependency.path);
            if (asset == null) return false;
        }
        return true;
    }

    public static synchronized boolean isLoaded(final String path) {
        return store.get(path) != null;
    }

    public static synchronized void loadAsset(final Class<? extends Resource> type, final String path) {
        final Asset asset = store.get(path);
        if (asset != null) {
            asset.refCount++;
            return;
        }
        try {
            AssetDescriptor descriptor = new AssetDescriptor(type, path);
            loadQueue.addFirst(descriptor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void unloadAsset(final String path) {

    }

    public static synchronized <T> T getAsset(final String path) {

        return null;
    }

    public static synchronized void clean() {

    }

    protected static synchronized AssetLoader<? extends Resource> getNewLoader(Class<? extends Resource> type) {
        Class<? extends AssetLoader<? extends Resource>> loaderClass = AssetStore.loaders.get(type);
        AssetLoader<? extends Resource> loaderInstance;
        try {
            Constructor<?> constructor = loaderClass.getConstructor(String.class);
            loaderInstance = (AssetLoader<? extends Resource>) constructor.newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException  | InvocationTargetException e) {
            throw new RuntimeException("Could not get loader for type: " + type.getSimpleName());
        }
        return loaderInstance;
    }

    private static HashMap<Class<? extends Resource>, Class<? extends AssetLoader<? extends Resource>>> getLoadersMap() {
        HashMap<Class<? extends Resource>, Class<? extends AssetLoader<? extends Resource>>> loaders = new HashMap<>();
        loaders.put(Texture.class, AssetLoaderTexture.class);
        loaders.put(Model.class, AssetLoaderModel.class);
        loaders.put(ShaderProgram.class, AssetLoaderShaderProgram.class);
        return loaders;
    }

}
