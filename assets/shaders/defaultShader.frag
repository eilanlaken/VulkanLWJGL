#version 330

/**
redo using:
https://gist.github.com/galek/53557375251e1a942dfa
read tutorial
https://typhomnt.github.io/teaching/ray_tracing/pbr_intro/
*/

// definitions: macros and structs
#define POINT_LIGHTS_COUNT 10
#define PI 3.1415926538

float DistributionGGX(vec3 N, vec3 H, float roughness);
float GeometrySchlickGGX(float NdotV, float roughness);
float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness);
vec3 fresnelSchlick(float cosTheta, vec3 F0);

// inputs from previous shaders
in vec3 tbnUnitVertexToCamera;
in vec3 tbnCameraPosition;
in vec3 tbnVertexPosition;
in vec3 tbnNormal;
in vec3 tbnPointLightPosition;

// uniforms - environment
uniform vec4 ambient;
uniform vec4 pointLightColor;
uniform float pointLightIntensity;

// uniforms - material
uniform vec4 albedo;
uniform float metallic;
uniform float roughness;

layout (location = 0) out vec4 fragColor;

void main() {

    vec3 N = normalize(tbnNormal);
    vec3 V = normalize(tbnCameraPosition - tbnVertexPosition);
    vec3 F0 = vec3(0.04);
    F0 = mix(F0, albedo.rgb, metallic);

    // reflectance equation
    vec3 Lo = vec3(0.0);
    for(int i = 0; i < 1; ++i) // TODO: use variable number of lights
    {
        // calculate per-light radiance
        vec3 L = normalize(tbnPointLightPosition - tbnVertexPosition); // TODO: replace constant light with light[i]
        vec3 H = normalize(V + L);
        float distance    = length(tbnPointLightPosition - tbnVertexPosition);
        float attenuation = pointLightIntensity / (distance * distance);
        vec3 radiance     = pointLightColor.rgb * attenuation;

        // cook-torrance brdf
        float NDF = DistributionGGX(N, H, roughness);
        float G   = GeometrySmith(N, V, L, roughness);
        vec3 F    = fresnelSchlick(max(dot(H, V), 0.0), F0);

        vec3 kS = F;
        vec3 kD = vec3(1.0) - kS;
        kD *= 1.0 - metallic;

        vec3 numerator    = NDF * G * F;
        float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001;
        vec3 specular     = numerator / denominator;

        // add to outgoing radiance Lo
        float NdotL = max(dot(N, L), 0.0);
        Lo += (kD * albedo.rgb / PI + specular) * radiance * NdotL;
    }

    float ao = 1; // TODO
    vec3 ambient = vec3(0.03) * albedo.rgb * ao;
    vec3 color = Lo + ambient;

    color = color / (color + vec3(1.0));
    color = pow(color, vec3(1.0/2.2));

    fragColor = vec4(color, 1.0);

}

float DistributionGGX(vec3 N, vec3 H, float roughness) {
    float a      = roughness*roughness;
    float a2     = a*a;
    float NdotH  = max(dot(N, H), 0.0);
    float NdotH2 = NdotH*NdotH;

    float num   = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return num / denom;
}

float GeometrySchlickGGX(float NdotV, float roughness) {
    float r = (roughness + 1.0);
    float k = (r*r) / 8.0;

    float num   = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return num / denom;
}

float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2  = GeometrySchlickGGX(NdotV, roughness);
    float ggx1  = GeometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}

vec3 fresnelSchlick(float cosTheta, vec3 F0) {
    return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}