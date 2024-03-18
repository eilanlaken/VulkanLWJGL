#version 450

// inputs
in vec4 color;
in vec2 uv;
flat in int texIndex;

uniform sampler2D u_textures[32];
uniform sampler2D u_textures_0;
uniform sampler2D u_textures_1;

uniform float v[10];


//uniform sampler2D u_texture;

// outputs
layout (location = 0) out vec4 out_color;

void main() {
    //out_color = color * texture(u_textures[texIndex], uv);
    //out_color = texture(u_textures[0], uv);
    vec4 texColor;
    switch(int(texIndex))
    {
        case  0: texColor = texture(u_textures_0, uv); break;
        case  1: texColor = texture(u_textures_0, uv); break;
//        case  2: texColor = texture(u_textures[ 2], uv); break;
//        case  3: texColor = texture(u_textures[ 3], uv); break;
//        case  4: texColor = texture(u_textures[ 4], uv); break;
//        case  5: texColor = texture(u_textures[ 5], uv); break;
//        case  6: texColor = texture(u_textures[ 6], uv); break;
//        case  7: texColor = texture(u_textures[ 7], uv); break;
//        case  8: texColor = texture(u_textures[ 8], uv); break;
//        case  9: texColor = texture(u_textures[ 9], uv); break;
//        case 10: texColor = texture(u_textures[10], uv); break;
//        case 11: texColor = texture(u_textures[11], uv); break;
//        case 12: texColor = texture(u_textures[12], uv); break;
//        case 13: texColor = texture(u_textures[13], uv); break;
//        case 14: texColor = texture(u_textures[14], uv); break;
//        case 15: texColor = texture(u_textures[15], uv); break;
//        case 16: texColor = texture(u_textures[16], uv); break;
//        case 17: texColor = texture(u_textures[17], uv); break;
//        case 18: texColor = texture(u_textures[18], uv); break;
//        case 19: texColor = texture(u_textures[19], uv); break;
//        case 20: texColor = texture(u_textures[20], uv); break;
//        case 21: texColor = texture(u_textures[21], uv); break;
//        case 22: texColor = texture(u_textures[22], uv); break;
//        case 23: texColor = texture(u_textures[23], uv); break;
//        case 24: texColor = texture(u_textures[24], uv); break;
//        case 25: texColor = texture(u_textures[25], uv); break;
//        case 26: texColor = texture(u_textures[26], uv); break;
//        case 27: texColor = texture(u_textures[27], uv); break;
//        case 28: texColor = texture(u_textures[28], uv); break;
//        case 29: texColor = texture(u_textures[29], uv); break;
//        case 30: texColor = texture(u_textures[30], uv); break;
//        case 31: texColor = texture(u_textures[31], uv); break;
    }

    //out_color = color * texture(u_texture, uv);
    //out_color = color * texture(u_textures[texIndex], uv);

    out_color = color * texture(u_textures_1, uv); // TODO: WORKS!
    out_color = color * texture(u_textures[1], uv);
    //out_color = color * texture(u_textures_1, uv); // TODO: BLANCK

    //out_color = color * texColor;
    out_color = vec4(v[3],0,0,v[0]);

}