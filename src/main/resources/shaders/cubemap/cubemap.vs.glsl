#version 400 core

in vec3 position;

out vec3 vUvs;

uniform mat4 uViewMatrix;
uniform mat4 uProjectionMatrix;

void main() {
    vec4 pos = uProjectionMatrix * uViewMatrix * vec4(position, 1.0f);
    gl_Position = pos.xyww;
    vUvs = position;
}
