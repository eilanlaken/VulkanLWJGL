package org.example.engine.core.assets;

import org.example.engine.core.memory.MemoryResource;

import java.io.IOException;
import java.util.Objects;

class AssetDescriptor {

    public final Class<? extends MemoryResource> type;
    public final String path;
    public final long size;

    // TODO: error handling.
    public AssetDescriptor(Class<? extends MemoryResource> type, String path) {
        this.type = type;
        this.path = path;
        long s = 0;
        try {
            s = AssetUtils.getFileSize(path);
        } catch (IOException e) {

        }
        this.size = s;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof AssetDescriptor)) return false;
        if (this == obj) return true;
        AssetDescriptor otherDescriptor = (AssetDescriptor) obj;
        return Objects.equals(this.path, otherDescriptor.path) && this.type == otherDescriptor.type;
    }

}
