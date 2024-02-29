#version 330

in vec3 a_position;
in vec2 a_textureCoordinates;
in vec3 a_normal;

// uniforms
uniform mat4 transform;
uniform vec3 cameraPosition;
uniform mat4 cameraCombined;
uniform vec3 pointLightPosition;

// shader pass through variables
out vec3 tbnUnitVertexToCamera;
out vec3 tbnCameraPosition;
out vec3 tbnVertexPosition;
out vec3 tbnVertexNormal;
out vec3 tbnPointLightPosition;

out vec3 debug;

void main() {
    // vertex shader immediate output
    vec4 worldVertexPosition = transform * vec4(a_position, 1.0);
    vec4 worldVertexNormal = transform * vec4(a_normal, 1.0);
    gl_Position = cameraCombined * worldVertexPosition;

    // create tangent and bi-normal, since in this case, we don't have them as attributes.
    vec3 tangent = normalize(vec3(1.0, 0.0, 0.0) - a_normal.x * a_normal); // choose arbitrary tangent vector, perpendicular to normal
    vec3 biNormal = cross(a_normal, tangent);

    // toTBN transforms world coordinates to TBN coordinates.
    vec3 T = normalize(vec3(transform * vec4(tangent, 0.0)));
    vec3 B = normalize(vec3(transform * vec4(biNormal, 0.0)));
    vec3 N = normalize(vec3(transform * vec4(a_normal, 0.0)));
    mat3 TBN = mat3(T, B, N);
    mat3 toTBN = transpose(TBN); // TBN is orthogonal therefore inverse(TBN) = transpose(TBN) = toTBN

    // compute vertex shader out variables: invTBN matrix, unit vertex to camera, world vertex position and world vertex normal
    tbnUnitVertexToCamera = normalize(toTBN * (cameraPosition - worldVertexPosition.xyz));
    tbnCameraPosition = toTBN * cameraPosition;
    tbnVertexPosition = toTBN * worldVertexPosition.xyz;
    tbnVertexNormal = toTBN * worldVertexNormal.xyz;
    tbnPointLightPosition = toTBN * pointLightPosition;

    debug = a_normal;
}