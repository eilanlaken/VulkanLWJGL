package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;
import org.example.engine.core.shape.ShapeData;

public class AssetLoaderShapeData implements AssetLoader<ShapeData> {

    @Override
    public Array<AssetDescriptor> getDependencies() {
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
