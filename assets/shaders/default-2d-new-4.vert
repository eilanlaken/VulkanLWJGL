#version 450

// attributes
layout(location = 0) in vec2 a_position;
layout(location = 1) in vec4 a_color;
layout(location = 2) in vec2 a_texCoord0;

// uniforms
//uniform mat4 u_camera_combined;

// outputs
out vec4 color;
out vec2 uv;

void main() {
    color = a_color;
    uv = a_texCoord0;
    gl_Position = vec4(a_position.x, a_position.y, 0, 1.0);
}