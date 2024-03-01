package org.example.engine.core.files;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.Queue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;

// TODO: make async
public final class AssetStore {


    private float progress = 0;
    private boolean doneLoading = false;
    private long totalBytesLoadQueue = 0;
    private static Queue<AssetDescriptor> loadQueue = new Queue<>();

    private static HashMap<String[], Object> storage = new HashMap<>();

    public static void load(final Class<? extends Object> type, final String ...paths) {

        try {
            AssetDescriptor descriptor = new AssetDescriptor(type, paths);
            loadQueue.addFirst(descriptor);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // TODO: implement.
    public static void update(float deltaTime) {

    }

    public Object get(final String... paths) {

        return null;
    }

}
