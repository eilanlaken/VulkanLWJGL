package org.example.engine.core.files;

import java.io.IOException;

class AssetDescriptor {

    public final Class<?> type;
    public final String path;
    public final long size;

    public AssetDescriptor(Class<?> type, String path) throws IOException {
        this.type = type;
        this.path = path;
        this.size = FileUtils.getFileSize(path);
    }
}
