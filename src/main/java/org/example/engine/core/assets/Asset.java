package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;
import org.example.engine.core.memory.MemoryResource;

class Asset implements MemoryResource {

    public int refCount;
    public final Object obj;
    public final AssetDescriptor descriptor;
    public final Array<Asset> dependencies;

    Asset(final Object obj, final AssetDescriptor descriptor, Array<Asset> dependencies) {
        this.refCount = 1;
        this.obj = obj;
        this.descriptor = descriptor;
        this.dependencies = dependencies;
    }

    @Override
    public void delete() {
        for (Asset dependency : dependencies) dependency.delete();
        refCount--;
        if (refCount <= 0 && obj instanceof MemoryResource) ((MemoryResource) obj).delete();
    }
}
