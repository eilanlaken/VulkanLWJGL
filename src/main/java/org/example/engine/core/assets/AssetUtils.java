package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.Window;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public final class AssetUtils {

    private static boolean initialized = false;
    private static Window window;

    public static void init(final Window window) {
        if (initialized) throw new IllegalStateException(AssetUtils.class.getSimpleName() + " instance already initialized.");
        AssetUtils.window = window;
        initialized = true;
    }

    public synchronized static String getFileContent(final String path) {
        final StringBuilder builder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static Array<String> getLastDroppedFilePaths() {
        int count = window.getLatestFilesDraggedAndDroppedCount();
        Array<String> allFileDraggedAndDroppedPaths = window.getFilesDraggedAndDropped();
        Array<String> lastDroppedFilePaths = new Array<>(5);
        for (int i = 0; i < count; i++) {
            lastDroppedFilePaths.add(allFileDraggedAndDroppedPaths.get(allFileDraggedAndDroppedPaths.size - 1 - i));
        }
        return lastDroppedFilePaths;
    }

    public static Array<String> getDroppedFilesHistory() {
        Array<String> allFileDraggedAndDroppedPaths = window.getFilesDraggedAndDropped();
        Array<String> droppedFilesHistory = new Array<>(20);
        for (int i = 0; i < allFileDraggedAndDroppedPaths.size; i++) {
            droppedFilesHistory.add(allFileDraggedAndDroppedPaths.get(i));
        }
        return droppedFilesHistory;
    }

    public static long getFileSize(final String path) throws IOException {
        Path filePath = Paths.get(path);
        return Files.size(filePath);
    }

    public static synchronized Date getLastModifiedDate(final String filepath) {
        Path p = Paths.get(filepath);
        BasicFileAttributes view;
        try {
            view = Files.getFileAttributeView(p, BasicFileAttributeView.class)
            .readAttributes();
        } catch (IOException e) {
            return null; // file does not exist or whatever
        }
        return new Date(view.lastModifiedTime().toMillis());
    }

    public static synchronized boolean directoryExists(final String dirPath) {
        File directory = new File(dirPath);
        return directory.exists() && directory.isDirectory();
    }

    public static synchronized Array<String> getDirectoryFiles(final String dirPath, final boolean recursive, final String ...extensions) {
        if (!directoryExists(dirPath)) throw new IllegalArgumentException("Path: " + dirPath + " does not exist or is not a directory.");
        Array<String> paths = new Array<>();
        File directory = new File(dirPath);
        File[] children = directory.listFiles();
        for (File child : children) {
            if (child.isFile() && hasExtension(child, extensions)) paths.add(child.getPath());
            else if (child.isDirectory() && recursive) getDirectoryFiles(child.getPath(), extensions, paths);
        }
        return paths;
    }

    private static synchronized void getDirectoryFiles(final String dirPath, final String[] extensions, Array<String> collector) {
        File directory = new File(dirPath);
        File[] children = directory.listFiles();
        for (File child : children) {
            if (child.isFile() && hasExtension(child, extensions)) collector.add(child.getPath());
            else if (child.isDirectory()) getDirectoryFiles(child.getPath(), extensions, collector);
        }
    }

    private static boolean hasExtension(final File file, final String ...extensions) {
        if (extensions == null || extensions.length == 0) return true;
        for (String extension : extensions) {
            if (file.getName().endsWith(extension)) return true;
        }
        return false;
    }

}
