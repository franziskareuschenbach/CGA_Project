#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 position;
    vec2 texture;
    vec3 normal;
    vec3 toSpotLight;
} vertexData;

uniform sampler2D diff;
uniform sampler2D emit;
uniform sampler2D specular;
uniform float shininess;

//SpotLight
uniform vec3 mCycleSpotLightCol;
uniform vec3 mCycleSpotLightAttParam;
uniform vec2 mCycleSpotLightAngle;
uniform vec3 mCycleSpotLightDir;



uniform vec3 col;

//fragment shader output
out vec4 color;

vec3 diffSpec(vec3 normale, vec3 lightDir, vec3 viewDir, vec3 diff, vec3 spec, float shini){
    vec3 diffuse = diff * max(dot(normale,lightDir), 0.0f);     //Diffuse Farbe * Skalarprodukt von Normale und LichtPos, oder 0.0

    //Blinn-Phong
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float specular = pow(max(dot(normale, halfwayDir), 0.0f),16.0f);


    return diffuse + specular;
}

float attenuate(float length, vec3 attParam){ //Gamma Correction nachschauen
    return 1.0/(attParam.x + attParam.y * length + attParam.z * pow(length,2));
}

vec3 pointLightIntensity(vec3 lightColor, float length, vec3 attP){
    return lightColor * attenuate(length, attP);
}

vec3 spotLightIntensity(vec3 spotLightColor, float length, vec3 sp, vec3 spDir){
    float cosTheta = dot(sp, normalize(spDir));
    float cosPhi = cos(mCycleSpotLightAngle.x);
    float cosGamma = cos(mCycleSpotLightAngle.y);

    float intensity = (cosTheta - cosGamma)/(cosPhi - cosGamma);
    float cintensity = clamp(intensity, 0.0f, 1.0f);

    return spotLightColor * cintensity * attenuate(length, mCycleSpotLightAttParam);
}

void main(){
    vec3 normals = normalize(vertexData.normal);
    vec3 positions = normalize(vertexData.position);

    //SpotLight
    float spLength = length(vertexData.toSpotLight);
    vec3 sp = normalize(vertexData.toSpotLight);

    vec3 diffCol = texture(diff, vertexData.texture).rgb;
    vec3 emitCol = texture(emit, vertexData.texture).rgb;
    vec3 specCol = texture(specular, vertexData.texture).rgb;

    //Emissive Term
    vec3 emissive = emitCol * col;

    //Ambient Term
    //SpotLight
    emissive += diffSpec(normals, sp, positions, diffCol, specCol, shininess) * spotLightIntensity(mCycleSpotLightCol, spLength, sp, mCycleSpotLightDir);

    color = vec4(emissive,1.0);
}