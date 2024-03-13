#version 330

// inputs
in vec4 color;

// outputs
layout (location = 0) out vec4 out_color;

void main() {
    //out_color = color * texture2D(u_texture, uv);
    out_color =  color;
}