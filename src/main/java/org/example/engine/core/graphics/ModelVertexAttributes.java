package org.example.engine.core.graphics;

public enum ModelVertexAttributes {

    POSITION("a_position"),
    TEXTURE_COORDINATES0("a_textCoords0"),
    TEXTURE_COORDINATES1("a_textCoords1"),
    COLOR("a_color"),
    NORMAL("a_normal"),
    TANGENT("a_tangent"),
    BI_NORMAL("a_biNormal"),
    BONE_WEIGHT0("a_boneWeight0"),
    BONE_WEIGHT1("a_boneWeight1"),
    BONE_WEIGHT2("a_boneWeight2"),
    BONE_WEIGHT3("a_boneWeight3"),
    BONE_WEIGHT4("a_boneWeight4"),
    BONE_WEIGHT5("a_boneWeight5"),
    ;

    public final String glslVariableName;

    ModelVertexAttributes(final String glslVariableName) {
        this.glslVariableName = glslVariableName;
    }

}
