#version 330

#define MAX_POINT_LIGHTS 10
#define MAX_DIRECTIONAL_LIGHTS 1
#define PI 3.1415926538

struct PointLight {
    vec3 position;
    vec3 color;
    float intensity;
};

struct DirectionalLight {
    vec3 position;
    vec3 direction;
    vec3 color;
    float intensity;
};

struct RemappingParams {
    float x;
    float y;
};

// inputs
in vec2 uv;
in mat3 invTBN;
in vec3 unit_vertex_to_camera;
in vec3 world_vertex_position;

// uniforms
uniform PointLight point_lights[MAX_POINT_LIGHTS];
uniform DirectionalLight directional_lights[MAX_DIRECTIONAL_LIGHTS];
uniform float atlas_width_inv;
uniform float atlas_height_inv;
uniform RemappingParams diffuse_map;
uniform float width;
uniform float height;
uniform sampler2D image;
uniform float metallic;
uniform float roughness;
uniform float ambient;
uniform float gamma; // <- gamma correction from options

layout (location = 0) out vec4 out_color;
layout (location = 1) out vec4 out_color_emissive;
layout (location = 2) out vec4 out_black;

// functions
vec4 get_emissive_color(vec3, vec3);
float distribution_GGX(vec3 n, vec3 h, float roughness);
float geometry_schlick_GGX(float ndotv, float roughness);
float geometry_smith(vec3 n, vec3 v, vec3 l, float roughness);
vec3 fresnel_schlick(float cos_theta, vec3 f0);

void main()
{
    // remappings
    vec2 uv_albedo = vec2((diffuse_map.x + uv.x * width) * atlas_width_inv, (diffuse_map.y + uv.y * height) * atlas_height_inv);
    // material values
    vec3 albedo    = texture(image, uv_albedo).rgb; //pow(texture(image, uv_albedo).rgb, vec3(2.2));

    // pbr required geometry
    vec3 N = vec3(0,0,1);
    vec3 V = unit_vertex_to_camera;
    vec3 F0 = mix(vec3(0.04), albedo, metallic);

    // summation
    vec3 Lo = vec3(0.0);
    vec3 total_specular = vec3(0,0,0);

    // for point lights
    for (int i = 0; i < MAX_POINT_LIGHTS; i++)
    {
        // calculate per-box2DLight radiance
        vec3 vertex_to_light    = invTBN * (point_lights[i].position - world_vertex_position);
        float distance_to_light = length(vertex_to_light);
        float attenuation       = 1.0 / (1.0 + 0.01 * distance_to_light + 0.001 * distance_to_light * distance_to_light);
        vec3 radiance           = point_lights[i].intensity * point_lights[i].color * attenuation;

        // cook-torrance brdf
        vec3 L                  = normalize(vertex_to_light);
        vec3 H                  = normalize(V + L);
        float NDF = distribution_GGX(N, H, roughness);
        float G   = geometry_smith(N, V, L, roughness);
        vec3  F   = fresnel_schlick(max(dot(H, V), 0.0), F0);

        vec3 numerator    = NDF * G * F;
        float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001;
        vec3 specular     = numerator / denominator;
        total_specular += specular;

        vec3 kS = F;
        vec3 kD = vec3(1.0) - kS;
        kD *= 1.0 - metallic;
        // add to outgoing radiance Lo
        float NdotL = max(dot(N, L), 0.0);
        Lo += (kD * albedo / PI + specular) * radiance * NdotL;
    }

    // sum directional lights
    for (int i = 0; i < MAX_DIRECTIONAL_LIGHTS; i++) {
        // calculate per-box2DLight radiance
        vec3 vertex_to_light    = invTBN * (directional_lights[i].position - world_vertex_position);
        vec3 radiance           = directional_lights[i].intensity * directional_lights[i].color;

        // cook-torrance brdf
        vec3 L    = normalize(vertex_to_light);
        vec3 H    = normalize(V + L);
        float NDF = distribution_GGX(N, H, roughness);
        float G   = geometry_smith(N, V, L, roughness);
        vec3  F   = fresnel_schlick(max(dot(H, V), 0.0), F0);

        vec3 numerator    = NDF * G * F;
        float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001;
        vec3 specular     = numerator / denominator;
        total_specular += specular;

        vec3 kS = F;
        vec3 kD = vec3(1.0) - kS;
        kD *= 1.0 - metallic;
        // add to outgoing radiance Lo
        float NdotL = max(dot(N, L), 0.0);
        Lo += (kD * albedo / PI + specular) * radiance * NdotL;
    }

    // choose which one works best - probably ambient is a per material texture property.
    //vec3 color = ambient + Lo; // <- also possible
    //vec3 color = max(ambient, Lo);
    vec3 color = albedo + Lo; // <- fix

    //color              = pow(color, vec3(1.0 / gamma));
    out_color          = vec4(color, 1.0);
    out_color_emissive = get_emissive_color(total_specular, out_color.rgb);
    out_black          = vec4(0.0, 0.0, 0.0, 1.0);
}

// functions
vec4 get_emissive_color(vec3 total_specular, vec3 color)
{
    float bloom_filter = dot(total_specular, vec3(0.2126, 0.7152, 0.0722));
    if (bloom_filter > 0.5) {
        return vec4(color, 0.87);
    } else {
        return vec4(0,0,0,0);
    }
}

float distribution_GGX(vec3 n, vec3 h, float roughness)
{
    float a      = roughness * roughness;
    float a2     = a * a;
    float ndoth  = max(dot(n, h), 0.0);
    float ndoth2 = ndoth * ndoth;

    float num   = a2;
    float denom = (ndoth2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return num / denom;
}

float geometry_schlick_GGX(float ndotv, float roughness)
{
    float r = (roughness + 1.0);
    float k = (r * r) / 8.0;

    float num   = ndotv;
    float denom = ndotv * (1.0 - k) + k;

    return num / denom;
}

float geometry_smith(vec3 n, vec3 v, vec3 l, float roughness)
{
    float ndotv = max(dot(n, v), 0.0);
    float ndotl = max(dot(n, l), 0.0);
    float ggx2  = geometry_schlick_GGX(ndotv, roughness);
    float ggx1  = geometry_schlick_GGX(ndotl, roughness);

    return ggx1 * ggx2;
}

vec3 fresnel_schlick(float cos_theta, vec3 f0)
{
    return f0 + (1.0 - f0) * pow(clamp(1.0 - cos_theta, 0.0, 1.0), 5.0);
}
