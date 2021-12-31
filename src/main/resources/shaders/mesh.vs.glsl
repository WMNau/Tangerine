#version 400 core

in vec3 position;
in vec2 uvs;

out vec4 vColor;
out vec2 vUvs;

void main()
{
    vec4 worldPosition = vec4(position, 1.0f);
    gl_Position = worldPosition;
    vColor = vec4(position.x + 0.5f, 1.0f, position.y + 0.5f, 1.0f);
    vUvs = uvs;
}
