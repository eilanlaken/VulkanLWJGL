package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AssetLoaderDebug implements AssetLoader<Debug> {

    private String content;
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
        System.out.println("path parent: " + path);
        content = AssetUtils.getFileContent(path);
        System.out.println("content parent: \n" + content);
        String[] lines = content.split("\n");

        Path filePath = Paths.get(path);

        dependPath1 = "assets/text/someText1.txt";
        dependPath2 = "assets/text/someText2.txt";
    }

    @Override
    public Debug create() {
        System.out.println("create debug: " + dependPath1);
        System.out.println(AssetStore.store);

        Debug debug = new Debug();
        debug.text = content;
        debug.dependency1 = (DebugDependency) AssetStore.getAsset(dependPath1).obj;
        debug.dependency2 = (DebugDependency) AssetStore.getAsset(dependPath2).obj;
        System.out.println("created debug");
        return debug;
    }
}
