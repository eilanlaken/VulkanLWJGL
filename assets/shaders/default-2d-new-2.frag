#version 450

// inputs
in vec4 color;
in vec2 uv;
flat in int texIndex;

uniform sampler2D u_textures[32];
uniform sampler2D u_textures_0;
uniform sampler2D u_textures_1;

// outputs
layout (location = 0) out vec4 out_color;

void main() {
    //out_color = color * texture(u_textures[texIndex], uv);
    //out_color = texture(u_textures[0], uv);


    //out_color = color * texture(u_texture, uv);
    //out_color = color * texture(u_textures[texIndex], uv);

    //out_color = color * texture(u_textures_1, uv); // TODO: WORKS!
    //out_color = texture(u_textures_1, uv);


    out_color = color * vec4(texIndex, texIndex, texIndex, 1.0);


}