package org.example.engine.core.graphics;

import org.example.engine.core.memory.Resource;

// TODO: make finals etc.
public class Model implements Resource {

    public ModelPart[] parts;
    public ModelArmature armature;


    @Override
    public void free() {

    }
}
