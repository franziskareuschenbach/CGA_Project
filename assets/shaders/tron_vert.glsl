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

uniform vec3 mCycleSpotLightPos;

out struct VertexData
{
    vec3 position;
    vec2 texture;
    vec3 normal;
    vec3 toSpotLight;
} vertexData;

void main(){
    mat4 modelView = view_matrix * model_matrix;
    vec4 pos = modelView * vec4(position, 1.0f);
    vec4 norm = inverse(transpose(modelView)) * vec4(normal, 0.0f);

    //SpotLight
    vec4 sLightPos = view_matrix * vec4(mCycleSpotLightPos,1.0f);
    vertexData.toSpotLight = (sLightPos - pos).xyz;

    gl_Position =projection_matrix * pos/*view_matrix * model_matrix * vec4(position, 1.0f)*/;

    vertexData.position = -pos.xyz; //toCamera
    vertexData.texture = texCoor * tcMultiplier;
    vertexData.normal = norm.xyz;
}
