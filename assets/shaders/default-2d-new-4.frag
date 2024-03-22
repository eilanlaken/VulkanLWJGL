#version 450

// inputs
in vec4 color;
in vec2 uv;

uniform sampler2D u_texture;

// outputs
layout (location = 0) out vec4 out_color;

void main() {

    out_color = color * texture(u_texture, uv);

}