#version 450

// inputs
in vec4 color;
in vec2 uv;
flat in float texIndex;

uniform sampler2D u_textures[32];
//uniform sampler2D u_textures_0;
//uniform sampler2D u_textures_1;

// outputs
layout (location = 0) out vec4 out_color;

void main() {
    //out_color = color * texture(u_textures[texIndex], uv);
    //out_color = texture(u_textures[0], uv);


    //out_color = color * texture(u_texture, uv);
    //out_color = color * texture(u_textures[texIndex], uv);

    //out_color = color * texture(u_textures_1, uv); // TODO: WORKS!
    //out_color = texture(u_textures_1, uv);


    //out_color = color * vec4(texIndex, texIndex, texIndex, 1.0);

    //out_color = texture(u_textures[0], uv) * texture(u_textures[1], uv);

    vec4 textureColor;
    switch (int(texIndex)) {
        case 0: textureColor = texture(u_textures[0], uv); break;
        case 1: textureColor = texture(u_textures[1], uv); break;
        case 2: textureColor = texture(u_textures[2], uv); break;
    }

    out_color = textureColor;
    //out_color = texture(u_textures[texIndex], uv);

    //out_color += texture(u_textures_0, uv);

}