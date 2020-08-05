#version 430

in vec2 tc; // interpolated incoming texture coordinate
out vec4 color;
uniform mat4 mv_matrix;
uniform mat4 proj_matrix;
layout (binding=0) uniform sampler2D samp;

void main(void)
{ 
vec4 texel = texture(samp, tc);
if(texel.a < 0.9)
    discard;
color = texel;
}