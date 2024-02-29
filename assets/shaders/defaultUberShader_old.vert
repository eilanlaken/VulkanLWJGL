#version 330

in vec3 a_position;
in vec2 a_texCoord0;
in vec3 a_normal;
in vec3 a_tangent;
in vec3 a_binormal;

// uniforms
uniform vec3 camera_position;
uniform mat4 body_transform;
uniform mat4 camera_combined; // proj * view

// outputs
out vec3 world_vertex_position;
out vec3 unit_vertex_to_camera;
out vec2 uv;
out mat3 invTBN; // invTBN is a matrix that transforms vectors from xyz space to tbn space

void main() {

    // calculate vertex immediate output
    vec4 vertex_position = body_transform * vec4(a_position, 1.0);
    gl_Position = camera_combined * vertex_position;

    // compute TBN matrix
    vec3 T = normalize(vec3(body_transform * vec4(a_tangent,   0.0)));
    vec3 B = normalize(vec3(body_transform * vec4(a_binormal, 0.0)));
    vec3 N = normalize(vec3(body_transform * vec4(a_normal,    0.0)));
    mat3 TBN = mat3(T, B, N);

    invTBN = transpose(TBN); // TBN is orthogonal therefore inverse(TBN) = transpose(TBN)
    unit_vertex_to_camera = normalize(invTBN * (camera_position - vertex_position.xyz));
    world_vertex_position = vertex_position.xyz;
    uv = a_texCoord0;

}
