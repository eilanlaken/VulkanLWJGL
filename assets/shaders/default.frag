#version 330
#define MAX_POINT_LIGHTS 10

// inputs
in vec3 unit_vertex_to_camera;
in vec3 unit_world_normal;
in vec3 world_vertex_position;
in vec2 uv;

// uniforms
uniform vec3 pointLightPos;
uniform vec4 pointLightColor;
uniform float pointLightIntensity;

uniform vec4 colorDiffuse;
float shineDamper = 32;
float reflectivity = 1;

// outputs
layout (location = 0) out vec4 out_color;

void main() {

    vec3 total_diffuse = vec3(0,0,0);
    vec3 total_specular = vec3(0,0,0);

    // sum point lights
    //    for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
    vec3 vertex_to_point_light = pointLightPos - world_vertex_position;
    float distance_to_light = length(vertex_to_point_light);
    float attenuation_factor = 1.0 + 0.01 * distance_to_light + 0.001 * distance_to_light * distance_to_light;

    vec3 unit_vertex_to_light = normalize(vertex_to_point_light);
    float nDotl = dot(unit_world_normal, unit_vertex_to_light);
    float brightness = max(0.0, nDotl);
    total_diffuse = total_diffuse + (pointLightIntensity * brightness * pointLightColor.rgb) / attenuation_factor;

    vec3 light_direction = -unit_vertex_to_light;
    vec3 reflected_light_direction = reflect(light_direction, unit_world_normal);
    float specular_factor = dot(reflected_light_direction, unit_vertex_to_camera);
    specular_factor = max(specular_factor, 0.0);
    float damped_factor = pow(specular_factor, shineDamper);
    total_specular = total_specular + (pointLightIntensity * damped_factor * reflectivity * pointLightColor.rgb) / attenuation_factor;
    //    }

    out_color = vec4(total_diffuse, 1.0) * colorDiffuse * 0.1 + 0.1 * colorDiffuse + vec4(total_specular * 1, 1.0);
}
