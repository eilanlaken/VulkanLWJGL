#version 450

in vec3 position;
in vec2 textureCoordinates;

uniform mat4 transform;

out vec2 uv;

void main() {
    gl_Position = transform * vec4(position, 1.0);
    uv = textureCoordinates;
}