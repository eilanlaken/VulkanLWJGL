package org.example.engine.core.graphics;

import org.lwjgl.opengl.GL11;

public enum ModelVertexAttribute {

    // TODO: add position_2 / position_3, change to colorPacked.
    POSITION_2D("a_position", 2, GL11.GL_FLOAT, false, 0),
    POSITION_3D("a_position", 3, GL11.GL_FLOAT, false, 0),
    COLOR("a_color", 4, GL11.GL_FLOAT, false, 1), // TODO: change to unsigned something, true.

    // TODO: see how things work here.
    COLOR_PACKED("a_color", 4, GL11.GL_UNSIGNED_BYTE, true, 1), // TODO: change to unsigned something, true.


    TEXTURE_COORDINATES0("a_textCoords0", 2, GL11.GL_FLOAT , false,2),
    TEXTURE_COORDINATES1("a_textCoords1", 2, GL11.GL_FLOAT , false,3),
    NORMAL("a_normal", 3, GL11.GL_FLOAT , false,4),
    TANGENT("a_tangent",3, GL11.GL_FLOAT , false,5),
    BI_NORMAL("a_biNormal",3, GL11.GL_FLOAT , false,6),
    BONE_WEIGHT0("a_boneWeight0",2, GL11.GL_FLOAT , false,7),
    BONE_WEIGHT1("a_boneWeight1",2, GL11.GL_FLOAT , false,8),
    BONE_WEIGHT2("a_boneWeight2",2, GL11.GL_FLOAT , false,9),
    BONE_WEIGHT3("a_boneWeight3",2, GL11.GL_FLOAT , false,10),
    BONE_WEIGHT4("a_boneWeight4",2, GL11.GL_FLOAT , false,11),
    BONE_WEIGHT5("a_boneWeight5",2, GL11.GL_FLOAT , false,12),
    ;

    public final String glslVariableName;
    public final int length;
    public final int type;
    public final boolean normalized;
    public final short bitmask;
    public final int slot;

    ModelVertexAttribute(final String glslVariableName, final int length, int type, boolean normalized, int slot) {
        this.glslVariableName = glslVariableName;
        this.type = type;
        this.normalized = normalized;
        this.length = length;
        this.bitmask = (short) (0b000001 << ordinal());

        this.slot = slot;
        //this.slot = ordinal();
    }

}
