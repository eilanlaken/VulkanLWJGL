package org.example.engine.core.files;

import java.io.IOException;

class AssetDescriptor {

    public final Class<? extends Object> type;
    public final String[] paths;
    public final long size;

    public AssetDescriptor(Class<? extends Object> type, String... paths) throws IOException {
        this.type = type;
        this.paths = paths;
        long total = 0;
        for (String path : paths) {
            total += FileUtils.getFileSize(path);
        }
        this.size = total;
    }
}
