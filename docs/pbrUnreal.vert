#version 450 core
// Physically Based Rendering
// Copyright (c) 2017-2018 Michał Siejak

// Physically Based shading model: Vertex program.

layout(location=0) in vec3 a_position;
layout(location=1) in vec2 a_textureCoordinates;
layout(location=2) in vec2 a_color;
layout(location=3) in vec3 a_normal;
layout(location=4) in vec3 a_tangent;
layout(location=5) in vec3 a_bitangent;

uniform mat4 viewProjectionMatrix;
uniform mat4 transform;

out vec3 position;
out vec2 texcoord;
out mat3 tangentBasis;

void main()
{
    position = vec3(transform * vec4(a_position, 1.0));
    texcoord = vec2(a_textureCoordinates.x, 1.0 - a_textureCoordinates.y);

    // Pass tangent space basis vectors (for normal mapping).
    tangentBasis = mat3(transform) * mat3(tangent, bitangent, normal);

    gl_Position = viewProjectionMatrix * transform * vec4(a_position, 1.0);
}
