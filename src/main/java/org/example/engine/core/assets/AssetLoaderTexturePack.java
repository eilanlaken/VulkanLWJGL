package org.example.engine.core.assets;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.graphics.Texture;
import org.example.engine.core.graphics.TexturePack;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

// TODO: implement. Finalize AssetStore.
public class AssetLoaderTexturePack implements AssetLoader<TexturePack> {


    private CollectionsArray<AssetDescriptor> dependencies;

    @Override
    public CollectionsArray<AssetDescriptor> getDependencies() {
        return null;
    }

    @Override
    public void asyncLoad(String path) {
        try {
            FileInputStream inputStream = new FileInputStream(path);
            Map<String, Object> data = AssetUtils.yaml.load(inputStream);
            List<Map<String, Object>> textures = (List<Map<String, Object>>) data.get("textures");
            dependencies = new CollectionsArray<>(textures.size());
            for (Map<String, Object> texture : textures) {
                String file = (String) texture.get("file");
                dependencies.add(new AssetDescriptor(Texture.class, file));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TexturePack create() {
        return null;
    }

}
