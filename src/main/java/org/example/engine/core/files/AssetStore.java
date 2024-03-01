package org.example.engine.core.files;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.Queue;
import org.example.engine.core.graphics.Model;
import org.example.engine.core.graphics.ShaderProgram;
import org.example.engine.core.graphics.Texture;
import org.example.engine.core.memory.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

// TODO: make async (later)
public final class AssetStore {

    private static HashMap<Class<?>, AssetLoader<?>> loaders;
    static {
        loaders = new HashMap<>();
        loaders.put(Texture.class, new AssetLoaderTexture());
        loaders.put(Model.class, new AssetLoaderModel());
        loaders.put(ShaderProgram.class, new AssetLoaderShaderProgram());
    }
    private static volatile float progress = 0;
    private static boolean doneLoading = false;
    private static long totalBytesLoadQueue = 0;
    private static volatile Queue<AssetDescriptor> loadQueue = new Queue<>();
    private static volatile HashMap<String, Asset> store = new HashMap<>();

    public static synchronized void put(final Class<?> type, final String path) {
        Asset asset = store.get(path);
        if (asset != null) asset.refCount++;
        try {
            AssetDescriptor descriptor = new AssetDescriptor(type, path);
            loadQueue.addFirst(descriptor);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        for (Map.Entry<String, Asset> entry : store.entrySet()) {
            entry.getValue().free();
        }
        store.clear();
    }
}
