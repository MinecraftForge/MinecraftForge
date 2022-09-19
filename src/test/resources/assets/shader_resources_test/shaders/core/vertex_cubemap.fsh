#version 150

#moj_import <fog.glsl>
#moj_import <shader_resources_test:cubemap_includes.glsl>

void main() {
    fragColor = vec4(vertexPos, 0);
}
