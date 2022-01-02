#version 400 core

in vec3 vUvs;

uniform samplerCube uCubeSampler;

out vec4 fColor;

void main()
{
    fColor = texture(uCubeSampler, vUvs);
}
