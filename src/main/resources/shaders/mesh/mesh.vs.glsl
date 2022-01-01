#version 400 core

in vec3 position;
in vec3 normal;
in vec2 uvs;

uniform mat4 uModelMatrix;
uniform mat4 uViewMatrix;
uniform mat4 uProjectionMatrix;

out vec3 vNormal;
out vec3 vWorldPosition;
out vec2 vUvs;

void main()
{
    vec4 worldPosition = uModelMatrix * vec4(position, 1.0f);
    gl_Position = uProjectionMatrix * uViewMatrix * worldPosition;
    vNormal = mat3(uModelMatrix) * normal;
    vWorldPosition = worldPosition.xyz;
    vUvs = uvs;
}
