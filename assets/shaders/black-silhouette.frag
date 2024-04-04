#version 450

// inputs
in vec4 color;
in vec2 uv;

// uniforms
uniform sampler2D u_texture;

// outputs
layout (location = 0) out vec4 out_color;

void main() {
    out_color = vec4(1.0,0.0,0.0,1.0) * texture(u_texture, uv);
}