package org.example.engine.core.assets;

import com.google.gson.Gson;
import org.example.engine.core.application.ApplicationWindow;
import org.example.engine.core.collections.Array;
import org.example.engine.core.memory.MemoryUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Set;

// TODO: change whole package from Asset to File
public final class AssetUtils {

    private static boolean           initialized = false;
    private static ApplicationWindow window      = null;
    public  static Yaml              yaml        = null;
    public  static Gson              gson        = null;

    private AssetUtils() {}

    public static void init(final ApplicationWindow window) {
        if (initialized) return;
        AssetUtils.window = window;

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

    public static synchronized boolean filesExist(final String ...filePaths) {
        for (String filepath : filePaths) {
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

    public static synchronized Array<String> getDirectoryFiles(final String dirpath, final boolean recursive, final String ...extensions) {
        if (!directoryExists(dirpath)) throw new IllegalArgumentException("Path: " + dirpath + " does not exist or is not a directory.");
        Array<String> paths = new Array<>();
        File directory = new File(dirpath);
        File[] children = directory.listFiles();
        assert children != null;
        for (File child : children) {
            if (child.isFile() && hasExtension(child, extensions)) paths.add(child.getPath());
            else if (child.isDirectory() && recursive) getDirectoryFiles(child.getPath(), extensions, paths);
        }
        return paths;
    }

    private static synchronized void getDirectoryFiles(final String dirpath, final String[] extensions, Array<String> collector) {
        File directory = new File(dirpath);
        File[] children = directory.listFiles();
        assert children != null;
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

    public static boolean saveFile(final String directory, final String filename, final String content) throws IOException {
        if (!directoryExists(directory)) throw new AssetsException("Directory: " + directory + " does not exist.");
        String filePath = directory + File.separator + filename;
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

    public static void saveImage(final String directory, final String filename, BufferedImage image) throws IOException {
        if (!directoryExists(directory)) throw new AssetsException("Directory: " + directory + " does not exist.");
        String filePath = directory + File.separator + filename + ".png";
        File file = new File(filePath);
        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            throw e;
        }
    }

    /** Returns the file extension (without the dot) or an empty string if the file name doesn't contain a dot. */
    public static String extension(final String name) {
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) return "";
        return name.substring(dotIndex + 1);
    }

    /** @return the name of the file, without parent paths or the extension. */
    public static String nameWithoutExtension(final String name) {
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) return name;
        return name.substring(0, dotIndex);
    }

    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     *
     * @param resource   the resource to read
     * @param bufferSize the initial buffer size
     *
     * @return the resource data
     *
     * @throws IOException if an IO error occurs
     */
    public synchronized static ByteBuffer readFileToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;
        Path path = Paths.get(resource);

        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1) ;
            }
        } else {
            try (InputStream source = AssetUtils.class.getClassLoader().getResourceAsStream(resource); ReadableByteChannel rbc = Channels.newChannel(source)) {
                buffer = BufferUtils.createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = MemoryUtils.resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                    }
                }
            }
        }

        buffer.flip();
        return MemoryUtil.memSlice(buffer);
    }

}
