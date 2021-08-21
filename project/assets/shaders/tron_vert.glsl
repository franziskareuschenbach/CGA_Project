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

uniform vec3 mCyclePointLightPos;

uniform vec3 mCycleSpotLightPos;

uniform vec3 cornerLightPos;
uniform vec3 corner2LightPos;
uniform vec3 corner3LightPos;
uniform vec3 corner4LightPos;

out struct VertexData
{
    vec3 position;
    vec2 texture;
    vec3 normal;
    vec3 toSpotLight;
    vec3 toPointLight;
        vec3 toCornerLight;
        vec3 toCornerLight2;
        vec3 toCornerLight3;
        vec3 toCornerLight4;
} vertexData;

void main(){
    mat4 modelView = view_matrix * model_matrix;
    vec4 pos = modelView * vec4(position, 1.0f);
    vec4 norm = inverse(transpose(modelView)) * vec4(normal, 0.0f);

    //SpotLight
    vec4 sLightPos = view_matrix * vec4(mCycleSpotLightPos,1.0f);
    vertexData.toSpotLight = (sLightPos - pos).xyz;

    //PointLight
    vec4 lightPos = view_matrix * vec4(mCyclePointLightPos, 1.0f); //Pos PointLight im ViewSpace
    vertexData.toPointLight = (lightPos - pos).xyz;                 //Richtung der Lichtquelle im Camera Space

    //Corner1
    vec4 cLightPos = view_matrix * vec4(cornerLightPos, 1.0f);
    vertexData.toCornerLight = (cLightPos - pos).xyz;

    //Corner2
    vec4 cLightPos2 = view_matrix * vec4(corner2LightPos, 1.0f);
    vertexData.toCornerLight2 = (cLightPos2 - pos).xyz;

    //Corner3
    vec4 cLightPos3 = view_matrix * vec4(corner3LightPos, 1.0f);
    vertexData.toCornerLight3 = (cLightPos3 - pos).xyz;

    //Corner4
    vec4 cLightPos4 = view_matrix * vec4(corner4LightPos, 1.0f);
    vertexData.toCornerLight4 = (cLightPos4 - pos).xyz;

    gl_Position =projection_matrix * pos/*view_matrix * model_matrix * vec4(position, 1.0f)*/;

    vertexData.position = -pos.xyz; //toCamera
    vertexData.texture = texCoor * tcMultiplier;
    vertexData.normal = norm.xyz;
}
