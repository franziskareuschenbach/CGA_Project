#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoor;
layout(location = 2) in vec3 normal;

//uniforms
// translation object to world
uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;
uniform vec2 tcMultiplier;

uniform vec3 roomPoint1LightPos;
uniform vec3 roomPoint2LightPos;
uniform vec3 roomPoint3LightPos;

uniform vec3 bodySpotLightPos;


out struct VertexData
{
    vec3 position;
    vec2 texture;
    vec3 normal;
    vec3 toSpotLight;
    vec3 toRoomPointLight1;
    vec3 toRoomPointLight2;
    vec3 toRoomPointLight3;
} vertexData;

void main(){
    mat4 modelView = view_matrix * model_matrix;
    vec4 pos = modelView * vec4(position, 1.0f);
    vec4 norm = inverse(transpose(modelView)) * vec4(normal, 0.0f);

    //SpotLight
    vec4 sLightPos = view_matrix * vec4(bodySpotLightPos,1.0f);
    vertexData.toSpotLight = (sLightPos - pos).xyz;

    //RoomLight1
    vec4 rLightPos1 = view_matrix * vec4(roomPoint1LightPos, 1.0f); //Pos PointLight im ViewSpace
    vertexData.toRoomPointLight1 = (rLightPos1 - pos).xyz;                 //Richtung der Lichtquelle im Camera Space

    //RoomLight2
    vec4 rLightPos2 = view_matrix * vec4(roomPoint2LightPos, 1.0f); //Pos PointLight im ViewSpace
    vertexData.toRoomPointLight2 = (rLightPos2 - pos).xyz;                 //Richtung der Lichtquelle im Camera Space

    //RoomLight3
    vec4 rLightPos3 = view_matrix * vec4(roomPoint3LightPos, 1.0f); //Pos PointLight im ViewSpace
    vertexData.toRoomPointLight3 = (rLightPos3 - pos).xyz;                 //Richtung der Lichtquelle im Camera Space


    gl_Position =projection_matrix * pos/*view_matrix * model_matrix * vec4(position, 1.0f)*/;

    vertexData.position = -pos.xyz; //toCamera
    vertexData.texture = texCoor * tcMultiplier;
    vertexData.normal = norm.xyz;
}
