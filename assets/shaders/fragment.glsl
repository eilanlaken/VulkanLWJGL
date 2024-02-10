#version 450

in vec3 color;

uniform sampler2D texture0;

out vec4 fragColor;

void main() {
    fragColor = vec4(color.r, texture(texture0, vec2(0,0)).gb, 1.0);
}