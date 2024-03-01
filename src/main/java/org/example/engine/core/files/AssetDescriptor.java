package org.example.engine.core.files;

import org.example.engine.core.memory.Resource;

import java.io.IOException;

class AssetDescriptor {

    public final Class<? extends Resource> type;
    public final String path;
    public final long size;

    public AssetDescriptor(Class<? extends Resource> type, String path) throws IOException {
        this.type = type;
        this.path = path;
        this.size = FileUtils.getFileSize(path);
    }
}
