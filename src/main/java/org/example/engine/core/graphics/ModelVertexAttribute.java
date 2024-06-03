package org.example.engine.core.graphics;

import org.lwjgl.opengl.GL11;

public enum ModelVertexAttribute {

    POSITION("a_position", 3, GL11.GL_FLOAT, false),
    COLOR("a_color", 4, GL11.GL_UNSIGNED_BYTE, true),
    TEXTURE_COORDINATES0("a_textCoords0", 2, GL11.GL_FLOAT , false),
    TEXTURE_COORDINATES1("a_textCoords1", 2, GL11.GL_FLOAT , false),
    NORMAL("a_normal", 3, GL11.GL_FLOAT , false),
    TANGENT("a_tangent",3, GL11.GL_FLOAT , false),
    BI_NORMAL("a_biNormal",3, GL11.GL_FLOAT , false),
    BONE_WEIGHT0("a_boneWeight0",2, GL11.GL_FLOAT , false),
    BONE_WEIGHT1("a_boneWeight1",2, GL11.GL_FLOAT , false),
    BONE_WEIGHT2("a_boneWeight2",2, GL11.GL_FLOAT , false),
    BONE_WEIGHT3("a_boneWeight3",2, GL11.GL_FLOAT , false),
    BONE_WEIGHT4("a_boneWeight4",2, GL11.GL_FLOAT , false),
    BONE_WEIGHT5("a_boneWeight5",2, GL11.GL_FLOAT , false),
    ;

    public final String glslVariableName;
    public final int length;
    public final int type;
    public final boolean normalized;
    public final short bitmask;
    public final int slot;

    ModelVertexAttribute(final String glslVariableName, final int length, int type, boolean normalized) {
        this.glslVariableName = glslVariableName;
        this.type = type;
        this.normalized = normalized;
        this.length = length;
        this.bitmask = (short) (0b000001 << ordinal());
        this.slot = ordinal();
    }

}
