#version 330

/**
redo using:
https://gist.github.com/galek/53557375251e1a942dfa
*/

// definitions: macros and structs
#define POINT_LIGHTS_COUNT 10
#define PI 3.1415926538

struct PointLight {
    vec3 position;
    vec3 color;
    float intensity;
};

// definitions: functions
float distributionGGX(vec3 n, vec3 h, float roughness);
float geometrySchlickGGX(float ndotv, float roughness);
float geometrySmith(vec3 n, vec3 v, vec3 l, float roughness);
vec3 fresnelSchlick(float cosTheta, vec3 f0);

// inputs from previous shaders
in mat3 invTBN;
in vec3 worldVertexPosition;
in vec3 worldVertexNormal;
in vec3 unitVertexToCamera;

// uniforms - environment
uniform PointLight pointLights[POINT_LIGHTS_COUNT];
uniform vec4 color;
uniform vec4 ambient;
uniform float metallic;
uniform float roughness;

void main() {

}

// function implementations
float distributionGGX(vec3 n, vec3 h, float roughness) {
    float a = roughness * roughness;
    float a2 = a * a;
    float ndoth = max(dot(n, h), 0.0);
    float ndoth2 = ndoth * ndoth;
    float num = a2;
    float denom = (ndoth2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;
    return num / denom;
}

float geometrySchlickGGX(float ndotv, float roughness) {
    float r = (roughness + 1.0);
    float k = (r * r) / 8.0;
    float num   = ndotv;
    float denom = ndotv * (1.0 - k) + k;
    return num / denom;
}

float geometrySmith(vec3 n, vec3 v, vec3 l, float roughness) {
    float ndotv = max(dot(n, v), 0.0);
    float ndotl = max(dot(n, l), 0.0);
    float ggx2  = geometry_schlick_GGX(ndotv, roughness);
    float ggx1  = geometry_schlick_GGX(ndotl, roughness);
    return ggx1 * ggx2;
}

vec3 fresnelSchlick(float cosTheta, vec3 f0) {
    return f0 + (1.0 - f0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}