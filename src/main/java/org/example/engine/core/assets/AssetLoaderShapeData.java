package org.example.engine.core.assets;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.shape.ShapeData;

public class AssetLoaderShapeData implements AssetLoader<ShapeData> {

    @Override
    public CollectionsArray<AssetDescriptor> getDependencies() {
        return null;
    }

    @Override
    public void asyncLoad(String path) {

    }

    @Override
    public ShapeData create() {
        return null;
    }

}
