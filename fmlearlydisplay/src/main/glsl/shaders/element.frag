#version 450 core

layout(set = 0, binding = 0) uniform sampler2D tex[16];

layout(push_constant) uniform PushConstants {
    vec2 screenSize;
    int rendertype;
    int textureNumber;
} pc;

layout(location = 0) in vec2 fTex;
layout(location = 1) in vec4 fColour;

layout(location = 0) out vec4 fragColor;

void main() {
    if (pc.rendertype == 0)
        fragColor = vec4(1.0, 1.0, 1.0, texture(tex[pc.textureNumber], fTex).r) * fColour;
    if (pc.rendertype == 1)
        fragColor = texture(tex[pc.textureNumber], fTex) * fColour;
    if (pc.rendertype == 2)
        fragColor = fColour;
}
