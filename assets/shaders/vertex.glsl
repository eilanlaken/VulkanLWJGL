#version 450

in vec3 position;
in vec2 textureCoordinates;

uniform mat4 transform;
uniform mat4 view;

out vec2 uv;

void main() {
    gl_Position = view * transform * vec4(position, 1.0);
    uv = textureCoordinates;
}