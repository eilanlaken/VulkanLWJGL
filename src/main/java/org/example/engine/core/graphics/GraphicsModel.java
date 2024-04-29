package org.example.engine.core.graphics;

import org.example.engine.core.memory.MemoryResource;

public class GraphicsModel implements MemoryResource {

    public final GraphicsModelPart[] parts;
    public final GraphicsModelArmature armature;

    public GraphicsModel(final GraphicsModelPart[] parts, final GraphicsModelArmature armature) {
        this.parts = parts;
        this.armature = armature;
    }

    @Override
    public void delete() {

    }
}
