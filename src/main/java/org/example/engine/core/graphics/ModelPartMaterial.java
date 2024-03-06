package org.example.engine.core.graphics;

import org.example.engine.core.memory.Resource;

import java.util.HashMap;

public class ModelPartMaterial {

    public HashMap<String, Object> materialParams;

    public ModelPartMaterial(HashMap<String, Object> materialParams) {
        this.materialParams = materialParams;
    }

}
