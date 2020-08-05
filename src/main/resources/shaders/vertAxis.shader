#version 430

layout (location=0) in vec3 pos;
layout (location=1) in vec3 col;

uniform mat4 mv_matrix;
uniform mat4 proj_matrix;

out vec4 axis_color;

void main(void)
{ 
gl_Position = proj_matrix * mv_matrix * vec4(pos, 1.0);
axis_color = vec4(col, 1.0);
}