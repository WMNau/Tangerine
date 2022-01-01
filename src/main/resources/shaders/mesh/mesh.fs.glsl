#version 400 core

#define NR_POINT_LIGHTS 4

struct Material
{
    sampler2D diffuse;
    sampler2D specular;
    float shininess;
};

struct DirectionalLight
{
    vec3 direction;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

struct PointLight
{
    vec3 position;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    float constant;
    float linear;
    float quadratic;
};

struct SpotLight
{
    vec3 position;
    vec3 direction;
    float cutoff;
    float outerCutoff;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    float constant;
    float linear;
    float quadratic;
};

in vec3 vNormal;
in vec3 vWorldPosition;
in vec2 vUvs;

uniform sampler2D textureSampler;

uniform Material uMaterial;
uniform DirectionalLight uDirectionalLight;
uniform PointLight uPointLights[NR_POINT_LIGHTS];
uniform SpotLight uSpotLight;

uniform vec3 uViewPosition;

out vec4 fColor;

vec3 CalculateDirectionalLight(DirectionalLight light, vec3 normal, vec3 viewDirection);
vec3 CalculatePointLight(PointLight light, vec3 normal, vec3 viewDirection);
vec3 CalculateSpotLight(SpotLight light, vec3 normal, vec3 viewDirection);

void main()
{
    vec3 normal = normalize(vNormal);
    vec3 viewDirection = normalize(uViewPosition - vWorldPosition);
    vec3 result = CalculateDirectionalLight(uDirectionalLight, normal, viewDirection);
    for (int i = 0; i < NR_POINT_LIGHTS; i++)
    {
        result += CalculatePointLight(uPointLights[i], normal, viewDirection);
    }
    result += CalculateSpotLight(uSpotLight, normal, viewDirection);
    fColor = vec4(result, 1.0f);
}

vec3 CalculateDirectionalLight(DirectionalLight light, vec3 normal, vec3 viewDirection)
{
    vec3 lightDirection = normalize(-light.direction);
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 reflectDirection = reflect(-lightDirection, normal);
    float spec = pow(max(dot(viewDirection, reflectDirection), 0.0f), uMaterial.shininess);
    vec4 diffuseTexture = texture(uMaterial.diffuse, vUvs);
    if (diffuseTexture.a < 0.01f)
    {
        discard;
    }
    vec3 ambient  = light.ambient  * diffuseTexture.rgb;
    vec3 diffuse  = light.diffuse  * diff * diffuseTexture.rgb;
    vec4 specularTexture = texture(uMaterial.specular, vUvs);
    if(specularTexture.a < 0.01f)
    {
        discard;
    }
    vec3 specular = light.specular * spec * specularTexture.rgb;
    return (ambient + diffuse + specular);
}

vec3 CalculatePointLight(PointLight light, vec3 normal, vec3 viewDirection)
{
    vec3 lightDirection = normalize(light.position - vWorldPosition);
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 reflectDirection = reflect(-lightDirection, normal);
    float spec = pow(max(dot(viewDirection, reflectDirection), 0.0f), uMaterial.shininess);
    float distance = length(light.position - vWorldPosition);
    float attenuation = 1.0f / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    vec4 diffuseTexture = texture(uMaterial.diffuse, vUvs);
    if (diffuseTexture.a < 0.01f)
    {
        discard;
    }
    vec3 ambient  = light.ambient  * diffuseTexture.rgb;
    vec3 diffuse  = light.diffuse  * diff * diffuseTexture.rgb;
    vec4 specularTexture = texture(uMaterial.specular, vUvs);
    if(specularTexture.a < 0.01f)
    {
        discard;
    }
    vec3 specular = light.specular * spec * specularTexture.rgb;
    ambient *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;
    return (ambient + diffuse + specular);
}

vec3 CalculateSpotLight(SpotLight light, vec3 normal, vec3 viewDirection)
{
    vec3 lightDirection = normalize(light.position - vWorldPosition);
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 reflectDirection = reflect(-lightDirection, normal);
    float spec = pow(max(dot(viewDirection, reflectDirection), 0.0f), uMaterial.shininess);
    float distance = length(light.position - vWorldPosition);
    float attenuation = 1.0f / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    float theta = dot(lightDirection, normalize(-light.direction));
    float epsilon = light.cutoff - light.outerCutoff;
    float intensity = clamp((theta - light.outerCutoff) / epsilon, 0.0f, 1.0f);
    vec4 diffuseTexture = texture(uMaterial.diffuse, vUvs);
    if (diffuseTexture.a < 0.01f)
    {
        discard;
    }
    vec3 ambient  = light.ambient  * diffuseTexture.rgb;
    vec3 diffuse  = light.diffuse  * diff * diffuseTexture.rgb;
    vec4 specularTexture = texture(uMaterial.specular, vUvs);
    if(specularTexture.a < 0.01f)
    {
        discard;
    }
    vec3 specular = light.specular * spec * specularTexture.rgb;
    ambient *= attenuation * intensity;
    diffuse *= attenuation * intensity;
    specular *= attenuation * intensity;
    return (ambient + diffuse + specular);
}
