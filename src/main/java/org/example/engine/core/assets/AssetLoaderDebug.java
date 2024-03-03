package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;

public class AssetLoaderDebug implements AssetLoader<Debug> {

    private String content;
    private String dependPath1;
    private String dependPath2;

    @Override
    public Array<AssetDescriptor> getDependencies() {
        Array<AssetDescriptor> dependencies = new Array<>();
        dependencies.add(new AssetDescriptor(DebugDependency.class, dependPath1));
        dependencies.add(new AssetDescriptor(DebugDependency.class, dependPath2));
        return dependencies;
    }

    @Override
    public void asyncLoad(String path) {
        content = AssetUtils.getFileContent(path);
        String[] lines = content.split("\n");
        dependPath1 = lines[1];
        dependPath2 = lines[2];
    }

    @Override
    public Debug create() {
        Debug debug = new Debug();
        debug.text = content;
        debug.dependency1 = (DebugDependency) AssetStore.getAsset(dependPath1).obj;
        debug.dependency2 = (DebugDependency) AssetStore.getAsset(dependPath2).obj;
        return debug;
    }
}
