#version 450

in vec2 uv;

uniform sampler2D texture0;

out vec4 fragColor;

void main() {
    fragColor = texture(texture0, uv);
}