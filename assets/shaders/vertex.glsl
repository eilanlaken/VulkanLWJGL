#version 450

in vec3 position;
in vec2 textureCoordinates;

out vec2 uv;

void main() {
    gl_Position = vec4(position, 1.0);
    uv = textureCoordinates;
}