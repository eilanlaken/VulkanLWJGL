package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;

public class AssetLoaderDebugDependency implements AssetLoader<DebugDependency> {

    private String content;

    @Override
    public Array<AssetDescriptor> getDependencies() {
        return null;
    }

    @Override
    public void asyncLoad(String path) {
        content = AssetUtils.getFileContent(path);
        int i = 0;
        while (i < 5) {
            i++;
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public DebugDependency create() {
        return new DebugDependency(content);
    }
}
