package org.example.engine.core.graphics;

import org.example.engine.core.memory.Resource;

import java.util.HashMap;

public class ModelPartMaterial implements Resource {

    public HashMap<String, Object> materialParams;
    public ModelPartMaterial(HashMap<String, Object> materialParams) {
        this.materialParams = materialParams;
    }

    @Override
    public void free() {
        // TODO: implement
    }
}
