#version 400 core

in vec2 vUvs;

uniform sampler2D uTextureSampler;

out vec4 fColor;

void main()
{
    fColor = texture(uTextureSampler, vUvs);
}
