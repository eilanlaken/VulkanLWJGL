package org.example.engine.core.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class FileUtils {

    public static String getFileContent(final String path) {
        try {
            return Files.readString(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
