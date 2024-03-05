package org.example.engine.core.graphics;

public enum ModelVertexAttribute {

    POSITION("a_position", 3),
    COLOR("a_color", 4),
    TEXTURE_COORDINATES0("a_textCoords0", 2),
    TEXTURE_COORDINATES1("a_textCoords1", 2),
    NORMAL("a_normal", 3),
    TANGENT("a_tangent",3),
    BI_NORMAL("a_biNormal",3),
    BONE_WEIGHT0("a_boneWeight0",2),
    BONE_WEIGHT1("a_boneWeight1",2),
    BONE_WEIGHT2("a_boneWeight2",2),
    BONE_WEIGHT3("a_boneWeight3",2),
    BONE_WEIGHT4("a_boneWeight4",2),
    BONE_WEIGHT5("a_boneWeight5",2),
    ;

    public final String glslVariableName;
    public final short bitmask;
    public final int length;
    public final int slot;

    ModelVertexAttribute(final String glslVariableName, final int length) {
        this.glslVariableName = glslVariableName;
        this.bitmask = (short) (0b000001 << ordinal());
        this.length = length;
        this.slot = ordinal();
    }

}
