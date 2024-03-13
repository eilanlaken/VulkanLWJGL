#version 330

// attributes
layout(location = 0) in vec2 a_position;
layout(location = 1) in vec4 a_color;
//layout(location = 2) in vec2 a_texCoord0;

out vec4 color;

void main(void){

    gl_Position = vec4(a_position.x,a_position.y,0.0,1.0);
    color = a_color;
}