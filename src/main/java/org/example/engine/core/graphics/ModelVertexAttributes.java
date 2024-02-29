package org.example.engine.core.graphics;

public enum ModelVertexAttributes {

    POSITION("a_position"),
    TEXTURE_COORDINATES("a_textureCoordinates"),
    COLOR("a_color"),
    NORMAL("a_normal"),
    TANGENT("a_tangent"),
    BI_NORMAL("a_biNormal"),
    BONE_WEIGHT("a_boneWeight"),
    ;

    public final String glslVariableName;

    ModelVertexAttributes(final String glslVariableName) {
        this.glslVariableName = glslVariableName;
    }

}
