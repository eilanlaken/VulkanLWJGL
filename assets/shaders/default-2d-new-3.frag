#version 450

// inputs
in vec4 color;
in vec2 uv;
flat in float texIndex;

uniform sampler2D u_textures[4];

// outputs
layout (location = 0) out vec4 out_color;

void main() {

    vec4 finalColor;

    switch (int(texIndex)) {
        // TODO: problem here: this shit defaults to 0
        // TODO: ALSO - WHO SAID THE BOUND TEXTURE SLOT WILL BE IN THE RANGE 0-3???
        case 0: finalColor = color * texture(u_textures[0], uv); break;
        case 1: finalColor = color * texture(u_textures[1], uv); break;
        case 2: finalColor = color * texture(u_textures[2], uv); break;
        case 3: finalColor = color * texture(u_textures[3], uv); break;
    }

    if (finalColor.a == 0) discard;

    out_color = finalColor;

}