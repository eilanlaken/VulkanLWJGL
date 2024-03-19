#version 450

// inputs
in vec4 color;
in vec2 uv;
flat in float texIndex;

uniform sampler2D u_textures[32];

// outputs
layout (location = 0) out vec4 out_color;

void main() {

    vec4 textureColor;

    switch (int(texIndex)) {
        case 0: textureColor = texture(u_textures[0], uv); break;
        case 1: textureColor = texture(u_textures[1], uv); break;
        case 2: textureColor = texture(u_textures[2], uv); break;
    }

    out_color = color * textureColor;

}