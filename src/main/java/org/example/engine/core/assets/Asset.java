package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;
import org.example.engine.core.memory.Resource;

class Asset implements Resource {

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
    public void free() {
        for (Asset dependency : dependencies) dependency.free();
        refCount--;
        if (refCount <= 0 && obj instanceof Resource) ((Resource) obj).free();
    }
}
