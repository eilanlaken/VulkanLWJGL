#version 330

layout(location = 0) in vec3 a_position;
layout(location = 1) in vec2 a_texCoord0;
layout(location = 2) in vec2 a_texCoord1;
layout(location = 3) in vec3 a_normal;

// uniforms
uniform mat4 body_transform;
uniform mat4 camera_combined; // proj * view

void main() {
    // calculate vertex immediate output
    vec4 vertex_position = body_transform * vec4(a_position, 1.0);
    gl_Position = camera_combined * vertex_position;
}