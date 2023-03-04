#version 150

#moj_import <fog.glsl>

in vec3 vertexNormal;

out vec4 fragColor;

void main() {
    fragColor = vec4(vertexNormal, 0);
}
