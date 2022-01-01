#version 400 core

in vec2 position;
in vec2 uvs;

out vec2 vUvs;

void main() {
    gl_Position = vec4(position, 0.0f, 1.0f);
    vUvs = uvs;
}
