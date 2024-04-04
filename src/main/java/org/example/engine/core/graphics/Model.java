package org.example.engine.core.graphics;

import org.example.engine.core.memory.Resource;

public class Model implements Resource {

    public final ModelPart[] parts;
    public final ModelArmature armature;

    public Model(final ModelPart[] parts, final ModelArmature armature) {
        this.parts = parts;
        this.armature = armature;
    }

    @Override
    public void delete() {

    }
}
