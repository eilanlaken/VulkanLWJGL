#version 330

// attributes
layout(location = 0) in vec3 a_position;
//layout(location = 1) in vec4 a_color;
//layout(location = 2) in vec2 a_texCoord0;

//out vec4 colour;

void main(void){

    gl_Position = vec4(a_position,1.0);
    //colour = vec4(1,1,1,1);
}