#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 position;
    vec2 texture;
    vec3 normal;
    vec3 toRoomPointLight1;
    vec3 toRoomPointLight2;
    vec3 toRoomPointLight3;
} vertexData;

uniform sampler2D diff;
uniform sampler2D emit;
uniform sampler2D specular;
uniform float shininess;


//RoomLight1
uniform vec3 roomPoint1LightCol;
uniform vec3 roomPoint1LightAttParam;

//RoomLight2
uniform vec3 roomPoint2LightCol;
uniform vec3 roomPoint2LightAttParam;

//RoomLight3
uniform vec3 roomPoint3LightCol;
uniform vec3 roomPoint3LightAttParam;

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


void main(){
    vec3 normals = normalize(vertexData.normal);
    vec3 positions = normalize(vertexData.position);

    //RoomLight1
    float lpLength = length(vertexData.toRoomPointLight1);
    vec3 lp = normalize(vertexData.toRoomPointLight1);
    //RoomLight2
    float lp2Length = length(vertexData.toRoomPointLight2);
    vec3 lp2 = normalize(vertexData.toRoomPointLight2);
    //RoomLight3
    float lp3Length = length(vertexData.toRoomPointLight3);
    vec3 lp3 = normalize(vertexData.toRoomPointLight3);

    vec3 diffCol = texture(diff, vertexData.texture).rgb;
    vec3 emitCol = texture(emit, vertexData.texture).rgb;
    vec3 specCol = texture(specular, vertexData.texture).rgb;

    //Emissive Term
    vec3 emissive = emitCol * col;

    //Ambient Term
    //RoomLight1
    emissive += diffSpec(normals, lp, positions, diffCol, specCol, shininess) * pointLightIntensity(roomPoint1LightCol, lpLength, roomPoint1LightAttParam);
    //RoomLight2
    emissive += diffSpec(normals, lp2, positions, diffCol, specCol, shininess) * pointLightIntensity(roomPoint2LightCol, lp2Length, roomPoint2LightAttParam);
    //RoomLight3
    emissive += diffSpec(normals, lp3, positions, diffCol, specCol, shininess) * pointLightIntensity(roomPoint3LightCol, lp3Length, roomPoint3LightAttParam);


    color = vec4(emissive,1.0);
}