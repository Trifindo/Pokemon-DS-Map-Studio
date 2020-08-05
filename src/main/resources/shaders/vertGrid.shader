#version 430

layout (location=0) in vec3 pos;

uniform mat4 mv_matrix;
uniform mat4 proj_matrix;

void main(void)
{ 
gl_Position = proj_matrix * mv_matrix * vec4(pos,1.0);
}