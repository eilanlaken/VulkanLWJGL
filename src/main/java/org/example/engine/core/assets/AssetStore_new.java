package org.example.engine.core.assets;

import org.example.engine.core.async.Task;
import org.example.engine.core.collections.Queue;
import org.example.engine.core.graphics.Model;
import org.example.engine.core.graphics.ShaderProgram;
import org.example.engine.core.graphics.Texture;
import org.example.engine.core.memory.Resource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public final class AssetStore_new {

    private final static HashMap<Class<? extends Resource>, Class<? extends AssetLoader<? extends Resource>>> loaders = getLoadersMap();
    private static volatile Queue<AssetDescriptor> loadQueue = new Queue<>();
    private static volatile HashMap<String, Asset> store = new HashMap<>();

    private static volatile Set<Task> parallelLoadTasks = new HashSet<>();
    private static volatile Set<Task> inOrderLoadTasks = new HashSet<>();

    public static void update() {

    }

    public static synchronized void loadAsset(final Class<?> type, final String path) {

    }

    public static synchronized void unloadAsset(final String path) {

    }

    public static synchronized <T> T getAsset(final String path) {

        return null;
    }

    public static synchronized void clean() {

    }

    protected static synchronized AssetLoader<? extends Resource> getNewLoader(Class<? extends Resource> type) {
        Class<? extends AssetLoader<? extends Resource>> loaderClass = AssetStore_new.loaders.get(type);
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
