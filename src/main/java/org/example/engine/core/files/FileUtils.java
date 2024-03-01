package org.example.engine.core.files;

import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.graphics.Window;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileUtils {

    private static boolean initialized = false;
    private static Window window;

    public static void init(final Window window) {
        if (initialized) throw new IllegalStateException(FileUtils.class.getSimpleName() + " instance already initialized.");
        FileUtils.window = window;
        initialized = true;
    }

    public static String getFileContent(final String path) {
        try {
            return Files.readString(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

}
