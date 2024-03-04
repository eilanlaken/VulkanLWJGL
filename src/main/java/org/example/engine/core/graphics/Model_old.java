package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.ArrayInt;
import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL30;

@Deprecated
public class Model_old implements Resource {

    // TODO:
    public final short vertexAttributesMask = 0b000;

    public final Array<ModelPart> parts;
    public final ModelArmature armature;

    public Model_old(final ModelPart part) {
        this.parts = new Array<>(1);
        this.parts.add(part);
        this.armature = null;
    }

    public Model_old(Array<ModelPart> parts, ModelArmature armature) {
        this.parts = parts;
        this.armature = armature;
    }

    @Override
    public void free() {
        for (ModelPart part : parts) {
            part.free();
        }
    }
}