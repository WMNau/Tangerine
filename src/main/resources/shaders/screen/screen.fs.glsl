#version 400 core

in vec2 vUvs;

uniform sampler2D textureSampler;

out vec4 fColor;

void main()
{
    fColor = texture(textureSampler, vUvs);
}
