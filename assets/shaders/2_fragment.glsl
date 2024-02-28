#version 450

uniform vec4 color;
uniform vec4 ambient;

out vec4 fragColor;

void main() {
    vec4 finalColor = color * ambient;
    fragColor = vec4(finalColor.rgb, 1) + ambient;
}