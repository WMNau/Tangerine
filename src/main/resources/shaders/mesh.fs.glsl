#version 400 core

in vec4 vColor;
in vec2 vUvs;

out vec4 fColor;

uniform sampler2D textureSampler;

void main()
{
    fColor = texture(textureSampler, vUvs);
}
