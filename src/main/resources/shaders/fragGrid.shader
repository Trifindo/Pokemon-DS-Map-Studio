#version 430

out vec4 color;
uniform mat4 mv_matrix;
uniform mat4 proj_matrix;

void main(void)
{ color = vec4(1.0, 1.0, 1.0, 1.0);
}