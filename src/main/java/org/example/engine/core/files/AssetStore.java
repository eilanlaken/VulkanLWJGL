package org.example.engine.core.files;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.Queue;
import org.example.engine.core.graphics.Model;
import org.example.engine.core.graphics.ShaderProgram;
import org.example.engine.core.graphics.Texture;
import org.example.engine.core.memory.Resource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

// TODO: make async (later)
public final class AssetStore {

    private static HashMap<Class<? extends Resource>, Class<? extends AssetLoader<? extends Resource>>> loaders;
    static {
        loaders = new HashMap<>();
        loaders.put(Texture.class, AssetLoaderTexture.class);
        loaders.put(Model.class, AssetLoaderModel.class);
        loaders.put(ShaderProgram.class, AssetLoaderShaderProgram.class);
    }
    private static volatile float progress = 0;
    private static boolean doneLoading = false;
    private static volatile long totalBytesLoadQueue = 0;
    private static volatile Queue<AssetDescriptor> loadQueue = new Queue<>();
    private static volatile HashMap<String, Asset> store = new HashMap<>();

    public static synchronized void put(final Class<?> type, final String path) {

    }

    public static synchronized void discard(final String path) {

    }

    public static synchronized boolean load(float deltaTime) {

        return doneLoading;
    }

    public static synchronized <T> T get(final String path) {

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

}
