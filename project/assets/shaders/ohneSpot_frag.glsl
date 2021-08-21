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
    vec3 toCornerLight;
    vec3 toCornerLight2;
    vec3 toCornerLight3;
    vec3 toCornerLight4;
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

//Corner1
uniform vec3 cornerLightCol;
uniform vec3 cornerLightAttParam;

//Corner2
uniform vec3 corner2LightCol;
uniform vec3 corner2LightAttParam;

//Corner3
uniform vec3 corner3LightCol;
uniform vec3 corner3LightAttParam;

//Corner4
uniform vec3 corner4LightCol;
uniform vec3 corner4LightAttParam;

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
    //Corner1
    float cpLength = length(vertexData.toCornerLight);
    vec3 cp = normalize(vertexData.toCornerLight);
    //Corner2
    float cpLength2 = length(vertexData.toCornerLight2);
    vec3 cp2 = normalize(vertexData.toCornerLight2);
    //Corner3
    float cpLength3 = length(vertexData.toCornerLight3);
    vec3 cp3 = normalize(vertexData.toCornerLight3);
    //Corner4
    float cpLength4 = length(vertexData.toCornerLight4);
    vec3 cp4 = normalize(vertexData.toCornerLight4);

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
    //Corner1
    //emissive += diffSpec(normals, cp, positions, diffCol, specCol, shininess) * pointLightIntensity(cornerLightCol, cpLength, cornerLightAttParam);
    //Corner2
    //emissive += diffSpec(normals, cp2, positions, diffCol, specCol, shininess) * pointLightIntensity(corner2LightCol, cpLength2, corner2LightAttParam);
    //Corner3
    //emissive += diffSpec(normals, cp3, positions, diffCol, specCol, shininess) * pointLightIntensity(corner3LightCol, cpLength3, corner3LightAttParam);
    //Corner4
    //emissive += diffSpec(normals, cp4, positions, diffCol, specCol, shininess) * pointLightIntensity(corner4LightCol, cpLength4, corner4LightAttParam);


    color = vec4(emissive,1.0);
}