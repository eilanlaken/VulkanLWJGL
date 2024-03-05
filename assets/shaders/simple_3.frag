#version 330
#define MAX_POINT_LIGHTS 10

uniform vec4 colorDiffuse;

// outputs
layout (location = 0) out vec4 out_color;

void main() {

    out_color =  colorDiffuse;
}
