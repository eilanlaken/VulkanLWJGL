package org.example.engine.core.assets;

import com.google.gson.Gson;
import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.application.ApplicationWindow;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Set;

public final class AssetUtils {

    private static boolean initialized = false;
    private static ApplicationWindow window;
    public static final Yaml yaml;
    public static final Gson gson;
    static {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Representer representer = new Representer(dumperOptions) {
            @Override
            protected MappingNode representJavaBean(Set<Property> properties, Object obj) {
                if (!classTags.containsKey(obj.getClass())) addClassTag(obj.getClass(), Tag.MAP);
                return super.representJavaBean(properties, obj);
            }
        };
        yaml = new Yaml(representer);
        gson = new Gson();
    }

    private AssetUtils() {}

    public static void init(final ApplicationWindow window) {
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

    public static CollectionsArray<String> getLastDroppedFilePaths() {
        int count = window.getLatestFilesDraggedAndDroppedCount();
        CollectionsArray<String> allFileDraggedAndDroppedPaths = window.getFilesDraggedAndDropped();
        CollectionsArray<String> lastDroppedFilePaths = new CollectionsArray<>(5);
        for (int i = 0; i < count; i++) {
            lastDroppedFilePaths.add(allFileDraggedAndDroppedPaths.get(allFileDraggedAndDroppedPaths.size - 1 - i));
        }
        return lastDroppedFilePaths;
    }

    public static CollectionsArray<String> getDroppedFilesHistory() {
        CollectionsArray<String> allFileDraggedAndDroppedPaths = window.getFilesDraggedAndDropped();
        CollectionsArray<String> droppedFilesHistory = new CollectionsArray<>(20);
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

    public static synchronized boolean filesExist(final String ...filepaths) {
        for (String filepath : filepaths) {
            if (!fileExists(filepath)) return false;
        }
        return true;
    }

    public static synchronized boolean fileExists(final String filepath) {
        File file = new File(filepath);
        return file.exists() && file.isFile();
    }

    public static synchronized boolean directoryExists(final String dirpath) {
        File directory = new File(dirpath);
        return directory.exists() && directory.isDirectory();
    }

    public static synchronized CollectionsArray<String> getDirectoryFiles(final String dirpath, final boolean recursive, final String ...extensions) {
        if (!directoryExists(dirpath)) throw new IllegalArgumentException("Path: " + dirpath + " does not exist or is not a directory.");
        CollectionsArray<String> paths = new CollectionsArray<>();
        File directory = new File(dirpath);
        File[] children = directory.listFiles();
        for (File child : children) {
            if (child.isFile() && hasExtension(child, extensions)) paths.add(child.getPath());
            else if (child.isDirectory() && recursive) getDirectoryFiles(child.getPath(), extensions, paths);
        }
        return paths;
    }

    private static synchronized void getDirectoryFiles(final String dirpath, final String[] extensions, CollectionsArray<String> collector) {
        File directory = new File(dirpath);
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

    public static boolean saveFile(final String dirpath, final String filename, final String content) throws IOException {
        if (!directoryExists(dirpath)) throw new IOException("Directory " + dirpath + " does not exist.");
        String filePath = dirpath + File.separator + filename;
        File file = new File(filePath);
        boolean fileExists = file.exists();
        try {
            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);
            bufferedWriter.close();
            fileWriter.close();
            return fileExists;
        } catch (IOException e) {
            throw e;
        }
    }

}
