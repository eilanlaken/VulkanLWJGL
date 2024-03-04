#version 330

in vec3 a_position;
in vec2 a_texCoord0;
in vec3 a_normal;

// uniforms
uniform mat4 body_transform;
uniform vec3 camera_position;
uniform mat4 camera_combined; // proj * view

// outputs
out vec3 unit_vertex_to_camera;
out vec3 unit_world_normal;
out vec3 world_vertex_position;
out vec2 uv;

void main() {
    // calculate vertex immediate output
    vec4 vertex_position = body_transform * vec4(a_position, 1.0);
    gl_Position = camera_combined * vertex_position;

    // outputs
    unit_vertex_to_camera = normalize(camera_position - vertex_position.xyz);
    unit_world_normal = normalize((body_transform * vec4(a_normal, 1.0)).xyz);
    world_vertex_position = vertex_position.xyz;
    uv = a_texCoord0;
}