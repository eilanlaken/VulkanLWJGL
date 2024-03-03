package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AssetLoaderDebug implements AssetLoader<Debug> {

    private int num;
    private String dependPath1;
    private String dependPath2;

    @Override
    public Array<AssetDescriptor> getDependencies() {
        System.out.println("d1: " + dependPath1);
        System.out.println("d2: " + dependPath2);

        Array<AssetDescriptor> dependencies = new Array<>();
        dependencies.add(new AssetDescriptor(DebugDependency.class, dependPath1));
        dependencies.add(new AssetDescriptor(DebugDependency.class, dependPath2));
        return dependencies;
    }

    @Override
    public void asyncLoad(String path) {
        String content = AssetUtils.getFileContent(path);
        String[] lines = content.split("\n");
        num = Integer.parseInt(lines[0]);
        Path filePath = Paths.get(path);
        Path parentPath = filePath.getParent();

        dependPath1 = "assets/text/someText1.txt";
        dependPath2 = "assets/text/someText2.txt";

        dependPath1 = parentPath.resolve(lines[1]).toString();
    }

    @Override
    public Debug create() {
        Debug debug = new Debug();
        debug.num = num;
        debug.dependency1 = (DebugDependency) AssetStore.getAsset(dependPath1).obj;
        debug.dependency2 = (DebugDependency) AssetStore.getAsset(dependPath2).obj;
        return debug;
    }
}
