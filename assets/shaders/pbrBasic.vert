#version 330

in vec3 a_position;
in vec2 a_textureCoordinates;
in vec3 a_normal;

uniform vec3 cameraPosition;
uniform mat4 transform;
uniform mat4 cameraView;
uniform mat4 cameraProjection;
uniform mat4 cameraCombined;

out mat3 invTBN; // invTBN is a matrix that transforms vectors from xyz space to tbn space
out vec3 worldVertexPosition;
out vec3 worldVertexNormal;
out vec3 unitVertexToCamera;

void main() {

    // compute vertex world position & normal
    vec4 worldVertexPosition4 = transform * vec4(a_position, 1.0);
    vec4 worldVertexNormal4 = transform * vec4(a_normal, 1.0);
    // vertex shader immediate output
    gl_Position = cameraCombined * worldVertexPosition4;

    // missing tangent and binormal data.
    // I can create a tangent that is perpendicular to the normal.
    // Then the binormal is simply a cross product of the normal and the tangent
    vec3 tangent = normalize(vec3(1.0, 0.0, 0.0) - a_normal.x * a_normal);
    vec3 biNormal = cross(a_normal, tangent);

    // compute vertex shader out variables: invTBN matrix, unit vertex to camera, world vertex position and world vertex normal
    vec3 T = normalize(vec3(transform * vec4(tangent, 0.0)));
    vec3 B = normalize(vec3(transform * vec4(biNormal, 0.0)));
    vec3 N = normalize(vec3(transform * vec4(a_normal, 0.0)));
    mat3 TBN = mat3(T, B, N);
    invTBN = transpose(TBN); // TBN is orthogonal therefore inverse(TBN) = transpose(TBN)
    unitVertexToCamera = normalize(invTBN * (cameraPosition - vertexPosition4.xyz));
    worldVertexPosition = worldVertexPosition4.xyz;
    worldVertexNormal = normalize(worldVertexNormal4.xyz);
}