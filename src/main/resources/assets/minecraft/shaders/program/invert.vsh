#version 120

attribute vec4 Position;

uniform mat4 ProjMat;
uniform vec2 InSize;
uniform vec2 OutSize;

varying vec2 texCoord;

void main(){
    vec4 outPos = ProjMat * vec4(Position.xy, 0.0, 1.0);
    gl_Position = vec4(outPos.xy, 0.2, 1.0);

    vec2 sizeRatio = OutSize / InSize;
    texCoord = Position.xy / OutSize;
    texCoord.x = texCoord.x * sizeRatio.x;
    texCoord.y = texCoord.y * sizeRatio.y;
    texCoord.y = sizeRatio.y - texCoord.y;
}
